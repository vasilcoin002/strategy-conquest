package pjvsemproj;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.maps.GameMap;
import pjvsemproj.views.GameView;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        HBox root = new HBox();
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER);

        // TODO remove test game
        GameView gameView = new GameView(
                stage, new Game(new GameMap(15, 10))
        );
        gameView.show();

        stage.show();

    }
}
