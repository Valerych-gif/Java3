package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DBAuthService implements AuthService {

    private DBController dbController;
    private static final Logger logger = LogManager.getLogger(MyServer.class);

    public DBAuthService(DBController dbController) {
        this.dbController = dbController;
    }

    @Override
    public void start() {
        logger.info("Сервис аутентификации запущен");
    }

    @Override
    public void stop() {
        logger.info("Сервис аутентификации остановлен");
    }

    @Override
    public String getNickByLoginPass(String login, String pass) {
        return dbController.getNickByLoginPass(login, pass);
    }

}
