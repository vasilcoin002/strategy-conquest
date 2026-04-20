package pjvsemproj;

import javafx.application.Application;
import javafx.stage.Stage;
import pjvsemproj.controllers.SceneDirector;

public class HelloApplication extends Application {
//    @Override
//    public void start(Stage stage) throws IOException {
//
//        HBox root = new HBox();
//        root.setPadding(new Insets(10));
//        root.setAlignment(Pos.CENTER);
//
//        // TODO remove test game
//        GameSetupManager gsm = new GameSetupManager();
//        Game game = gsm.setupTestMatch(
//                new GameMap(15, 10),
//                new HumanPlayer("Vasya", 0),
//                new HumanPlayer("Ivan", 0)
////                new BotPlayer("Ivan", 0)
//        );
//
//        LocalGameService lgs = new LocalGameService(game);
//
//        GameController gameController = new GameController(stage, lgs);
//
//        gameController.showView();
//        stage.show();
//    }
    @Override
    public void start(Stage stage) {
        SceneDirector director = new SceneDirector(stage);
        director.showMainMenu();
    }

    public static void main(String[] args) {
        launch();
    }
}
