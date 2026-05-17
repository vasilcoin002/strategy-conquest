package pjvsemproj;

import javafx.application.Application;
import javafx.stage.Stage;
import pjvsemproj.controllers.SceneDirector;

/**
 * Main JavaFX Application lifecycle coordinator.
 * <p>
 * Responsible for establishing the core graphical window pipeline, bootstrapping foundational UI components,
 * and initializing the root scene router orchestration layer.
 */
public class HelloApplication extends Application {

    /**
     * Initializes the primary application window and loads the main landing screen graph.
     * <p>
     * This lifecycle entry point method instantiates the centralized {@link SceneDirector} router context,
     * pins it to the runtime stage platform wrapper, and triggers the main menu display layout.
     *
     * @param stage The primary execution window wrapper platform provisioned by the JavaFX runtime loop.
     */
    @Override
    public void start(Stage stage) {
        SceneDirector director = new SceneDirector(stage);
        director.showMainMenu();
    }

    /**
     * Fallback execution entry point invoked by legacy application environments.
     * <p>
     * Forwards execution arguments directly over into the JavaFX module bootstrap pipeline via {@link Application#launch(String...)}.
     *
     * @param args Array of optional system runtime execution command-line parameter arguments.
     */
    public static void main(String[] args) {
        launch();
    }
}