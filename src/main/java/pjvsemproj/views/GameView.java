package pjvsemproj.views;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.views.renderers.GameRenderer;
import pjvsemproj.views.renderers.OwnershipRenderer;

import java.util.List;

import static pjvsemproj.views.ViewConstants.TILE_SIZE;

public class GameView {

    private final int gameAreaWidth;
    private final int gameAreaHeight;

    private final Stage stage;
    private final Scene scene;
    private final Game game;

    public GameView(Stage stage, Game game) {
        this.stage = stage;
        this.game = game;

        gameAreaWidth = game.getMap().getWidth() * TILE_SIZE;
        gameAreaHeight = game.getMap().getHeight() * TILE_SIZE;
        Canvas citiesCanvas = new Canvas(gameAreaWidth, gameAreaHeight);
        Canvas ownershipCanvas = new Canvas(gameAreaWidth, gameAreaHeight);
        Pane root = new StackPane(citiesCanvas, ownershipCanvas);
        scene = new Scene(root, gameAreaWidth, gameAreaHeight);

        GameRenderer gameRenderer = new GameRenderer(citiesCanvas);
        OwnershipRenderer ownershipRenderer = new OwnershipRenderer(ownershipCanvas);
        gameRenderer.setBackground(root);

//        // TODO remove later
        List<Player> players = game.getPlayers();
        for (Player player: players) {
            Color color = getPlayerColor(game, player);
            List<City> cities = player.getCities();
            List<TroopUnit> troops = player.getTroops();
            gameRenderer.renderCities(cities, color);
            ownershipRenderer.renderEntitiesOwner(cities, color);
            ownershipRenderer.renderEntitiesOwner(troops, color);
        }
    }

    public void show() {
        stage.setScene(scene);
    }

    public Color getPlayerColor(Game game, Player player) {
        if (game.getPlayers().getFirst() == player) return Color.BLUE;
        return Color.YELLOW;
    }
}
