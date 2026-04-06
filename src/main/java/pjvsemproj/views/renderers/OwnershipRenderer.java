package pjvsemproj.views.renderers;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import pjvsemproj.models.entities.IGridEntity;
import pjvsemproj.models.entities.Ownable;

import java.util.List;

import static pjvsemproj.views.ViewConstants.TILE_SIZE;

public class OwnershipRenderer extends Renderer {

    private static final int boxXSize = 32;
    private static final int boxYSize = 4;

    public OwnershipRenderer(Canvas canvas) {
        super(canvas);
    }

    public <T extends Ownable & IGridEntity> void renderEntityOwner(T entity, Color color) {
        int viewX = getEntityViewX(entity);
        int viewY = getEntityViewY(entity);

        int boxXPos = viewX + (TILE_SIZE - boxXSize) / 2;
        int boxYPos = viewY + TILE_SIZE - boxYSize;

        gc.setFill(color);
        gc.fillRect(boxXPos, boxYPos, boxXSize, boxYSize);
    }

    public <T extends Ownable & IGridEntity> void renderEntitiesOwner(List<T> entities, Color color) {
        for (IGridEntity entity: entities) {
            renderEntityOwner(entity, color);
        }
    }

    public <T extends Ownable & IGridEntity> void clearEntityOwner(T entity) {
        int viewX = getEntityViewX(entity);
        int viewY = getEntityViewY(entity);

        gc.clearRect(viewX, viewY, TILE_SIZE, TILE_SIZE);
    }

    public <T extends Ownable & IGridEntity> void clearEntitiesOwner(List<T> entities) {
        for (T entity: entities) {
            clearEntityOwner(entity);
        }
    }
}
