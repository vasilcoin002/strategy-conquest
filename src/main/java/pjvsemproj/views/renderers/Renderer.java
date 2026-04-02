package pjvsemproj.views.renderers;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import pjvsemproj.models.entities.IGridEntity;
import pjvsemproj.models.game.maps.Tile;

public abstract class Renderer {
    protected final Canvas canvas;
    protected final GraphicsContext gc;

    public Renderer(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public GraphicsContext getGc() {
        return gc;
    }

    public Tile getTile(IGridEntity entity) {
        return entity.getTile();
    }

    public int getEntityX(IGridEntity entity) {
        return getTile(entity).getX();
    }

    public int getEntityY(IGridEntity entity) {
        return getTile(entity).getY();
    }
}
