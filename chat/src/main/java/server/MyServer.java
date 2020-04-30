package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class MyServer {
    private final int PORT = 8189;

    private Map<String, ClientHandler> clients;
    //    private List<ClientHandler> clients;
    private AuthService authService;

    public AuthService getAuthService() {
        return authService;
    }

    public MyServer() {
        try (ServerSocket server = new ServerSocket(PORT)) {
            authService = new BaseAuthService();
            authService.start();
            clients = new HashMap<>();

            while (true) {
                System.out.println("Сервер ожидает подключения");
                Socket socket = server.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            System.out.println("Ошибка в работе сервера");
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

    public synchronized void broadcastMsg(String from, String msg) {
        broadcastMsg(formatMessage(from, msg));
    }

    public synchronized void sendMsgToClient(String from, String to, String msg) {
        if (clients.containsKey(to)) {
            clients.get(to).sendMsg(formatMessage(from, msg));
        }
    }


    //Отправка персонального сообщения
//    public synchronized void privateMsg(String msg, String nick) {
//        for (ClientHandler o : clients) {
//            if (o.getName().equals(nick)) {
//                o.sendMsg(msg);
//                break;
//            }
//        }
//    }

    //Отправка списка пользователей
//    public synchronized void broadcastUserList() {
//        StringBuilder msg = new StringBuilder("/clients ");
//        for (ClientHandler o : clients) {
//            msg.append(o.getName()).append(" ");
//        }
//        broadcastMsg(msg.toString());
//    }

    public synchronized void unsubscribe(ClientHandler o) {
        clients.remove(o.getName());
        broadcastClients();
    }

    public synchronized void subscribe(ClientHandler o) {
        clients.put(o.getName(), o);
        broadcastClients();
    }

    private String formatMessage(String from, String msg) {
        return from + ": " + msg;
    }

    public synchronized void broadcastClients() {
        StringBuilder builder = new StringBuilder("/clients ");
        for (String nick : clients.keySet()) {
            builder.append(nick).append(' ');
        }
        broadcastMsg(builder.toString());
    }
}
