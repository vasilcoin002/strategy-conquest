package semproject.semproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import semproject.semproject.models.Entity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Entity mainCharacter = new Entity(1d, "Vasya", 0, 0);


        HBox root = new HBox();
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 320, 240);

        stage.setScene(scene);
        stage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            Map<KeyCode, Runnable> controls = Map.of(
                    KeyCode.UP,    mainCharacter::moveUp,
                    KeyCode.DOWN,  mainCharacter::moveDown,
                    KeyCode.LEFT,  mainCharacter::moveLeft,
                    KeyCode.RIGHT, mainCharacter::moveRight
            );
            controls.getOrDefault(event.getCode(), () -> {}).run();

            System.out.println("x: " + mainCharacter.getX() + "y: " + mainCharacter.getY());
        });
        stage.show();

    }
}
