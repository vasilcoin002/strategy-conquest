package pjvsemproj.server;

import pjvsemproj.models.game.players.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connection implements Runnable{

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
        try{
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            boolean running = true;
            while (running){
                String msg = in.readLine();
                LOGGER.log(Level.INFO, "Server received from {0}: >>>{1}<<<",
                        new Object[]{playerName, msg});
                if(msg != null){
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

    private boolean processIncomingMessage(String msg){
        String[] tokens = msg.split("\\|", -1);
        Protocol actionCode = Protocol.valueOf(tokens[0]);

        switch(actionCode){
            case LOGIN:
                return handleLogin(tokens);

            case READY:
               return handleReady();

            case MOVE:
               return handleMove(tokens);

            case ATTACK:
               return handleAttack(tokens);

            case BUY_UNIT:
               return  handleBuyUnit(tokens);

            case UPGRADE_CITY:
               return handleUpgradeCity(tokens);

            case END_TURN:
                return handleEndTurn();

            case QUIT:
                handleQuit();
                return false;

            default:
                sendToClient(Protocol.ERROR, "UNKNOWN_COMMAND");
                return true;
        }
    }

    public void sendToClient(Protocol code, String... args){
        StringBuilder msg = new StringBuilder(code.toString());

        for (String arg : args) {
            msg.append("|").append(arg);
        }

        out.println(msg);
    }

    private boolean handleLogin(String[] tokens){
        if (tokens.length < 2){
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

    private boolean handleMove(String[] tokens){

        if(!isLoggedIn()){
            sendToClient(Protocol.ERROR, "NOT_LOGGED_IN");
        }

        if (tokens.length < 4) {
            sendToClient(Protocol.ERROR, "MOVE_REQUIRES_UNITID_X_Y");
            return true;
        }
        String unitId = tokens[1];

        try{
            int x = Integer.parseInt(tokens[2]);
            int y = Integer.parseInt(tokens[3]);
            session.onMove(this, unitId, x, y);
        } catch (NumberFormatException ex){
            sendToClient(Protocol.ERROR, "INVALID_COORDINATES");
        }

        return true;
    }

    private boolean handleAttack(String[] tokens){
        if(!isLoggedIn()) {
            sendToClient(Protocol.ERROR, "NOT_LOGGED_IN");
            return true;
        }

        if (session == null) {
            sendToClient(Protocol.ERROR, "NOT_IN_SESSION");
            return true;
        }

        if (tokens.length < 3) {
            sendToClient(Protocol.ERROR, "ATTACK_REQUIRES_ATTACKERID_TARGETID");
            return true;
        }

        String attackerId = tokens[1];
        String targetId = tokens[2];

        session.onAttack(this, attackerId, targetId);
        return true;
    }

    private boolean handleBuyUnit(String[] tokens){
        if(!isLoggedIn()) {
            sendToClient(Protocol.ERROR, "NOT_LOGGED_IN");
            return true;
        }

        if (session == null) {
            sendToClient(Protocol.ERROR, "NOT_IN_SESSION");
            return true;
        }

        if (tokens.length < 3) {
            sendToClient(Protocol.ERROR, "BUY_UNIT_REQUIRES_CITYID_TROOPTYPE");
            return true;
        }

        String cityId = tokens[1];
        String troopType = tokens[2];

        session.onUnitPurchase(this, cityId, troopType);
        return true;
    }

    private boolean handleUpgradeCity(String[] tokens){
        if(!isLoggedIn()) {
            sendToClient(Protocol.ERROR, "NOT_LOGGED_IN");
            return true;
        }

        if (session == null) {
            sendToClient(Protocol.ERROR, "NOT_IN_SESSION");
            return true;
        }

        if (tokens.length < 2) {
            sendToClient(Protocol.ERROR, "UPGRADE_CITY_REQUIRES_CITYID");
            return true;
        }

        String cityId = tokens[1];

        session.onCityUpgrade(this, cityId);
        return true;
    }

    private boolean handleEndTurn(){
        if(!isLoggedIn()) {
            sendToClient(Protocol.ERROR, "NOT_LOGGED_IN");
            return true;
        }

        if (session == null) {
            sendToClient(Protocol.ERROR, "NOT_IN_SESSION");
            return true;
        }

        session.onEndTurn(this);
        return true;
    }

    private void handleQuit() {
        if (session != null) {
            session.onPlayerQuit(this);
        }
    }

    private boolean isLoggedIn() {
        return player != null;
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

    public Player getPlayer() {
        return player;
    }
}
