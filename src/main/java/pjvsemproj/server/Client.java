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
 * Client-side networking class.
 * <p>
 * Sends commands to the server and processes responses.
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

    public Client(String host, int port, String playerName) {
        this.host = host;
        this.port = port;
        this.playerName = playerName;
        this.running = false;
    }

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

    public void login(String playerName) {
        sendToServer(Protocol.LOGIN, playerName);
    }

    public void sendToServer(Protocol code, String... args) {
        StringBuilder msg = new StringBuilder(code.toString());

        for (String arg : args) {
            msg.append("|").append(arg);
        }

        LOGGER.log(Level.INFO, "Client {0} sending >>>{1}<<<", new Object[]{playerName, msg});
        out.println(msg);
    }

    public void moveUnit(String unitId, int x, int y) {
        sendToServer(Protocol.MOVE, unitId, String.valueOf(x), String.valueOf(y));
    }

    public void attack(String attackerId, String targetId) {
        sendToServer(Protocol.ATTACK, attackerId, targetId);
    }

    public void buyUnit(String cityId, String troopType) {
        sendToServer(Protocol.BUY_UNIT, cityId, troopType);
    }

    public void upgradeCity(String cityId) {
        sendToServer(Protocol.UPGRADE_CITY, cityId);
    }

    public void endTurn() {
        sendToServer(Protocol.END_TURN);
    }

    public void ready() {
        sendToServer(Protocol.READY);
    }

    public void quit() {
        sendToServer(Protocol.QUIT);
    }

    public void setServerEventListener(ServerEventListener listener) {
        this.listener = listener;
    }

    public void setLobbyListener(
            LobbyEventListener listener
    ) {
        this.lobbyListener = listener;
    }
}
