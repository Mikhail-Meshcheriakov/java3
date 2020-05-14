package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class BaseAuthService implements AuthService {
    public static final String JDBC_DRIVER = "org.sqlite.JDBC";
    public static final String DATABASE_URL = "jdbc:sqlite:chat.db";
    private Connection connection;
    private PreparedStatement psChangeNick;
    private PreparedStatement psGetNickByLoginPass;
    private static final Logger LOGGER = LogManager.getLogger(BaseAuthService.class);

    @Override
    public void start() {
        LOGGER.info("Сервис аутентификации запущен");
    }

    @Override
    public void stop() {
        LOGGER.info("Сервис аутентификации остановлен");
    }

    @Override
    public void connectDB() throws ClassNotFoundException, SQLException {
        Class.forName(JDBC_DRIVER);
        connection = DriverManager.getConnection(DATABASE_URL);
        LOGGER.info("Соединение с БД установлено.");
        psChangeNick = connection.prepareStatement("UPDATE users SET nick = ? WHERE nick = ? AND NOT EXISTS (SELECT 1 FROM users WHERE nick = ?)");
        psGetNickByLoginPass = connection.prepareStatement("SELECT nick FROM users WHERE login = ? AND password = ?");
    }

    @Override
    public boolean changeNick(String nickOld, String nickNew) throws SQLException {
        psChangeNick.setString(1, nickNew);
        psChangeNick.setString(2, nickOld);
        psChangeNick.setString(3, nickNew);
        return psChangeNick.executeUpdate() != 0;
    }


    @Override
    public void disconnectDB() {
        if (psChangeNick != null) {
            try {
                psChangeNick.close();
            } catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        if (psGetNickByLoginPass != null) {
            try {
                psGetNickByLoginPass.close();
            } catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        LOGGER.info("Соединение с БД завершено.");
    }

    @Override
    public String getNickByLoginPass(String login, String pass) {
        try {
            psGetNickByLoginPass.setString(1, login);
            psGetNickByLoginPass.setString(2, pass);
            ResultSet resultSet = psGetNickByLoginPass.executeQuery();
            while (resultSet.next()) {
                return resultSet.getString(1);
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }
}
