package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MyServer {
    private final int PORT = 8189;

    private Map<String, ClientHandler> clients;
    private AuthService authService;

    private DBController dbController;

    public AuthService getAuthService() {
        return authService;
    }

    public DBController getDbController() {
        return dbController;
    }

    public MyServer() {
        try (ServerSocket server = new ServerSocket(PORT)) {
            dbController = new DBController();
            authService = new DBAuthService(dbController);
            authService.start();
            clients = new HashMap<>();

            while (true) {
                System.out.println("Сервер ожидает подключения");
                Socket socket = server.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            System.out.println("Ошибка в работе сервера");
            e.printStackTrace();
        } finally {
            if (authService != null) {
                authService.stop();
            }
        }
    }

    public synchronized boolean isNickBusy(String nick) {
        return clients.containsKey(nick);
    }

    public synchronized void broadcastMsg(String msg) {
        for (ClientHandler o : clients.values()) {
            o.sendMsg(msg);
        }
    }

    public synchronized void sendMessageToCertainClient(String nick, String msg) {
        for (ClientHandler o : clients.values()) {
            if (o.getName().equals(nick)){
                o.sendMsg(msg);
            }
        }
    }

    public synchronized void unsubscribe(ClientHandler o) {
        clients.remove(o.getName());
        refreshClientsList();
    }

    public synchronized void subscribe(ClientHandler o) {
        clients.put(o.getName(), o);
        refreshClientsList();
    }

    public void refreshClientsList() {
        StringBuilder clientList= new StringBuilder();
        for (ClientHandler client : clients.values()) {
            clientList.append(' ').append(client.getName());
        }
        broadcastMsg("/clients"+clientList);
    }
}
