package pjvsemproj.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer implements Runnable {

    private final int port;
    private ServerSocket serverSocket;
    private Socket socket;


    public GameServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true){
                socket = serverSocket.accept();
                Connection connection = new Connection(this, socket);

                new Thread(connection).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
