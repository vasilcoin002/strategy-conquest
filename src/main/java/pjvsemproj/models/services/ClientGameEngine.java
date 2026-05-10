package pjvsemproj.models.services;

import java.util.function.Consumer;

public interface ClientGameEngine extends CoreGameService {
    void login(String playerName);
    void ready();
    String getClientName();
    boolean isMyTurn();
    void quit();

    void setOnGameOver(Consumer<String> callback);
    void setOnBoardUpdated(Runnable callback);
}
