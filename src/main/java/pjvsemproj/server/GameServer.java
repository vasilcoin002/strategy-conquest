package pjvsemproj.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class GameServer implements Runnable {
    // TODO unregisterConnection, stopServer, tryAssignToSession, removeSession


    private final int PORT_NUMBER;
    private ServerSocket serverSocket;
    private Socket socket;
    private final Map<String, Connection> connectionsByName = new HashMap<>();
    private final List<GameSession> sessions = new ArrayList<>();
    private static final Logger LOGGER = Logger.getLogger(GameServer.class.getName());

    public GameServer(int port) {
        this.PORT_NUMBER = port;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PORT_NUMBER);
            while (true) {
                socket = serverSocket.accept();
                Connection connection = new Connection(this, socket);

                new Thread(connection).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized boolean registerConnection(Connection connection, String name) {
        if (isNameTaken(name)) {
            LOGGER.info("Adding connection for" + name);
            connectionsByName.put(name, connection);
            return true;
        }
        return false;
    }

    public synchronized boolean isNameTaken(String name) {
        if (connectionsByName.containsKey(name)) {
            return false;
        }
        return true;
    }

    public synchronized void unregisterConnection() {
    }

    public synchronized void stopServer() {
    }

    public synchronized void tryAssignToSession() {
    }

    public synchronized void removeSession() {
    }

}
