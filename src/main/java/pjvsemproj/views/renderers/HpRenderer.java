package pjvsemproj.views.renderers;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import pjvsemproj.models.entities.Damageable;
import pjvsemproj.models.entities.IGridEntity;

import java.util.List;

import static pjvsemproj.views.ViewConstants.TILE_SIZE;

public class HpRenderer extends Renderer {

    int boxXSize = 4;
    int boxYSize = 40;

    public HpRenderer(Canvas canvas) {
        super(canvas);
    }

    public <T extends Damageable & IGridEntity> void renderEntityHp(T entity) {
        int viewX = getEntityViewX(entity);
        int viewY = getEntityViewY(entity);

        int boxXPos = viewX + TILE_SIZE - boxXSize;
        int boxYPos = viewY + (TILE_SIZE - boxYSize) / 2;

        gc.setFill(Color.RED);
        gc.fillRect(boxXPos, boxYPos, boxXSize, boxYSize);

        // TODO finish later when entity.getMaxHealth will be in interface
//        double percentHpLeft = entity.getHealth() / entity.getMaxHealth();
        double percentHpLeft = 0.25;

        gc.setFill(Color.GRAY);
        gc.fillRect(boxXPos, boxYPos, boxXSize, (1 - percentHpLeft) * boxYSize);
    }

    public <T extends Damageable & IGridEntity> void renderEntitiesHp(List<? extends T> entities) {
        for (T entity : entities) {
            renderEntityHp(entity);
        }
    }
}
