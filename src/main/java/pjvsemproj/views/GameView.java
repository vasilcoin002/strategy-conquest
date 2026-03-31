package pjvsemproj.views;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pjvsemproj.models.game.Game;
import pjvsemproj.views.renderers.GameRenderer;

public class GameView {

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

        GameRenderer mapRenderer = new GameRenderer(gc);
        mapRenderer.setBackground(root);
    }

    public void show() {
        stage.setScene(scene);
    }
}
