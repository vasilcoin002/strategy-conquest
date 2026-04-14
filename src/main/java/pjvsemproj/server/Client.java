package pjvsemproj.server;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

public class Client implements Runnable {

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
    }

    private boolean processIncomingMessage(String msg) {
        return false;
    }

    public void close() {
    }

    public void send() {
    }

    public void moveUnit() {
    }

    public void attack() {
    }

    public void buyUnit() {
    }

    public void upgradeCity() {
    }

    public void endTurn() {
    }

    public void ready() {
    }

    public void quit() {
    }
}
