package pjvsemproj;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import pjvsemproj.models.entities.Entity;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
//        Player mainCharacter = new Player(100, 0, 0, new Knife());

        HBox root = new HBox();
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 320, 240);

        stage.setScene(scene);
//        stage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
//            Map<KeyCode, Runnable> controls = Map.of(
//                    KeyCode.UP, mainCharacter::moveUp,
//                    KeyCode.DOWN, mainCharacter::moveDown,
//                    KeyCode.LEFT, mainCharacter::moveLeft,
//                    KeyCode.RIGHT, mainCharacter::moveRight
//            );
//            controls.getOrDefault(event.getCode(), () -> {
//            }).run();
//
//            System.out.println("x: " + mainCharacter.getX() + "y: " + mainCharacter.getY());
//        });
        stage.show();
    }
}
