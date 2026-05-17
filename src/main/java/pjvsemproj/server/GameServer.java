package pjvsemproj.server;

import pjvsemproj.config.GameSetupManager;
import pjvsemproj.models.game.Game;
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
 * Manages active connections and game sessions. It listens for inbound client
 * requests on a dedicated port background thread, handles lobby registration, matches usernames
 * against game configuration definitions, and spawns active multiplayer game sessions.
 */
public class GameServer implements Runnable {

    private final int PORT_NUMBER;
    private ServerSocket serverSocket;
    private final Map<String, Connection> connectionsByName = new HashMap<>();
    private final List<GameSession> sessions = new ArrayList<>();
    private static final Logger LOGGER = Logger.getLogger(GameServer.class.getName());
    private boolean running;

    /**
     * Constructs a main game server state machine allocated to a specified network port.
     *
     * @param port The specific network port index number where the server socket will listen for connections.
     */
    public GameServer(int port) {
        this.PORT_NUMBER = port;
    }

    /**
     * Executes the background thread execution block for connection listening loops.
     * <p>
     * Initializes the core {@link ServerSocket} and continually yields to blocking {@code accept()} requests.
     * Each accepted connection is safely wrapped in a worker thread and registered to run independently.
     *
     * @throws RuntimeException wrapping an underlying {@link IOException} if socket binding or initialization fails.
     */
    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PORT_NUMBER);
            running = true;
            while (running) {
                Socket socket = serverSocket.accept();
                Connection connection = new Connection(this, socket);

                new Thread(connection).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Registers a verified network connection client into the active lobby registry tracking registry.
     * <p>
     * Enforces username uniqueness constraints. If successful, it maps the connection and immediately
     * executes a matchmaking query to check if a new game session can be allocated.
     *
     * @param connection The server-side network {@link Connection} handling communications with the remote client machine.
     * @param name       The requested profile username string claimed by the client context.
     * @return {@code true} if the name was free and registration into the lobby maps succeeded;
     * {@code false} if the identifier is already claimed by another online player.
     */
    public synchronized boolean registerConnection(Connection connection, String name) {
        if (isNameTaken(name)) {
            return false;
        }
        LOGGER.info("Adding connection for " + name);

        connectionsByName.put(name, connection);

        tryAssignToSession();

        return true;
    }

    /**
     * Checks whether a specific username string is currently mapped to an active lobby connection.
     *
     * @param name The target username string parameter to check.
     * @return {@code true} if the identifier key exists inside current connections; {@code false} otherwise.
     */
    public synchronized boolean isNameTaken(String name) {
        return connectionsByName.containsKey(name);
    }

    /**
     * Gracefully breaks the continuous listening loop, terminates open sockets, and destroys all ongoing game sessions.
     * <p>
     * Interrupts blocking socket blocks, closes channels, and runs safety teardown routines on all active matches.
     */
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

    /**
     * Evaluates lobby pairing criteria and authoritatively initializes a synchronized multiplayer game session room.
     * <p>
     * Pulls the first two connections from the pool, reads and parses the configuration file,
     * strips any invisible character anomalies, and enforces strict, case-insensitive nickname checks against the level file.
     * If validation passes, it initializes a {@link ServerGameService} referee layer and runs the match session.
     */
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
     * Strips all invisible characters, carriage returns, tabs, and trailing whitespaces from name markers.
     * <p>
     * Protects the matching logic from regex text processing anomalies caused by varied file encodings.
     *
     * @param input The uncleaned raw string configuration line parameter.
     * @return A clean, trimmed string containing only visible tracking identifiers.
     */
    private String cleanName(String input) {
        if (input == null) return "";
        return input.replaceAll("[\\n\\r\\t\\u200e\\u200f]", "").trim();
    }

    /**
     * Removes an active game session from the tracking registry and closes its associated network links.
     *
     * @param session The specific {@link GameSession} instance that needs to be destroyed.
     */
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