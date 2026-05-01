package pjvsemproj;

import javafx.application.Application;
import javafx.stage.Stage;
import pjvsemproj.config.GameSetupManager;
import pjvsemproj.controllers.SceneDirector;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.maps.GameMap;
import pjvsemproj.models.game.players.HumanPlayer;
import pjvsemproj.models.services.GameService;
import pjvsemproj.models.services.LocalGameService;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) {
        SceneDirector director = new SceneDirector(stage);
        director.showMainMenu();

//        GameSetupManager gsm = new GameSetupManager();
//        Game game = gsm.setupTestMatch(
//                new GameMap(15, 10),
//                new HumanPlayer("Vasya", 0),
//                new HumanPlayer("Ivan", 0)
//        );
//
//        // TODO add director.showLocalGame and director.showNetworkGame to know where to display save button
//        GameService gameService = new LocalGameService(game);
//        director.showGame(gameService);
    }

    public static void main(String[] args) {
        launch();
    }
}
