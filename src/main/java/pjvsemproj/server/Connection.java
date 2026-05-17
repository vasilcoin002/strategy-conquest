package pjvsemproj.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connection implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Connection.class.getName());
    private final GameServer server;
    private final Socket socket;
    private String playerName;
    private BufferedReader in;
    private PrintWriter out;
    private GameSession session;
    private boolean running;

    public Connection(GameServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
        this.running = false;
    }

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
                System.out.println("Server notified of natural win by client. Shutting down...");
                server.stopServer();
                running = false;
                return true;

            default:
                sendToClient(Protocol.ERROR, "UNKNOWN_COMMAND");
                return true;
        }
        return true;
    }

    public void sendToClient(Protocol code, String... args) {
        StringBuilder msg = new StringBuilder(code.toString());

        for (String arg : args) {
            msg.append("|").append(arg);
        }

        out.println(msg);
        out.flush();
    }

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

    private void handleMove(String[] tokens) {

        if (!isLoggedIn()) {
            sendToClient(Protocol.ERROR, "NOT_LOGGED_IN");
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
     * Safely terminates the network connection, closes streams,
     * and stops the listening thread.
     */
    public synchronized void closeConnection() {
        running = false;

        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Connection closed for player: " + playerName);
            }

            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            System.err.println("Error while closing connection for " + playerName + ": " + e.getMessage());
        }
    }

    private boolean isLoggedIn() {
        return playerName != null;
    }

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

    public void setSession(GameSession session) {
        this.session = session;
    }

    public String getPlayerName() {
        return playerName;
    }
}
