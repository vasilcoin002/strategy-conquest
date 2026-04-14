package pjvsemproj.server;

import pjvsemproj.models.game.players.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connection implements Runnable {

    // TODO handleMove, handleAttack, handleBuyUnit, handleUpgradeCity, handleEndTurn

    private static final Logger LOGGER = Logger.getLogger(Connection.class.getName());
    private final GameServer server;
    private final Socket socket;
    private Player player;
    private String playerName;
    private BufferedReader in;
    private PrintWriter out;
    private GameSession session;

    public Connection(GameServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            boolean running = true;
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

        }
        // TODO remove and add normal return
        return true;
    }

    public void sendToClient(Protocol code, String... args) {
        StringBuilder msg = new StringBuilder(code.toString());

        for (String arg : args) {
            msg.append("|").append(arg);
        }

        out.println(msg);
    }

    private boolean handleLogin(String[] tokens) {
        if (tokens.length < 2) {
            sendToClient(Protocol.ERROR, "LOGIN_REQUIRES_NAME");
            return true;
        }
        String requestedName = tokens[1].trim();
        boolean accepted = server.registerConnection(this, requestedName);
        if (!accepted) {
            sendToClient(Protocol.ERROR, "NAME_ALREADY_TAKEN");
            return true;
        }
        sendToClient(Protocol.OK, "LOGIN_ACCEPTED");
        return true;
    }

    private boolean handleReady() {
        if (!isLoggedIn()) {
            sendToClient(Protocol.ERROR, "NOT_LOGGED_IN");
            return true;
        }

        if (session == null) {
            sendToClient(Protocol.ERROR, "NOT_IN_SESSION");
            return true;
        }

        session.handleReady(this);
        return true;
    }

    private boolean handleMove(String[] tokens) {

        if (!isLoggedIn()) {
            sendToClient(Protocol.ERROR, "NOT_LOGGED_IN");
        }

        if (tokens.length < 4) {
            sendToClient(Protocol.ERROR, "MOVE_REQUIRES_UNITID_X_Y");
            return true;
        }
        String unitId = tokens[1];

        try {
            int x = Integer.parseInt(tokens[2]);
            int y = Integer.parseInt(tokens[3]);
            // TODO uncomment
//            session.handleMove();
        } catch (NumberFormatException ex) {
            sendToClient(Protocol.ERROR, "INVALID_COORDINATES");
        }

        return true;
    }

    private boolean handleMove() {
        return false;
    }

    private boolean handleAttack() {
        return false;
    }

    private boolean handleBuyUnit() {
        return false;
    }

    private boolean handleUpgradeCity() {
        return false;
    }

    private boolean handleEndTurn() {
        return false;
    }

    private boolean isLoggedIn() {
        return player != null;
    }

    private void quit() {
        LOGGER.info("Quitting connection.");
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ex) {
            LOGGER.severe(ex.getMessage());
        }
    }

    public Player getPlayer() {
        return player;
    }
}
