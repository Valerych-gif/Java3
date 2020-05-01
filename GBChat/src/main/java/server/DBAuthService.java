package server;

public class DBAuthService implements AuthService {

    private DBController dbController;

    public DBAuthService(DBController dbController) {
        this.dbController = dbController;
    }

    @Override
    public void start() {
        System.out.println("Сервис аутентификации запущен");
    }

    @Override
    public void stop() {
        System.out.println("Сервис аутентификации остановлен");
    }

    @Override
    public String getNickByLoginPass(String login, String pass) {
        return dbController.getNickByLoginPass(login, pass);
    }

}
