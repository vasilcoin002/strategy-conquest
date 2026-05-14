package pjvsemproj.views;

public class LobbyInfo {
    private final String name;
    private final String host;
    private final int port;

    public LobbyInfo(String name, String host, int port) {
        this.name = name;
        this.host = host;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return name + " (" + host + ":" + port + ")";
    }
}