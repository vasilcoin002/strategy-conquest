package pjvsemproj.views.renderers;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.maps.Tile;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.views.Color;

import static pjvsemproj.views.ViewConstants.TILE_SIZE;

public class GameRenderer {

    private final GraphicsContext gc;

    public GameRenderer(GraphicsContext gc) {
        this.gc = gc;
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

    public void renderCity(City city, Color color) {
        Tile cityTile = city.getTile();
        int screenPosX = cityTile.getX() * TILE_SIZE;
        int screenPosY = cityTile.getY() * TILE_SIZE;

        String cityColor = color.toString().toLowerCase();
        Image cityImage = new Image("city_" + cityColor + ".png");

        gc.drawImage(cityImage, screenPosX, screenPosY);
    }

    // TODO add method renderCities which will use renderCity method
}
