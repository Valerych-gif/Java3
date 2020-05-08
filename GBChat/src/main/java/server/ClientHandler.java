package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private MyServer myServer;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private AuthService authService;
    private DBController dbController;
    private static final int AUTH_TIMER = 120_000;

    private String name;

    public String getName() {
        return name;
    }

    public ClientHandler(MyServer myServer, Socket socket) {
        try {
            this.myServer = myServer;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.name = null;

            this.authService = myServer.getAuthService();
            this.dbController = myServer.getDbController();


            new Thread(() -> {
                try {
                    Thread.sleep(AUTH_TIMER);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (name == null) {
                    sendMsg("/end");
                    System.out.println("Клиент отключен от соединения по тайм-ауту");
                    closeConnection();
                }
            }).start();


            try {
                authentication();
                readMessages();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }

        } catch (IOException e) {
            throw new RuntimeException("Проблемы при создании обработчика клиента");
        }
    }

    public void authentication() throws IOException {
        while (true) {
            String str = in.readUTF();
            if (str.startsWith("/auth")) {
                String[] parts = str.split("\\s");
                if (parts.length == 3) {
                    String login = parts[1];
                    String password = parts[2];
                    String nick = authService.getNickByLoginPass(login, password);
                    if (nick != null) {
                        if (!myServer.isNickBusy(nick)) {
                            sendMsg("/authok " + nick);
                            name = nick;
                            myServer.broadcastMsg(name + " зашел в чат");
                            myServer.subscribe(this);
                            return;
                        } else {
                            sendMsg("Учетная запись уже используется");
                        }
                    } else {
                        sendMsg("Неверные логин/пароль");
                    }
                }
            }

            if (str.startsWith("/reg")) {
                String[] parts = str.split("\\s");
                if (parts.length == 4) {
                    String login = parts[1];
                    String password = parts[2];
                    String nick = parts[3];
                    if (dbController.registration(login, password, nick)) {
                        sendMsg("/authok " + nick);
                        name = nick;
                        myServer.broadcastMsg(name + " зашел в чат");
                        myServer.subscribe(this);
                        return;
                    } else {
                        sendMsg("/regfail");
                    }

                }
            }

        }
    }

    public void readMessages() throws IOException {
        while (true) {
            String strFromClient = in.readUTF();
            System.out.println("от " + name + ": " + strFromClient);

            if (strFromClient.equals("/end")) {
                closeConnection();
                return;
            }

            if (strFromClient.startsWith("/w")) {
                sendMessageToCertainClient(strFromClient);
            } else {
                myServer.broadcastMsg(name + ": " + strFromClient);
            }

            if (strFromClient.startsWith("/chnick")) {
                changeNick(strFromClient);
            }

        }
    }

    private void changeNick(String str) {
        String[] parts = str.split("\\s");
        String nick;

        if (parts.length > 1) {
            nick = parts[1];
        } else return;

        if (!dbController.isNickBusy(nick)) {
            dbController.changeNick(name, nick);
            name = nick;
            sendMsg("/newnick " + name);
            myServer.refreshClientsList();
        } else {
            sendMsg("Данный ник уже занят");
        }
    }

    private void sendMessageToCertainClient(String str) {
        String[] parts = str.split("\\s");
        String nick = parts[1];
        String message = name + "->" + nick + ": " + parts[2];
        myServer.sendMessageToCertainClient(nick, message);
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        myServer.unsubscribe(this);
        myServer.broadcastMsg(name + " вышел из чата");
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
