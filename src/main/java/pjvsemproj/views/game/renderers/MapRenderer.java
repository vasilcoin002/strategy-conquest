package pjvsemproj.views.game.renderers;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import pjvsemproj.dto.CityDTO;
import pjvsemproj.dto.EntityDTO;
import pjvsemproj.dto.TileDTO;
import pjvsemproj.dto.TroopUnitDTO;
import pjvsemproj.models.entities.IGridEntity;
import pjvsemproj.models.entities.Ownable;
import pjvsemproj.models.entities.troopUnits.TroopType;
import pjvsemproj.models.entities.troopUnits.TroopUnit;

import java.util.*;

import static pjvsemproj.views.ViewConstants.TILE_SIZE;


/**
 * Responsible for rendering map entities (cities, troops, overlays).
 *
 * Uses JavaFX GraphicsContext for drawing.
 */
public class MapRenderer extends Renderer {

    private final Map<TroopType, String> troopsImageNames = new HashMap<>();

    public MapRenderer() {

        TroopType[] types = TroopType.values();
        for (TroopType type: types) {
            troopsImageNames.put(type, type.toString().toLowerCase() + ".png");
        }
    }

    private void renderEntity(GraphicsContext gc, EntityDTO entity, int xSize, int ySize, String imageName) {
        int viewX = getEntityViewX(entity);
        int viewY = getEntityViewY(entity);

        int middleOfTileX = viewX + TILE_SIZE/2;
        int middleOfTileY = viewY + TILE_SIZE/2;

        Image cityImage = new Image(imageName);

        gc.drawImage(
                cityImage,
                middleOfTileX - xSize / 2,
                middleOfTileY - ySize / 2,
                xSize, ySize
        );
    }

    private void clearEntity(GraphicsContext gc, EntityDTO entity) {
        int viewX = getEntityViewX(entity);
        int viewY = getEntityViewY(entity);

        clear(gc, viewX, viewY, TILE_SIZE, TILE_SIZE);
    }

    public void renderCity(GraphicsContext gc, CityDTO city, Color ownerColor) {
        renderEntity(gc, city, TILE_SIZE, TILE_SIZE, "city.png");
        renderEntityOwner(gc, city, ownerColor);
    }

    public void renderCities(GraphicsContext gc, List<CityDTO> cities, Map<String, Color> ownersColors) {
        for (CityDTO city: cities) {
            renderCity(gc, city, ownersColors.get(city.ownerName));
        }
    }

    public void renderTroop(GraphicsContext gc, TroopUnitDTO troopUnit, Color ownerColor) {
        String imageName = troopsImageNames.get(TroopType.valueOf(troopUnit.entityType));
        renderEntity(
                gc,
                troopUnit,
                TILE_SIZE * 3 / 4,
                TILE_SIZE * 3 / 4,
                imageName
        );
        renderEntityOwner(gc, troopUnit, ownerColor);
        renderTroopUnitHp(gc, troopUnit);
    }

    public void renderTroops(GraphicsContext gc, List<TroopUnitDTO> troops, Map<String, Color> ownersColors) {
        for (TroopUnitDTO troopUnit: troops) {
            renderTroop(gc, troopUnit, ownersColors.get(troopUnit.ownerName));
        }
    }

    public void clearTroopUnit(GraphicsContext gc, TroopUnitDTO troopUnit) {
        clearEntity(gc, troopUnit);
    }

    public void renderEntityOwner(GraphicsContext gc, EntityDTO entity, Color color) {
        int boxXSize = 32;
        int boxYSize = 4;

        int viewX = getEntityViewX(entity);
        int viewY = getEntityViewY(entity);

        int boxXPos = viewX + (TILE_SIZE - boxXSize) / 2;
        int boxYPos = viewY + TILE_SIZE - boxYSize;

        gc.setFill(color);
        gc.fillRect(boxXPos, boxYPos, boxXSize, boxYSize);
    }

    private void clearEntityOwner(GraphicsContext gc, EntityDTO entity) {
        int viewX = getEntityViewX(entity);
        int viewY = getEntityViewY(entity);

        gc.clearRect(viewX, viewY, TILE_SIZE, TILE_SIZE);
    }

    public void renderTroopUnitHp(GraphicsContext gc, TroopUnitDTO troopUnit) {
        int boxXSize = 4;
        int boxYSize = 40;

        int viewX = getEntityViewX(troopUnit);
        int viewY = getEntityViewY(troopUnit);

        int boxXPos = viewX + TILE_SIZE - boxXSize;
        int boxYPos = viewY + (TILE_SIZE - boxYSize) / 2;

        gc.setFill(Color.RED);
        gc.fillRect(boxXPos, boxYPos, boxXSize, boxYSize);

        double percentHpLeft = (double) troopUnit.hp / troopUnit.maxHp;

        gc.setFill(Color.GRAY);
        gc.fillRect(boxXPos, boxYPos, boxXSize, (1 - percentHpLeft) * boxYSize);
    }

    private void clearTroopUnitHp(GraphicsContext gc, TroopUnitDTO troopUnit) {
        int viewX = getEntityViewX(troopUnit);
        int viewY = getEntityViewY(troopUnit);

        gc.clearRect(viewX, viewY, TILE_SIZE, TILE_SIZE);
    }

    public void renderTile(GraphicsContext gc, TileDTO tile, Map<String, Color> ownersColor) {
        int viewX = tile.x * TILE_SIZE;
        int viewY = tile.y * TILE_SIZE;

        for (EntityDTO entity: tile.entities) {
            if (entity instanceof CityDTO city) {
                renderCity(gc, city, ownersColor.get(city.ownerName));
            } else if (entity instanceof TroopUnitDTO troopUnit) {
                renderTroop(gc, troopUnit, ownersColor.get(troopUnit.ownerName));
            }
        }

        gc.clearRect(viewX, viewY, TILE_SIZE, TILE_SIZE);
    }

    public void clearTile(GraphicsContext gc, TileDTO tile) {
        int viewX = tile.x * TILE_SIZE;
        int viewY = tile.y * TILE_SIZE;

        gc.clearRect(viewX, viewY, TILE_SIZE, TILE_SIZE);
    }

    public void renderSelection(GraphicsContext gc, EntityDTO entity) {
        int viewX = getEntityViewX(entity);
        int viewY = getEntityViewY(entity);

        Image image = new Image("selection_circle.png");
        gc.drawImage(image, viewX, viewY, TILE_SIZE, TILE_SIZE);
    }

    public void clearSelection(GraphicsContext gc) {
        clear(gc);
    }

    public void renderAvailableMove(GraphicsContext gc, TileDTO tile) {
        int viewX = tile.x * TILE_SIZE;
        int viewY = tile.y * TILE_SIZE;

        Image image = new Image("move_circle.png");
        gc.drawImage(image, viewX, viewY, TILE_SIZE, TILE_SIZE);
    }

    public void renderAvailableMoves(GraphicsContext gc, Set<TileDTO> tiles) {
        for (TileDTO tile: tiles) {
            renderAvailableMove(gc, tile);
        }
    }

    // TODO implement
    public void renderAvailableAttacks(GraphicsContext gc, Set<TileDTO> tiles) {

    }

    // TODO implement
    public void renderAvailableAttack(GraphicsContext gc, TileDTO tile) {

    }
}
