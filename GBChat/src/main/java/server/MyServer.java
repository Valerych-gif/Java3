package server;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyServer {
    private final int PORT = 8189;

    private Map<String, ClientHandler> clients;
    private AuthService authService;
    private static final Logger logger = LogManager.getLogger(MyServer.class);

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
            ExecutorService executorService = Executors.newCachedThreadPool();

            while (true) {
                logger.info("Сервер ожидает подключения");
                Socket socket = server.accept();
                logger.info("Клиент подключился");
                executorService.execute(() -> {
                    new ClientHandler(this, socket);
                });
            }
        } catch (IOException e) {
            logger.error("Ошибка в работе сервера");
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
