package pjvsemproj.views.renderers;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import pjvsemproj.models.entities.IGridEntity;

import static pjvsemproj.views.ViewConstants.TILE_SIZE;

public class SelectionRenderer extends Renderer {
    public SelectionRenderer(Canvas canvas) {
        super(canvas);
    }

    public void renderSelection(IGridEntity entity) {
        int viewX = getEntityViewX(entity);
        int viewY = getEntityViewY(entity);

        Image image = new Image("selection_circle.png");
        gc.drawImage(image, viewX, viewY, TILE_SIZE, TILE_SIZE);
    }
}
