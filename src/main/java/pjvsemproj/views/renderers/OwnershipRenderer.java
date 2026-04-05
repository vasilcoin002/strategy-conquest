package pjvsemproj.views.renderers;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import pjvsemproj.models.entities.IGridEntity;

import java.util.List;

import static pjvsemproj.views.ViewConstants.TILE_SIZE;

public class OwnershipRenderer extends Renderer {

    public OwnershipRenderer(Canvas canvas) {
        super(canvas);
    }

    public void renderEntityOwner(IGridEntity entity, Color color) {
        int viewX = getEntityViewX(entity);
        int viewY = getEntityViewY(entity);

        gc.setFill(color);
        gc.fillRect(viewX+16, viewY + TILE_SIZE - 4, TILE_SIZE-32, 4);
    }

    public void renderEntitiesOwner(List<? extends IGridEntity> entities, Color color) {
        for (IGridEntity entity: entities) {
            renderEntityOwner(entity, color);
        }
    }
}
