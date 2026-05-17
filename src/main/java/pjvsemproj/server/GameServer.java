package pjvsemproj.server;

import pjvsemproj.config.GameSetupManager;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.maps.GameMap;
import pjvsemproj.models.game.players.HumanPlayer;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.services.CoreGameService;
import pjvsemproj.models.services.ServerGameService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


/**
 * Main server class responsible for accepting client connections.
 * <p>
 * Manages active connections and game sessions.
 */
public class GameServer implements Runnable {
    // TODO tryAssignToSession

    private final int PORT_NUMBER;
    private ServerSocket serverSocket;
    private Socket socket;
    private final Map<String, Connection> connectionsByName = new HashMap<>();
    private final List<GameSession> sessions = new ArrayList<>();
    private static final Logger LOGGER = Logger.getLogger(GameServer.class.getName());
    private boolean running;

    public GameServer(int port) {
        this.PORT_NUMBER = port;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PORT_NUMBER);
            running = true;
            while (running) {
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
            return false;
        }
        LOGGER.info("Adding connection for " + name);

        connectionsByName.put(name, connection);

        tryAssignToSession();

        return true;
    }

    public synchronized boolean isNameTaken(String name) {
        if (connectionsByName.containsKey(name)) {
            return true;
        }
        return false;
    }

    public synchronized void unregisterConnection(String connectionName, Connection connection) {
        boolean removed = connectionsByName.remove(connectionName, connection);

        if (removed) {
            LOGGER.info("Connection removed: " + connectionName);
        } else {
            LOGGER.warning("Failed to remove connection: " + connectionName);
        }
    }

    // Make sure you have a boolean flag for your loop, e.g., private volatile boolean running = true;

    public synchronized void stopServer() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close(); // This immediately interrupts serverSocket.accept()
                System.out.println("Server socket closed gracefully.");
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }

        for (GameSession session : sessions) {
            session.terminate();
        }
        sessions.clear();
    }

    public synchronized void tryAssignToSession() {
        if (connectionsByName.size() >= 2) {

            List<Connection> waitingList = new ArrayList<>(connectionsByName.values());
            Connection c1 = waitingList.get(0);
            Connection c2 = waitingList.get(1);

            connectionsByName.remove(c1.getPlayerName());
            connectionsByName.remove(c2.getPlayerName());

            GameSetupManager setupManager = new GameSetupManager();
            Game game = setupManager.loadNetworkGame("config.json", c1.getPlayerName(), c2.getPlayerName());
            CoreGameService service = new ServerGameService(game);

            GameSession session = new GameSession(this, c1, c2, service);
            sessions.add(session);

            c1.setSession(session);
            c2.setSession(session);

            LOGGER.info("Match created between " + c1.getPlayerName() + " and " + c2.getPlayerName());
            session.startGame();
        }
    }

    public synchronized void removeSession(GameSession session) {
        boolean removed = sessions.remove(session);

        if (removed) {
            LOGGER.info("Session removed.");

            session.getConnection1().quit();
            session.getConnection2().quit();
        } else {
            LOGGER.warning("Session not found.");
        }
    }
}
