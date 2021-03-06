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
    private static final int PORT = 8189;

    private Map<String, ClientHandler> clients;
    private AuthService authService;
    private static final Logger LOGGER = LogManager.getLogger(MyServer.class);

    public AuthService getAuthService() {
        return authService;
    }

    public MyServer() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        try (ServerSocket server = new ServerSocket(PORT)) {
            authService = new BaseAuthService();
            authService.start();
            clients = new HashMap<>();

            while (true) {
                LOGGER.info("Сервер ожидает подключения");
                Socket socket = server.accept();
                executorService.execute(new ClientHandler(this, socket));
            }
        } catch (IOException e) {
            LOGGER.error("Ошибка в работе сервера", e);
        } finally {
            executorService.shutdown();
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
