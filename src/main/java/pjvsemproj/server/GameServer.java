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

    public synchronized void stopServer() {
        LOGGER.info("Stopping server...");

        for (GameSession session : new ArrayList<>(sessions)) {
            removeSession(session);
        }

        for (Connection connection : new ArrayList<>(connectionsByName.values())) {
            connection.quit();
        }
        connectionsByName.clear();

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            LOGGER.severe("Error closing server socket: " + e.getMessage());
        }

        LOGGER.info("Server stopped.");
    }

    public synchronized void tryAssignToSession() {
        // If we have at least 2 people waiting in the lobby
        if (connectionsByName.size() >= 2) {

            // 1. Grab the first two connections
            List<Connection> waitingList = new ArrayList<>(connectionsByName.values());
            Connection c1 = waitingList.get(0);
            Connection c2 = waitingList.get(1);

            // 2. Remove them from the lobby so they don't get matched again
            connectionsByName.remove(c1.getPlayerName());
            connectionsByName.remove(c2.getPlayerName());

            // TODO remove creation of predefined test match and load configured match from config file
            // 3. Create the Player domain objects
            Player p1 = new HumanPlayer(c1.getPlayerName(), 100);
            Player p2 = new HumanPlayer(c2.getPlayerName(), 100);

            // 4. Build the Game State
            GameSetupManager setupManager = new GameSetupManager();
            GameMap map = new GameMap(5, 5);
            Game game = setupManager.setupTestMatch(map, p1, p2);

            // 5. Create the Referee (Service)
            CoreGameService service = new ServerGameService(game);

            // 6. Create the Session and inject the Referee
            GameSession session = new GameSession(this, c1, c2, service);
            sessions.add(session);

            // 7. Link the connections to their new session
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
