package server;

import java.sql.*;

public class DBController {
    private Connection conn;
    private PreparedStatement statement;
    private ResultSet rs;

    public void start() {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:gbchat.db");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }

    public void stop(){
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getNickByLoginPass(String login, String pass) {
        String nick=null;
        start();
        try {
            statement = conn.prepareStatement("SELECT * FROM Auth WHERE login=? AND pass=?");
            statement.setString(1, login);
            statement.setString(2, pass);
            rs=statement.executeQuery();
            if (rs.next()) {
                nick = rs.getString("nick");
                System.out.println(nick);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        stop();
        return nick;
    }

    public void changeNick (String originalNick, String newNick){
        start();
        try {
            statement = conn.prepareStatement("UPDATE Auth SET nick = ? WHERE nick=?");
            statement.setString(1, newNick);
            statement.setString(2, originalNick);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        stop();
    }

    public boolean isNickBusy(String nick) {
        start();
        try {
            statement = conn.prepareStatement("SELECT * FROM Auth WHERE nick=?");
            statement.setString(1, nick);
            return statement.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        stop();
        return false;
    }

    public boolean registration(String login, String password, String nick) {
        start();
        try {
            statement = conn.prepareStatement("SELECT * FROM Auth WHERE login=? OR nick=?");
            statement.setString(1, login);
            statement.setString(2, nick);
            if(statement.executeQuery().next()) return false;
            statement = conn.prepareStatement("INSERT INTO Auth (login, pass, nick) VALUES (?, ?, ?)");
            statement.setString(1, login);
            statement.setString(2, password);
            statement.setString(3, nick);
            return (statement.executeUpdate()!=0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        stop();
        return false;
    }
}
