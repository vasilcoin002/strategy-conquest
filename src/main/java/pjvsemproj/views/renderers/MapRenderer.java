package pjvsemproj.views.renderers;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import pjvsemproj.models.entities.Damageable;
import pjvsemproj.models.entities.IGridEntity;
import pjvsemproj.models.entities.Ownable;
import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopType;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.maps.Tile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static pjvsemproj.views.ViewConstants.TILE_SIZE;

public class MapRenderer extends Renderer {

    private final Map<TroopType, String> troopsImageNames = new HashMap<>();

    public MapRenderer() {

        TroopType[] types = TroopType.values();
        for (TroopType type: types) {
            troopsImageNames.put(type, type.toString().toLowerCase() + ".png");
        }
    }

    private void renderEntity(GraphicsContext gc, IGridEntity entity, int xSize, int ySize, String imageName) {
        Tile cityTile = entity.getTile();
        int viewX = cityTile.getX() * TILE_SIZE;
        int viewY = cityTile.getY() * TILE_SIZE;

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

    private void clearEntity(GraphicsContext gc, IGridEntity entity) {
        int viewX = getEntityViewX(entity);
        int viewY = getEntityViewY(entity);

        clear(gc, viewX, viewY, TILE_SIZE, TILE_SIZE);
    }

    public void renderCity(GraphicsContext gc, City city, Color ownerColor) {
        renderEntity(gc, city, TILE_SIZE, TILE_SIZE, "city.png");
        renderEntityOwner(gc, city, ownerColor);
    }

    public void renderCities(GraphicsContext gc, List<City> cities, Color ownerColor) {
        for (City city: cities) {
            renderCity(gc, city, ownerColor);
        }
    }

    public void renderTroop(GraphicsContext gc, TroopUnit troopUnit, Color ownerColor) {
        String imageName = troopsImageNames.get(TroopType.valueOf(troopUnit.getName()));
        renderEntity(
                gc,
                troopUnit,
                TILE_SIZE * 3 / 4,
                TILE_SIZE * 3 / 4,
                imageName
        );
        renderEntityOwner(gc, troopUnit, ownerColor);
        renderEntityHp(gc, troopUnit);
    }

    public void renderTroops(GraphicsContext gc, List<TroopUnit> troops, Color ownerColor) {
        for (TroopUnit troopUnit: troops) {
            renderTroop(gc, troopUnit, ownerColor);
        }
    }

    public void clearTroopUnit(GraphicsContext gc, TroopUnit troopUnit) {
        clearEntity(gc, troopUnit);
    }

    public <T extends Ownable & IGridEntity> void renderEntityOwner(GraphicsContext gc, T entity, Color color) {
        int boxXSize = 32;
        int boxYSize = 4;

        int viewX = getEntityViewX(entity);
        int viewY = getEntityViewY(entity);

        int boxXPos = viewX + (TILE_SIZE - boxXSize) / 2;
        int boxYPos = viewY + TILE_SIZE - boxYSize;

        gc.setFill(color);
        gc.fillRect(boxXPos, boxYPos, boxXSize, boxYSize);
    }

    private <T extends Ownable & IGridEntity> void clearEntityOwner(GraphicsContext gc, T entity) {
        int viewX = getEntityViewX(entity);
        int viewY = getEntityViewY(entity);

        gc.clearRect(viewX, viewY, TILE_SIZE, TILE_SIZE);
    }

    public <T extends Damageable & IGridEntity> void renderEntityHp(GraphicsContext gc, T entity) {
        int boxXSize = 4;
        int boxYSize = 40;

        int viewX = getEntityViewX(entity);
        int viewY = getEntityViewY(entity);

        int boxXPos = viewX + TILE_SIZE - boxXSize;
        int boxYPos = viewY + (TILE_SIZE - boxYSize) / 2;

        gc.setFill(Color.RED);
        gc.fillRect(boxXPos, boxYPos, boxXSize, boxYSize);

        double percentHpLeft = (double) entity.getHealth() / entity.getMaxHealth();

        gc.setFill(Color.GRAY);
        gc.fillRect(boxXPos, boxYPos, boxXSize, (1 - percentHpLeft) * boxYSize);
    }

    private <T extends Damageable & IGridEntity> void clearEntityHp(GraphicsContext gc, T entity) {
        int viewX = getEntityViewX(entity);
        int viewY = getEntityViewY(entity);

        gc.clearRect(viewX, viewY, TILE_SIZE, TILE_SIZE);
    }

    public void renderSelection(GraphicsContext gc, IGridEntity entity) {
        int viewX = getEntityViewX(entity);
        int viewY = getEntityViewY(entity);

        Image image = new Image("selection_circle.png");
        gc.drawImage(image, viewX, viewY, TILE_SIZE, TILE_SIZE);
    }

    public void clearSelection(GraphicsContext gc) {
        clear(gc);
    }

    public void renderAvailableMove(GraphicsContext gc, Tile tile) {
        int viewX = tile.getX() * TILE_SIZE;
        int viewY = tile.getY() * TILE_SIZE;

        Image image = new Image("move_circle.png");
        gc.drawImage(image, viewX, viewY, TILE_SIZE, TILE_SIZE);
    }

    public void renderAvailableMoves(GraphicsContext gc, Set<Tile> tiles) {
        for (Tile tile: tiles) {
            renderAvailableMove(gc, tile);
        }
    }

    // TODO implement
    public void renderAvailableAttacks(GraphicsContext gc, Set<Tile> tiles) {

    }

    // TODO implement
    public void renderAvailableAttack(GraphicsContext gc, Tile tile) {

    }
}
