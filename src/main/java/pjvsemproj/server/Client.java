package pjvsemproj.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Client-side networking engine running on a dedicated background execution thread.
 * <p>
 * Responsible for opening sockets to remote hosts, initiating authentication sequences,
 * and maintaining a continuous read loop to listen for inbound text packets from the server.
 * Decodes incoming message strings via protocol delimiter tokens and dispatches events down onto
 * registered view and engine listeners.
 */
public class Client implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
    private ServerEventListener listener;
    private LobbyEventListener lobbyListener;

    private final String host;
    private final int port;
    private final String playerName;

    private PrintWriter out;
    private boolean running;

    /**
     * Constructs a network client worker initialized with target host coordinates and player credentials.
     *
     * @param host       The remote server IPv4 loopback or external IP host address string to connect to.
     * @param port       The destination network port opened by the hosting server.
     * @param playerName The custom username selected by the human user to claim during login authentication.
     */
    public Client(String host, int port, String playerName) {
        this.host = host;
        this.port = port;
        this.playerName = playerName;
        this.running = false;
    }

    /**
     * Executes the background network listener and connection connection cycle.
     * <p>
     * Allocates standard system TCP sockets, binds text I/O readers/writers, dispatches the primary
     * login verification message, and processes a blocking loop for stream data packets.
     *
     * @throws RuntimeException Wrapping an underlying {@link IOException} if a severe connection error occurs.
     */
    @Override
    public void run() {
        try (
                Socket socket = new Socket(host, port);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            out = new PrintWriter(socket.getOutputStream(), true);
            login(playerName);
            LOGGER.info("LOGIN sent");
            running = true;
            while (running) {
                try {
                    String msg = in.readLine();
                    if (msg != null) {
                        System.out.println("CLIENT DEBUG: Heard -> " + msg);
                        processIncomingMessage(msg);
                    } else {
                        running = false;
                    }
                } catch (Exception e) {
                    System.err.println("CLIENT LISTENER CRASHED!");
                    // TODO log
                    running = false;
                }
            }
        } catch (ConnectException ex) {
            LOGGER.log(Level.SEVERE, "Server is not running. {0}", ex.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Parses and handles inbound text packets using explicit protocol action codes.
     * <p>
     * Splits raw messages by pipe characters to isolate parameters, matches tokens to the {@link Protocol}
     * enum index, and triggers callbacks to synchronize UI screens or advance local game states.
     *
     * @param msg The raw string text line read directly from the inbound server socket stream.
     */
    private void processIncomingMessage(String msg) {
        String[] tokens = msg.split("\\|", -1);
        Protocol actionCode = Protocol.valueOf(tokens[0]);

        switch (actionCode) {
            case OK:
                LOGGER.info("Server OK: " + (tokens.length > 1 ? tokens[1] : ""));
                break;

            case ERROR:
                String errorMsg = tokens.length > 1 ? tokens[1] : "Unknown Error";

                if (lobbyListener != null) {
                    lobbyListener.onError(errorMsg);
                }

                LOGGER.warning("Server ERROR: " + errorMsg);
                break;

            case GAME_STARTED:
                String player1 = tokens[1];
                String player2 = tokens[2];

                LOGGER.info("Game started: " + player1 + " vs " + player2);
                break;

            case GAME_OVER:
                System.out.println("Client received GAME_OVER for " + tokens[1]);
                String winnerName = tokens[1];
                if (listener != null) {
                    listener.onGameOver(winnerName);
                }
                running = false; // Stop the client loop since the game is over
                break;

            case GAME_STATE:
                int delimiterIndex = msg.indexOf('|');
                String jsonPayload = msg.substring(delimiterIndex + 1);

                if (lobbyListener != null) {
                    lobbyListener.onGameState(jsonPayload);
                }
                break;

            case TURN_STARTED:
                String currentPlayer = tokens[1];
                System.out.println("CLIENT TURN_STARTED listener = " + listener);

                if (listener != null) {
                    listener.onTurnStarted(currentPlayer);
                }
                LOGGER.info("Turn started: " + currentPlayer);
                break;

            case UNIT_MOVED:
                String unitId = tokens[1];
                int x = Integer.parseInt(tokens[2]);
                int y = Integer.parseInt(tokens[3]);
                if (listener != null) {
                    listener.onUnitMoved(unitId, x, y);
                }
                LOGGER.info("Unit moved: " + unitId + " -> (" + x + "," + y + ")");
                break;

            case UNIT_ATTACKED:
                String attackerId = tokens[1];
                String targetId = tokens[2];
                int newHp = Integer.parseInt(tokens[3]);
                if (listener != null) {
                    listener.onUnitAttacked(attackerId, targetId, newHp);
                }

                LOGGER.info("Attack: " + attackerId + " -> " + targetId + " HP=" + newHp);
                break;

            case UNIT_DIED:
                String deadUnit = tokens[1];
                if (listener != null) {
                    listener.onUnitDied(deadUnit);
                }
                LOGGER.info("Unit died: " + deadUnit);
                break;

            case CITY_UPGRADED:
                String cityId = tokens[1];
                if (listener != null) {
                    listener.onCityUpgraded(cityId);
                }

                LOGGER.info("City upgraded: " + cityId);
                break;

            case QUIT:
                LOGGER.info("Opponent quit: " + tokens[1]);
                break;

            case UNIT_BOUGHT:
                if (listener != null) {
                    listener.onUnitBought(
                            tokens[1],
                            tokens[2],
                            tokens[3]
                    );
                }
                break;
            default:
                LOGGER.warning("Unknown message: " + msg);
                break;
        }
    }

    /**
     * Authenticates the client connection by writing a standard login registration string.
     *
     * @param playerName The clean user identity name string to claim on the remote server host.
     */
    public void login(String playerName) {
        sendToServer(Protocol.LOGIN, playerName);
    }

    /**
     * Centralized network utility method to compose and transmit encoded pipe-delimited packet messages.
     *
     * @param code The primary {@link Protocol} header token that classifies the action type context.
     * @param args A varargs string array mapping parameters to append sequentially behind the packet header.
     */
    public void sendToServer(Protocol code, String... args) {
        StringBuilder msg = new StringBuilder(code.toString());

        for (String arg : args) {
            msg.append("|").append(arg);
        }

        LOGGER.log(Level.INFO, "Client {0} sending >>>{1}<<<", new Object[]{playerName, msg});
        out.println(msg);
    }

    /**
     * Transmits a contextual unit navigation move instruction over to the remote host.
     *
     * @param unitId Unique identifier token of the moving troop entity.
     * @param x      Target horizontal destination column map grid coordinate.
     * @param y      Target vertical destination row map grid coordinate.
     */
    public void moveUnit(String unitId, int x, int y) {
        sendToServer(Protocol.MOVE, unitId, String.valueOf(x), String.valueOf(y));
    }

    /**
     * Transmits an offensive engagement combat execution command over to the server.
     *
     * @param attackerId Unique tracking key matching the attacking troop asset.
     * @param targetId   Unique tracking key matching the defending troop asset target.
     */
    public void attack(String attackerId, String targetId) {
        sendToServer(Protocol.ATTACK, attackerId, targetId);
    }

    /**
     * Transmits a military production recruitment request over to the server.
     *
     * @param cityId    Unique registration identifier tracking the producing settlement structure.
     * @param troopType High-level class enum configuration type label of the unit to buy.
     */
    public void buyUnit(String cityId, String troopType) {
        sendToServer(Protocol.BUY_UNIT, cityId, troopType);
    }

    /**
     * Transmits a settlement tier transformation development request to the server.
     *
     * @param cityId Unique tracking token mapping out the settlement targeted for upgrade.
     */
    public void upgradeCity(String cityId) {
        sendToServer(Protocol.UPGRADE_CITY, cityId);
    }

    /**
     * Transmits a turn finalization authorization request to hand turn permissions over to subsequent players.
     */
    public void endTurn() {
        sendToServer(Protocol.END_TURN);
    }

    /**
     * Transmits a match synchronization token to notify the match session that this user is fully ready to join the map.
     */
    public void ready() {
        sendToServer(Protocol.READY);
    }

    /**
     * Transmits a voluntary surrender declaration packet to safely teardown active multiplayer sessions on the server host.
     */
    public void quit() {
        sendToServer(Protocol.QUIT);
    }

    /**
     * Attaches a dedicated game listener to handle events once a match session boots into play screens.
     *
     * @param listener The active {@link ServerEventListener} tracking live game behaviors and updates.
     */
    public void setServerEventListener(ServerEventListener listener) {
        this.listener = listener;
    }

    /**
     * Attaches a lobby listener to intercept pre-game matchmaking states and connection verification errors.
     *
     * @param listener The matchmaking {@link LobbyEventListener} tracking pre-game room transitions.
     */
    public void setLobbyListener(LobbyEventListener listener) {
        this.lobbyListener = listener;
    }
}