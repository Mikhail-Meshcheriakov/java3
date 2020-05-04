package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class ClientHandler {
    private MyServer myServer;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private String name;

    public String getName() {
        return name;
    }

    public ClientHandler(MyServer myServer, Socket socket) {
        try {
            this.myServer = myServer;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.name = "";
            new Thread(() -> {
                try {
                    authentication();
                    readMessages();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException("Проблемы при создании обработчика клиента");
        }
    }

    public void authentication() throws IOException {
        while (true) {
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    closeConnection();
                }
            };
            Timer timer = new Timer();
            timer.schedule(timerTask, 120000);
            String str = in.readUTF();
            String nick = null;
            timer.cancel();
            if (str.startsWith("/auth")) {
                String[] parts = str.split("\\s");
                try {
                    myServer.getAuthService().connectDB();
                    nick = myServer.getAuthService().getNickByLoginPass(parts[1], parts[2]);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } finally {
                    myServer.getAuthService().disconnectDB();
                }
                if (nick != null) {
                    if (!myServer.isNickBusy(nick)) {
                        sendMsg("/authok " + nick);
                        name = nick;
                        myServer.broadcastMsg(name + " зашел в чат");
                        myServer.subscribe(this);
                        return;
                    } else {
                        sendMsg("Учетная запись уже используется");
                    }
                } else {
                    sendMsg("Неверные логин/пароль");
                }
            }
        }
    }

    public void readMessages() throws IOException {
        while (true) {
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
            } else if (strFromClient.startsWith("/cn")) {
                String nickNew = strFromClient.substring(4);
                try {
                    myServer.getAuthService().connectDB();
                    if (myServer.getAuthService().changeNick(name, nickNew)) {
                        myServer.broadcastMsg(name, "Пользователь " + name + " сменил ник на: " + nickNew);
                        myServer.unsubscribe(this);
                        name = nickNew;
                        myServer.subscribe(this);
                    } else {
                        myServer.sendMsgToClient(name, name, "Этот ник занят");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    myServer.getAuthService().disconnectDB();
                }
            } else {
                myServer.broadcastMsg(name, strFromClient);
            }
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
