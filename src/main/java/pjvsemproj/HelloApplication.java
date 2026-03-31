package pjvsemproj;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.maps.GameMap;
import pjvsemproj.models.game.players.BotPlayer;
import pjvsemproj.models.game.players.HumanPlayer;
import pjvsemproj.models.managers.GameSetupManager;
import pjvsemproj.views.GameView;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        HBox root = new HBox();
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER);

        // TODO remove test game
        GameSetupManager gsm = new GameSetupManager();
        Game game = gsm.setupTestMatch(
                new GameMap(15, 10),
                new HumanPlayer("Vasya", 0),
                new BotPlayer("Ivan", 0)
        );
        GameView gameView = new GameView(stage, game);
        gameView.show();

        stage.show();

    }
}
