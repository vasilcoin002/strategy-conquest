package pjvsemproj.views.renderers;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import pjvsemproj.models.entities.troopUnits.TroopType;
import pjvsemproj.models.entities.troopUnits.TroopUnit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pjvsemproj.views.ViewConstants.TILE_SIZE;

public class TroopsRenderer extends EntityRenderer {

    private final Map<TroopType, String> imageNames = new HashMap<>();

    public TroopsRenderer(Canvas canvas) {
        super(canvas);

        TroopType[] types = TroopType.values();
        for (TroopType type: types) {
            imageNames.put(type, type.toString().toLowerCase() + ".png");
        }
    }

    public void renderTroop(TroopUnit troopUnit) {
        String imageName = imageNames.get(TroopType.valueOf(troopUnit.getName()));
        renderEntity(
                troopUnit,
                TILE_SIZE * 3 / 4,
                TILE_SIZE * 3 / 4,
                imageName
        );
    }

    public void renderTroops(List<TroopUnit> troops) {
        for (TroopUnit troopUnit: troops) {
            renderTroop(troopUnit);
        }
    }

    public void clearTroopUnit(TroopUnit troopUnit) {
        clearEntity(troopUnit);
    }
}
