package pjvsemproj.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client implements Runnable{

    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());

    private final String host;
    private final int port;
    private final String playerName;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;


    public Client(String host, int port, String playerName) {
        this.host = host;
        this.port = port;
        this.playerName = playerName;
    }

    @Override
    public void run() {
        try{
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            boolean running = true;
            while(running){
                String msg = in.readLine();
                LOGGER.log(Level.INFO, "Client received: >>>{0}<<<", msg);
                if (msg != null){
                    processIncomingMessage(msg);
                } else {
                    running = false;
                }
            }
        } catch (ConnectException ex){
            LOGGER.log(Level.SEVERE, "Server is not running. {0}", ex.getMessage());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean processIncomingMessage(String msg){}

    public void close(){
        LOGGER.info("Closing client");
    }

    public void sendToServer(Protocol code, String... args){
        StringBuilder msg = new StringBuilder(code.toString());

        for (String arg : args) {
            msg.append("|").append(arg);
        }

        LOGGER.log(Level.INFO, "Client {0} sending >>>{1}<<<", new Object[]{playerName, msg});
        out.println(msg);
    }

    public void moveUnit(String unitId, int x, int y){
        sendToServer(Protocol.MOVE, unitId, String.valueOf(x), String.valueOf(y));
    }

    public void attack(String attackerId, String targetId){
        sendToServer(Protocol.ATTACK, attackerId, targetId);
    }

    public void buyUnit(String cityId, String troopType){
        sendToServer(Protocol.BUY_UNIT, cityId, troopType);
    }

    public void upgradeCity(String cityId){
        sendToServer(Protocol.UPGRADE_CITY, cityId);
    }

    public void endTurn(){
        sendToServer(Protocol.END_TURN);
    }

    public void ready(){
        sendToServer(Protocol.READY);
    }

    public void quit(){
        sendToServer(Protocol.QUIT);
    }
}
