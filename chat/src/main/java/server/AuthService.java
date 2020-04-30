package server;

import java.sql.SQLException;

public interface AuthService {
    void start();

    String getNickByLoginPass(String login, String pass);

    void stop();

    void connectDB() throws ClassNotFoundException, SQLException;

    void  disconnectDB();

    boolean changeNick(String nickOld, String nickNew) throws SQLException;
}
