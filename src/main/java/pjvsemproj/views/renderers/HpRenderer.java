package pjvsemproj.views.renderers;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import pjvsemproj.models.entities.Damageable;
import pjvsemproj.models.entities.IGridEntity;

import java.util.List;

import static pjvsemproj.views.ViewConstants.TILE_SIZE;

public class HpRenderer extends Renderer {

    public HpRenderer(Canvas canvas) {
        super(canvas);
    }

    public <T extends Damageable & IGridEntity> void renderEntityHp(T entity) {
        int viewX = getEntityViewX(entity);
        int viewY = getEntityViewY(entity);

        // TODO finish later
//        double percentHpLeft = entity.getHealth() / entity.getMaxHealth();

        gc.setFill(Color.GRAY);
        gc.fillRect(viewX + TILE_SIZE - 4, viewY + 12, 4, 40);

    }

    public <T extends Damageable & IGridEntity> void renderEntitiesHp(List<? extends T> entities) {
        for (T entity: entities) {
            renderEntityHp(entity);
        }
    }
}
