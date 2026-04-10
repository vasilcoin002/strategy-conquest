package pjvsemproj.server;

import java.net.Socket;

public class Connection implements Runnable{

    private final GameServer server;
    private final Socket socket;

    public Connection(GameServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run() {

    }
}
