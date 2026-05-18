package pjvsemproj.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Server-side connection worker running on an independent background thread.
 * <p>
 * Responsible for managing the bidirectional communication lifecycle with an individual remote client socket.
 * It maintains a blocking read loop to capture inbound text packets, decodes them using pipe-delimited tokens,
 * routes the parsed commands to active match sessions, and exposes safe utilities to send replies.
 */
public class Connection implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Connection.class.getName());
    private final GameServer server;
    private final Socket socket;
    private String playerName;
    private BufferedReader in;
    private PrintWriter out;
    private GameSession session;
    private boolean running;

    /**
     * Constructs a connection handler instance bound to a unique network socket.
     *
     * @param server The master {@link GameServer} instance orchestrating lobby matchmaking queues and registries.
     * @param socket The active network {@link Socket} pipeline established with the remote application client.
     */
    public Connection(GameServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
        this.running = false;
    }

    /**
     * Runs the primary connection worker execution block.
     * <p>
     * Initializes buffered stream wrappers, switches structural flags to active states,
     * and processes incoming text lines within a blocking message-reception loop until
     * a disconnect pattern occurs or an I/O anomaly hits.
     */
    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            running = true;
            while (running) {
                String msg = in.readLine();
                LOGGER.log(Level.INFO, "Server received from {0}: >>>{1}<<<",
                        new Object[]{playerName, msg});
                if (msg != null) {
                    running = processIncomingMessage(msg);
                } else {
                    running = false;
                }
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error communicating with client {0}",
                    ex.getMessage());
        } finally {
            quit();
        }
    }

    /**
     * Splits and parses raw input string messages to route them onto appropriate business handlers.
     *
     * @param msg The raw data line read from the underlying client socket input stream buffer.
     * @return {@code true} if the listener loop should continue processing subsequent inbound messages;
     * {@code false} if the session lifecycle must break and terminate.
     */
    private boolean processIncomingMessage(String msg) {
        String[] tokens = msg.split("\\|", -1);
        Protocol actionCode = Protocol.valueOf(tokens[0]);

        switch (actionCode) {
            case LOGIN:
                handleLogin(tokens);
                break;

            case READY:
                handleReady();
                break;

            case MOVE:
                handleMove(tokens);
                break;

            case ATTACK:
                handleAttack(tokens);
                break;

            case BUY_UNIT:
                handleBuyUnit(tokens);
                break;

            case UPGRADE_CITY:
                handleUpgradeCity(tokens);
                break;

            case END_TURN:
                handleEndTurn();
                break;

            case QUIT:
                session.onPlayerQuit(this);
                running = false;
                return true;

            case GAME_OVER:
                LOGGER.info("Server notified of natural win by client. Shutting down...");
                server.stopServer();
                running = false;
                return true;

            default:
                sendToClient(Protocol.ERROR, "UNKNOWN_COMMAND");
                return true;
        }
        return true;
    }

    /**
     * Composes and transmits an encoded, pipe-delimited packet string back to the connected client.
     *
     * @param code The primary {@link Protocol} header token that classifies the action type context.
     * @param args A varargs string array mapping parameters to append sequentially behind the protocol code header.
     */
    public void sendToClient(Protocol code, String... args) {
        StringBuilder msg = new StringBuilder(code.toString());

        for (String arg : args) {
            msg.append("|").append(arg);
        }

        out.println(msg);
        out.flush();
    }

    /**
     * Processes login authentication packets and registers the client identity in the lobby.
     * <p>
     * Verifies syntax bounds, cleans input strings, and checks if the name is already in use.
     *
     * @param tokens The complete token array split from the raw incoming protocol message string.
     */
    private void handleLogin(String[] tokens) {
        if (tokens.length < 2) {
            sendToClient(Protocol.ERROR, "LOGIN_REQUIRES_NAME");
            return;
        }
        String requestedName = tokens[1].trim();
        this.playerName = requestedName;
        boolean accepted = server.registerConnection(this, requestedName);

        if (!accepted) {
            this.playerName = null;
            sendToClient(Protocol.ERROR, "NAME_ALREADY_TAKEN");
            return;
        }

        sendToClient(Protocol.OK, "LOGIN_ACCEPTED");
    }

    /**
     * Routes a match readiness signal to the containing game session.
     * <p>
     * Verifies that the client is logged in and belongs to an active game room before applying changes.
     */
    private void handleReady() {
        if (!isLoggedIn()) {
            sendToClient(Protocol.ERROR, "NOT_LOGGED_IN");
            return;
        }

        if (session == null) {
            sendToClient(Protocol.ERROR, "NOT_IN_SESSION");
            return;
        }

        session.handleReady(this);
    }

    /**
     * Extracts coordinates and executes safe spatial relocation instructions inside the match session.
     * <p>
     * Verifies authentication guards, validates token element indices, and wraps parameter transformations
     * in try-catch constraints to prevent malformed coordinate injection crashes.
     *
     * @param tokens The complete token array split from the raw incoming protocol message string.
     */
    private void handleMove(String[] tokens) {
        if (!isLoggedIn()) {
            sendToClient(Protocol.ERROR, "NOT_LOGGED_IN");
            return; // Fix missing return statement to stop processing malformed states
        }

        if (tokens.length < 4) {
            sendToClient(Protocol.ERROR, "MOVE_REQUIRES_UNITID_X_Y");
            return;
        }
        String unitId = tokens[1];

        try {
            int x = Integer.parseInt(tokens[2]);
            int y = Integer.parseInt(tokens[3]);
            session.onMove(this, unitId, x, y);
        } catch (NumberFormatException ex) {
            sendToClient(Protocol.ERROR, "INVALID_COORDINATES");
        }
    }

    /**
     * Parses input keys and forwards offensive combat commands to the active game session.
     *
     * @param tokens The complete token array split from the raw incoming protocol message string.
     */
    private void handleAttack(String[] tokens) {
        if (!isLoggedIn()) {
            sendToClient(Protocol.ERROR, "NOT_LOGGED_IN");
            return;
        }

        if (session == null) {
            sendToClient(Protocol.ERROR, "NOT_IN_SESSION");
            return;
        }

        if (tokens.length < 3) {
            sendToClient(Protocol.ERROR, "ATTACK_REQUIRES_ATTACKERID_TARGETID");
            return;
        }

        String attackerId = tokens[1];
        String targetId = tokens[2];

        session.onAttack(this, attackerId, targetId);
    }

    /**
     * Parses input keys and dispatches troop recruitment production commands to the game session.
     *
     * @param tokens The complete token array split from the raw incoming protocol message string.
     */
    private void handleBuyUnit(String[] tokens) {
        if (!isLoggedIn()) {
            sendToClient(Protocol.ERROR, "NOT_LOGGED_IN");
            return;
        }

        if (session == null) {
            sendToClient(Protocol.ERROR, "NOT_IN_SESSION");
            return;
        }

        if (tokens.length < 3) {
            sendToClient(Protocol.ERROR, "BUY_UNIT_REQUIRES_CITYID_TROOPTYPE");
            return;
        }

        String cityId = tokens[1];
        String troopType = tokens[2];

        session.onUnitPurchase(this, cityId, troopType);
    }

    /**
     * Parses configuration keys and dispatches structure level-upgrade requests to the session referee.
     *
     * @param tokens The complete token array split from the raw incoming protocol message string.
     */
    private void handleUpgradeCity(String[] tokens) {
        if (!isLoggedIn()) {
            sendToClient(Protocol.ERROR, "NOT_LOGGED_IN");
            return;
        }

        if (session == null) {
            sendToClient(Protocol.ERROR, "NOT_IN_SESSION");
            return;
        }

        if (tokens.length < 2) {
            sendToClient(Protocol.ERROR, "UPGRADE_CITY_REQUIRES_CITYID");
            return;
        }

        String cityId = tokens[1];

        session.onCityUpgrade(this, cityId);
    }

    /**
     * Forwards an action finalization token to rotate player turns within the game session.
     */
    private void handleEndTurn() {
        if (!isLoggedIn()) {
            sendToClient(Protocol.ERROR, "NOT_LOGGED_IN");
            return;
        }

        if (session == null) {
            sendToClient(Protocol.ERROR, "NOT_IN_SESSION");
            return;
        }

        session.onEndTurn(this);
    }

    /**
     * Gracefully stops the reading thread loop, closes I/O buffers, and breaks socket channels.
     */
    public synchronized void closeConnection() {
        running = false;

        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                LOGGER.info("Connection closed for player: " + playerName);
            }

            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            LOGGER.severe("Error while closing connection for " + playerName + ": " + e.getMessage());
        }
    }

    /**
     * Verifies if this connection context has completed identity validation.
     *
     * @return {@code true} if an authenticated name string is bound to this thread context;
     * {@code false} if unauthenticated.
     */
    private boolean isLoggedIn() {
        return playerName != null;
    }

    /**
     * Hard-closes underlying low-level socket connections and logs critical connection failures.
     */
    public void quit() {
        LOGGER.info("Quitting connection.");
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ex) {
            LOGGER.severe(ex.getMessage());
        }
    }

    /**
     * Links this connection worker thread to an active game session.
     *
     * @param session The multi-user matchmaking {@link GameSession} room context to join.
     */
    public void setSession(GameSession session) {
        this.session = session;
    }

    /**
     * Fetches the registered player identity string name key bound to this connection state.
     *
     * @return The authenticated username string tracking this connection, or {@code null} if unauthenticated.
     */
    public String getPlayerName() {
        return playerName;
    }
}