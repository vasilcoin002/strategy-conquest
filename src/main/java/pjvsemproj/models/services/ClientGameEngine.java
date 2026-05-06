package pjvsemproj.models.services;

public interface ClientGameEngine extends CoreGameService {
    void login(String playerName);
    void ready();
    String getClientName();
    boolean isMyTurn();
    void quit();

    void setOnBoardUpdated(Runnable callback);
}
