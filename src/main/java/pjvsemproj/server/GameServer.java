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
        // If we have at least 2 people waiting in the lobby
        if (connectionsByName.size() >= 2) {

            List<Connection> waitingList = new ArrayList<>(connectionsByName.values());
            Connection c1 = waitingList.get(0);
            Connection c2 = waitingList.get(1);

            pjvsemproj.config.GameConfigParser parser = new pjvsemproj.config.GameConfigParser();
            pjvsemproj.dto.GameDTO dto;
            try {
                dto = parser.parseLevelConfig("config.json");
            } catch (Exception e) {
                c1.sendToClient(Protocol.ERROR, "Config failed: " + e.getMessage());
                c2.sendToClient(Protocol.ERROR, "Config failed: " + e.getMessage());
                return;
            }

            String expected1 = cleanName(dto.players.get(0).name);
            String expected2 = cleanName(dto.players.get(1).name);

            String name1 = cleanName(c1.getPlayerName());
            String name2 = cleanName(c2.getPlayerName());

            boolean isMatch = (name1.equalsIgnoreCase(expected1) && name2.equalsIgnoreCase(expected2)) ||
                    (name1.equalsIgnoreCase(expected2) && name2.equalsIgnoreCase(expected1));

            if (!isMatch) {
                String errorMsg = "Mismatch! Config needs: '" + expected1 + "' & '" + expected2 + "'. You typed: '" + name1 + "' & '" + name2 + "'.";

                c1.sendToClient(Protocol.ERROR, errorMsg);
                c2.sendToClient(Protocol.ERROR, errorMsg);

                connectionsByName.remove(c1.getPlayerName());
                connectionsByName.remove(c2.getPlayerName());
                c1.quit();
                c2.quit();
                return;
            }

            pjvsemproj.config.GameConfigSanitizer sanitizer = new pjvsemproj.config.GameConfigSanitizer();
            pjvsemproj.config.GameConfigValidator validator = new pjvsemproj.config.GameConfigValidator();
            GameSetupManager setupManager = new GameSetupManager();

            try {
                sanitizer.sanitize(dto);
                validator.validate(dto);
            } catch (Exception e) {
                c1.sendToClient(Protocol.ERROR, "Validation failed: " + e.getMessage());
                c2.sendToClient(Protocol.ERROR, "Validation failed: " + e.getMessage());
                return;
            }

            Game game = setupManager.createNetworkGameFromDTO(dto, expected1);

            connectionsByName.remove(c1.getPlayerName());
            connectionsByName.remove(c2.getPlayerName());

            CoreGameService service = new ServerGameService(game);
            GameSession session = new GameSession(this, c1, c2, service);
            sessions.add(session);

            c1.setSession(session);
            c2.setSession(session);

            LOGGER.info("Perfect match created between " + name1 + " and " + name2);
            session.startGame();
        }
    }

    /**
     * Strips all invisible characters, carriage returns, and extra spaces.
     */
    private String cleanName(String input) {
        if (input == null) return "";
        return input.replaceAll("[\\n\\r\\t\\u200e\\u200f]", "").trim();
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
