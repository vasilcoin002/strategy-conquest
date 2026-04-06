package pjvsemproj.views;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.views.renderers.CitiesRenderer;
import pjvsemproj.views.renderers.HpRenderer;
import pjvsemproj.views.renderers.OwnershipRenderer;
import pjvsemproj.views.renderers.TroopsRenderer;

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
        Canvas troopsCanvas = new Canvas(gameAreaWidth, gameAreaHeight);
        Canvas ownershipCanvas = new Canvas(gameAreaWidth, gameAreaHeight);
        Canvas hpCanvas = new Canvas(gameAreaWidth, gameAreaHeight);
        Pane root = new StackPane(citiesCanvas, troopsCanvas, ownershipCanvas, hpCanvas);
        scene = new Scene(root, gameAreaWidth, gameAreaHeight);

        CitiesRenderer citiesRenderer = new CitiesRenderer(citiesCanvas);
        TroopsRenderer troopsRenderer = new TroopsRenderer(troopsCanvas);
        OwnershipRenderer ownershipRenderer = new OwnershipRenderer(ownershipCanvas);
        HpRenderer hpRenderer = new HpRenderer(hpCanvas);
        setBackground(root);

//        // TODO remove later
        List<Player> players = game.getPlayers();
        for (Player player : players) {
            Color color = getPlayerColor(game, player);
            List<City> cities = player.getCities();
            List<TroopUnit> troops = player.getTroops();
            citiesRenderer.renderCities(cities);
            troopsRenderer.renderTroops(troops);
            ownershipRenderer.renderEntitiesOwner(cities, color);
            ownershipRenderer.renderEntitiesOwner(troops, color);
            hpRenderer.renderEntitiesHp(troops);
        }
    }

    public void show() {
        stage.setScene(scene);
    }

    public Color getPlayerColor(Game game, Player player) {
        if (game.getPlayers().getFirst() == player) return Color.BLUE;
        return Color.YELLOW;
    }

    public Background getBackground() {
        Image grassTexture = new Image("grass.png");

        BackgroundImage backgroundImage = new BackgroundImage(
                grassTexture,
                BackgroundRepeat.REPEAT,   // horizontally
                BackgroundRepeat.REPEAT,   // vertically
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT     // keep the exact 64x64 pixel size (don't stretch)
        );

        return new Background(backgroundImage);
    }

    public void setBackground(Pane pane) {
        pane.setBackground(getBackground());
    }
}
