package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private static final boolean KICK_INACTIVE_CLIENTS = true;
    private static final int AUTH_TIMER = 120_000;
    private MyServer myServer;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private AuthService authService;
    private DBController dbController;
    private String nickFromAuthDB;
    private String name;
    private Logger logger = LogManager.getLogger(ClientHandler.class);

    public String getName() {
        return name;
    }

    public ClientHandler(MyServer myServer, Socket socket) {
        try {
            initClientHandler(myServer, socket);
            startTimerForKickClientWithoutAuth();

            try {
                authOrRegCheck();
                readMessages();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }

        } catch (IOException e) {
            logger.error("Проблемы при создании обработчика клиента", e);
        }
    }

    private void initClientHandler(MyServer myServer, Socket socket) throws IOException {
        this.myServer = myServer;
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        this.name = null;
        this.authService = myServer.getAuthService();
        this.dbController = myServer.getDbController();
    }

    private void startTimerForKickClientWithoutAuth() {
        if (!KICK_INACTIVE_CLIENTS) return;
        new Thread(() -> {
            try {
                Thread.sleep(AUTH_TIMER);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (name == null) {
                sendMessage("/end");
                logger.info("Клиент отключен от соединения по тайм-ауту");
                closeConnection();
            }
        }).start();
    }

    private void authOrRegCheck() throws IOException {
        while (true) {
            String str = in.readUTF();
            if (str.startsWith("/auth")) {
                if (authCheck(str)) return;
            }
            if (str.startsWith("/reg")) {
                if (regCheck(str)) return;
            }
        }
    }

    private boolean authCheck(String str) {
        logger.info("Получен запрос на авторизацию");
        String[] parts = str.split("\\s");
        if (parts.length == 3) {
            String login = parts[1];
            String password = parts[2];
            if (checkNickByLoginAndPass(login, password)) {
                connectClient();
                return true;
            } else {
                sendWrongLoginOrPassMessage();
                return false;
            }
        } else {
            logger.error("Неверное количество параметров при авторизации");
            return false;
        }
    }

    private boolean checkNickByLoginAndPass(String login, String password) {
        nickFromAuthDB = authService.getNickByLoginPass(login, password);
        return nickFromAuthDB != null;
    }

    private void sendWrongLoginOrPassMessage() {
        logger.info("Пользователь ввел неверный логин или пароль");
        sendMessage("Неверные логин/пароль");
    }

    private void connectClient() {
        name = nickFromAuthDB;
        myServer.subscribe(this);
        sendMessage("/authok " + name);
        myServer.broadcastMessage(name + " зашел в чат");
        logger.info("Пользователь авторизовался под ником '{}'", name);
    }

    private boolean regCheck(String str) {
        logger.info("Получен запрос на регистрацию");
        String[] parts = str.split("\\s");
        if (parts.length == 4) {
            String login = parts[1];
            String password = parts[2];
            String nick = parts[3];
            if (!(dbController.isLoginBusy(login)||dbController.isNickBusy(nick))) {
                regClient(login, password, nick);
                return true;
            } else {
                logger.info("Неудачная попытка регистрации. Пользователю отправлено сообщение '/regfail'");
                sendMessage("/regfail");
            }
        }
        return false;
    }

    private void regClient(String login, String password, String nick) {
        dbController.registration(login, password, nick);
        name = nick;
        myServer.subscribe(this);
        sendMessage("/authok " + name);
        myServer.broadcastMessage(name + " зашел в чат");
        logger.info("Отправлено сообщение формата '/authok {}'", name);
    }

    private void readMessages() throws IOException {
        while (true) {
            String strFromClient = in.readUTF();

            if (strFromClient.equals("/end")) {
                closeConnection();
                return;
            }

            if (strFromClient.startsWith("/w")) {
                sendMessageToCertainClient(strFromClient);
                return;
            }

            if (strFromClient.startsWith("/chnick")) {
                changeNick(strFromClient);
                return;
            }

            logger.info("Получено сообщение от " + name + ": " + strFromClient);
            myServer.broadcastMessage(name + ": " + strFromClient);

        }
    }

    private void changeNick(String str) {
        String[] parts = str.split("\\s");
        String nick;

        if (parts.length > 1) {
            nick = parts[1];
        } else return;

        if (!dbController.isNickBusy(nick)) {
            logger.info("Пользователь {} запросил смену ника на ник {}", name, nick);
            dbController.changeNick(name, nick);
            name = nick;
            sendMessage("/newnick " + name);
            myServer.refreshClientsList();
        } else {
            logger.info("Пользователю {} не удалось сменить ник", name);
            sendMessage("Данный ник уже занят");
        }
    }

    private void sendMessageToCertainClient(String str) {
        logger.info("Пользователь {} отправил личное сообщение другому пользователю", name);
        String[] parts = str.split("\\s");
        String nick = parts[1];
        String message = name + "->" + nick + ": " + parts[2];
        myServer.sendMessageToCertainClient(nick, message);
    }

    public void sendMessage(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        myServer.unsubscribe(this);
        myServer.broadcastMessage(name + " вышел из чата");
        logger.info(name + " вышел из чата");
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
