package server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BaseAuthService implements AuthService {
    private List<Entry> entries;
    private Connection connection;
    private Statement statement;
    private PreparedStatement preparedStatement;

    public BaseAuthService() {
        entries = new ArrayList<>();
        try {
            connectDB();
            ResultSet resultSet = statement.executeQuery("SELECT login, password, nick FROM users");
            while (resultSet.next()) {
                entries.add(new Entry(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3)));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        System.out.println("Сервис аутентификации запущен");
    }

    @Override
    public void stop() {
        disconnectDB();
        System.out.println("Сервис аутентификации остановлен");
    }

    @Override
    public void connectDB() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:chat.db");
        statement = connection.createStatement();
    }

    @Override
    public boolean changeNick(String nickOld, String nickNew) throws SQLException {
        preparedStatement = connection.prepareStatement("SELECT * From users WHERE nick = ?");
        preparedStatement.setString(1, nickNew);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            return false;
        }

        preparedStatement = connection.prepareStatement("UPDATE users SET nick = ? WHERE nick = ?");
        preparedStatement.setString(1, nickNew);
        preparedStatement.setString(2, nickOld);
        return preparedStatement.executeUpdate() != 0;
    }


    @Override
    public void disconnectDB() {
        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getNickByLoginPass(String login, String pass) {
        try {
            preparedStatement = connection.prepareStatement("SELECT nick FROM users WHERE login = ? AND password = ?");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, pass);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return resultSet.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class Entry {
        private String login;
        private String pass;
        private String nick;

        public Entry(String login, String pass, String nick) {
            this.login = login;
            this.pass = pass;
            this.nick = nick;
        }
    }
}
