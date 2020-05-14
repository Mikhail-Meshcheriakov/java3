package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class ClientHandler implements Runnable{
    private MyServer myServer;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private static final Logger LOGGER = LogManager.getLogger(ClientHandler.class);

    private String name;

    public String getName() {
        return name;
    }

    @Override
    public void run() {
        try {
            authentication();
            readMessages();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            closeConnection();
            LOGGER.info("Клиент {} отключился.", socket.getRemoteSocketAddress());
        }
    }

    public ClientHandler(MyServer myServer, Socket socket) {
        try {
            this.myServer = myServer;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.name = "";
            LOGGER.info("Клиент {} подключился.", socket.getRemoteSocketAddress());
        } catch (IOException e) {
            LOGGER.error("Проблемы при создании обработчика клиента.", e);
        }
    }

    public void authentication() throws IOException {
        while (true) {
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    closeConnection();
                    LOGGER.info("Отключение клиента {} по таймауту.", socket.getRemoteSocketAddress());
                }
            };
            Timer timer = new Timer();
            timer.schedule(timerTask, 120000);
            String str = in.readUTF();
            if (str.equals("/end")) {
                Thread.currentThread().interrupt();
                return;
            }
            String nick = null;
            timer.cancel();
            if (str.startsWith("/auth")) {
                String[] parts = str.split("\\s");
                try {
                    myServer.getAuthService().connectDB();
                    nick = myServer.getAuthService().getNickByLoginPass(parts[1], parts[2]);
                } catch (ClassNotFoundException | SQLException e) {
                    LOGGER.error(e.getMessage(), e);
//                    e.printStackTrace();
                } finally {
                    myServer.getAuthService().disconnectDB();
                }
                if (nick != null) {
                    if (!myServer.isNickBusy(nick)) {
                        sendMsg("/authok " + nick);
                        name = nick;
                        myServer.broadcastMsg(name + " зашел в чат");
                        myServer.subscribe(this);
                        LOGGER.info("Пользователь {} успешно авторизовался на клиенте {}", name, socket.getRemoteSocketAddress());
                        return;
                    } else {
                        LOGGER.info("Неудачная попытка авторизации на клиенте {}. Учетная запись уже используется.", socket.getRemoteSocketAddress());
                        sendMsg("Учетная запись уже используется");
                    }
                } else {
                    LOGGER.info("Неудачная попытка авторизации на клиенте {}. Неверные логин/пароль.", socket.getRemoteSocketAddress());
                    sendMsg("Неверные логин/пароль");
                }
            }
        }
    }

    public void readMessages() throws IOException {
        while (!Thread.currentThread().isInterrupted()) {
            String strFromClient = in.readUTF();
            System.out.println("от " + name + ": " + strFromClient);
            if (strFromClient.equals("/end")) {
                myServer.unsubscribe(this);
                myServer.broadcastMsg(name + " вышел из чата");
                return;
            }
            //Обработка персонального сообщения
            if (strFromClient.startsWith("/w")) {
                String[] tokens = strFromClient.split("\\s");
                String nick = tokens[1];
                String msg = strFromClient.substring(4 + nick.length());
                myServer.sendMsgToClient(name, nick, msg);
                LOGGER.info("Пользователь {} отправил личное сообщение пользователю {}", name, nick);
            } else if (strFromClient.startsWith("/cn")) {
                String nickNew = strFromClient.substring(4);
                try {
                    myServer.getAuthService().connectDB();
                    if (myServer.getAuthService().changeNick(name, nickNew)) {
                        myServer.broadcastMsg(name, "Пользователь " + name + " сменил ник на: " + nickNew);
                        LOGGER.info("Пользователь {} сменил ник на {}.", name, nickNew);
                        myServer.unsubscribe(this);
                        name = nickNew;
                        sendMsg("/changenick " + nickNew);
                        myServer.subscribe(this);
                    } else {
                        LOGGER.info("Неудачная попытка смены ника {} на {} (ник занят).", name, nickNew);
                        myServer.sendMsgToClient(name, name, "Этот ник занят");
                    }
                } catch (SQLException e) {
                    LOGGER.error(e.getMessage(), e);
                } catch (ClassNotFoundException e) {
                    LOGGER.error(e.getMessage(), e);
                } finally {
                    myServer.getAuthService().disconnectDB();
                }
            } else {
                LOGGER.info("Пользователь {} отправил многоадресное сообщение.", name);
                myServer.broadcastMsg(name, strFromClient);
            }
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void closeConnection() {
        try {
            in.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        try {
            out.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        try {
            socket.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
