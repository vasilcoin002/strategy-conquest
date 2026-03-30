package pjvsemproj.views;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import pjvsemproj.models.game.Game;

public class GameView {

    private static final int TILE_SIZE = 64;
    private final int gameAreaWidth;
    private final int gameAreaHeight;

    private final Stage stage;
    private final Scene scene;
    private final Game game;
    private final GraphicsContext gc;

    public GameView(Stage stage, Game game) {
        this.stage = stage;
        this.game = game;

        gameAreaWidth = game.getMap().getWidth() * TILE_SIZE;
        gameAreaHeight = game.getMap().getHeight() * TILE_SIZE;

        Canvas canvas = new Canvas(gameAreaWidth, gameAreaHeight);
        gc = canvas.getGraphicsContext2D();
        BorderPane root = new BorderPane(canvas);
        scene = new Scene(root, gameAreaWidth, gameAreaHeight);

//        MapRenderer mapRenderer = new MapRenderer(gc, TILE_SIZE);
        Image grassTexture = new Image("grass.png");

        BackgroundImage backgroundImage = new BackgroundImage(
                grassTexture,
                BackgroundRepeat.REPEAT,   // horizontally
                BackgroundRepeat.REPEAT,   // vertically
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT     // keep the exact 64x64 pixel size (don't stretch)
        );
        root.setBackground(new Background(backgroundImage));
    }

    public void show() {
        stage.setScene(scene);
    }
}
