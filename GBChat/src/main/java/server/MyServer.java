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
    private ExecutorService clientExecutorService;
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
            initServer();
            authService.start();
            clientsConnecting(server);
        } catch (IOException e) {
            logger.error("Ошибка в работе сервера");
            e.printStackTrace();
        } finally {
            stopServer();
        }
    }

    private void initServer() {
        clientExecutorService = Executors.newCachedThreadPool();
        dbController = new DBController();
        authService = new DBAuthService(dbController);
        clients = new HashMap<>();
    }

    private void clientsConnecting(ServerSocket server) throws IOException {
        while (true) {
            logger.info("Сервер ожидает подключения");
            Socket socket = server.accept();
            logger.info("Клиент подключился");
            clientExecutorService.execute(() -> new ClientHandler(this, socket));
        }
    }

    private void stopServer() {
        if (authService != null) {
            authService.stop();
            clientExecutorService.shutdown();
        }
    }

    public synchronized void broadcastMessage(String message) {
        for (ClientHandler o : clients.values()) {
            o.sendMessage(message);
        }
    }

    public synchronized void sendMessageToCertainClient(String clientNick, String message) {
        for (ClientHandler o : clients.values()) {
            if (o.getName().equals(clientNick)){
                o.sendMessage(message);
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
        broadcastMessage("/clients"+clientList);
    }
}
