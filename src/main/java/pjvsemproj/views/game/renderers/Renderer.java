package pjvsemproj.views.game.renderers;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import pjvsemproj.dto.EntityDTO;

import static pjvsemproj.views.ViewConstants.TILE_SIZE;

/**
 * Base renderer class providing helper methods for drawing entities.
 * <p>
 * Offers foundational utilities to convert logical board game grid indices into JavaFX screen pixel spaces
 * and handles raw canvas cleanup sweeps.
 */
public abstract class Renderer {

    /**
     * Extracts the logical map column coordinate index of a given entity.
     *
     * @param entity The data transfer object representation of the target map entity.
     * @return The horizontal column game grid cell index position.
     */
    public int getEntityGameX(EntityDTO entity) {
        return entity.x;
    }

    /**
     * Translates an entity's logical column position into a JavaFX screen coordinate pixel value.
     *
     * @param entity The data transfer object representation of the target map entity.
     * @return The starting screen pixel position on the horizontal X component axis.
     */
    public int getEntityViewX(EntityDTO entity) {
        return getEntityGameX(entity) * TILE_SIZE;
    }

    /**
     * Extracts the logical map row coordinate index of a given entity.
     *
     * @param entity The data transfer object representation of the target map entity.
     * @return The vertical row game grid cell index position.
     */
    public int getEntityGameY(EntityDTO entity) {
        return entity.y;
    }

    /**
     * Translates an entity's logical row position into a JavaFX screen coordinate pixel value.
     *
     * @param entity The data transfer object representation of the target map entity.
     * @return The starting screen pixel position on the vertical Y component axis.
     */
    public int getEntityViewY(EntityDTO entity) {
        return getEntityGameY(entity) * TILE_SIZE;
    }

    /**
     * Erases and resets the entire area bounded by the current canvas element context.
     *
     * @param gc The active JavaFX graphical context linked to the target map viewport layer.
     */
    public void clear(GraphicsContext gc) {
        Canvas canvas = gc.getCanvas();
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        clear(gc, 0, 0, width, height);
    }

    /**
     * Clears a rectangular slice of the canvas using specific coordinate bounds.
     *
     * @param gc The active JavaFX graphical context handling active drawing loops.
     * @param x1 The upper-left boundary starting coordinate position on the X axis.
     * @param y1 The upper-left boundary starting coordinate position on the Y axis.
     * @param x2 The width dimension pixel range to clear outward from the starting position.
     * @param y2 The height dimension pixel range to clear outward from the starting position.
     */
    public void clear(GraphicsContext gc, double x1, double y1, double x2, double y2) {
        gc.clearRect(x1, y1, x2, y2);
    }
}