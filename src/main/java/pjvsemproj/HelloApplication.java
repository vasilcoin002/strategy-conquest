package pjvsemproj;

import javafx.application.Application;
import javafx.stage.Stage;
import pjvsemproj.controllers.SceneDirector;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) {
        SceneDirector director = new SceneDirector(stage);
        director.showMainMenu();
    }

    public static void main(String[] args) {
        launch();
    }
}
