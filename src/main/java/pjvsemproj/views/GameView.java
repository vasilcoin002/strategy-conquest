package pjvsemproj.views;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.views.renderers.GameRenderer;

import java.util.List;

import static pjvsemproj.views.ViewConstants.TILE_SIZE;

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

        GameRenderer gameRenderer = new GameRenderer(gc);
        gameRenderer.setBackground(root);

//        // TODO remove later
        List<Player> players = game.getPlayers();
        for (Player player: players) {
            Color color = getPlayerColor(game, player);
            gameRenderer.renderCities(player.getCities(), color);
        }
    }

    public void show() {
        stage.setScene(scene);
    }

    public Color getPlayerColor(Game game, Player player) {
        if (game.getPlayers().getFirst() == player) return Color.BLUE;
        return Color.RED;
    }
}
