package pjvsemproj.views.renderers;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import pjvsemproj.models.game.maps.Tile;

import java.util.Set;

import static pjvsemproj.views.ViewConstants.TILE_SIZE;

public class MovementRenderer extends Renderer {

    public MovementRenderer(Canvas canvas) {
        super(canvas);
    }

    public void renderAvailableTile(Tile tile) {
        int viewX = tile.getX() * TILE_SIZE;
        int viewY = tile.getY() * TILE_SIZE;

        Image image = new Image("move_circle.png");
        gc.drawImage(image, viewX, viewY, TILE_SIZE, TILE_SIZE);
    }

    public void renderAvailableTiles(Set<Tile> tiles) {
        for (Tile tile: tiles) {
            renderAvailableTile(tile);
        }
    }
}
