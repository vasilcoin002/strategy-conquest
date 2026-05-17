package pjvsemproj.views.game.renderers;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import pjvsemproj.dto.CityDTO;
import pjvsemproj.dto.EntityDTO;
import pjvsemproj.dto.TileDTO;
import pjvsemproj.dto.TroopUnitDTO;
import pjvsemproj.models.entities.troopUnits.TroopType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static pjvsemproj.views.ViewConstants.TILE_SIZE;

/**
 * Responsible for rendering map entities (cities, troops, overlays).
 * <p>
 * Uses JavaFX GraphicsContext for drawing. Handles rendering player colors,
 * fractional unit health indicators, and placement highlights for available move and attack commands.
 */
public class MapRenderer extends Renderer {

    private final Map<TroopType, String> troopsImageNames = new HashMap<>();

    /**
     * Constructs a map renderer instance and populates image asset filename keys.
     * <p>
     * Iterates through available unit types to systematically link asset names dynamically.
     */
    public MapRenderer() {
        TroopType[] types = TroopType.values();
        for (TroopType type: types) {
            troopsImageNames.put(type, type.toString().toLowerCase() + ".png");
        }
    }

    /**
     * Internal abstraction to draw an image scaled and precisely centered within an entity's destination tile cell.
     *
     * @param gc        The target JavaFX graphical context surface.
     * @param entity    The target entity specifying positions to align against.
     * @param xSize     The horizontal pixel scale target width to assign to the drawn asset.
     * @param ySize     The vertical pixel scale target height to assign to the drawn asset.
     * @param imageName The filename location token mapping the targeted image file resource path.
     */
    private void renderEntity(GraphicsContext gc, EntityDTO entity, int xSize, int ySize, String imageName) {
        int viewX = getEntityViewX(entity);
        int viewY = getEntityViewY(entity);

        int middleOfTileX = viewX + TILE_SIZE/2;
        int middleOfTileY = viewY + TILE_SIZE/2;

        Image cityImage = new Image(imageName);

        gc.drawImage(
                cityImage,
                middleOfTileX - (double) xSize / 2,
                middleOfTileY - (double) ySize / 2,
                xSize, ySize
        );
    }

    /**
     * Draws an individual city structure asset alongside its matching participant ownership color indicator.
     *
     * @param gc         The target JavaFX graphical context surface.
     * @param city       The settlement data container tracking level ranking properties.
     * @param ownerColor The color assigned to the user profile controlling this settlement.
     */
    public void renderCity(GraphicsContext gc, CityDTO city, Color ownerColor) {
        renderEntity(gc, city, TILE_SIZE, TILE_SIZE, "city.png");
        renderEntityOwner(gc, city, ownerColor);
    }

    /**
     * Batch-renders a list collection of city structural nodes across the active map grid.
     *
     * @param gc           The target JavaFX graphical context surface.
     * @param cities       The collection checklist of settlement DTO models to process.
     * @param ownersColors A map pairing participant identity names onto distinct JavaFX color values.
     */
    public void renderCities(GraphicsContext gc, List<CityDTO> cities, Map<String, Color> ownersColors) {
        for (CityDTO city: cities) {
            renderCity(gc, city, ownersColors.get(city.ownerName));
        }
    }

    /**
     * Draws an individual combat unit sprite, its ownership marker, and its current health points.
     *
     * @param gc         The target JavaFX graphical context surface.
     * @param troopUnit  The data transfer model container storing statistical health and type values.
     * @param ownerColor The color assigned to the user profile controlling this unit division.
     */
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

    /**
     * Batch-renders a list collection of mobile combat unit groups across the map canvas.
     *
     * @param gc           The target JavaFX graphical context surface.
     * @param troops       The collection checklist of mobile troop DTO models to process.
     * @param ownersColors A map pairing participant identity names onto distinct JavaFX color values.
     */
    public void renderTroops(GraphicsContext gc, List<TroopUnitDTO> troops, Map<String, Color> ownersColors) {
        for (TroopUnitDTO troopUnit: troops) {
            renderTroop(gc, troopUnit, ownersColors.get(troopUnit.ownerName));
        }
    }

    /**
     * Renders a solid color horizontal rectangle box at the baseline edge of a tile to specify faction ownership.
     *
     * @param gc     The target JavaFX graphical context surface.
     * @param entity The target map entity requesting ownership highlight tracking.
     * @param color  The specific color of the player currently controlling this asset.
     */
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

    /**
     * Draws a vertical health bar on the right side of a unit, showing its current relative hit-points.
     * <p>
     * Fills a background column with red, then layers a gray overlay proportionally to visually
     * map lost health values.
     *
     * @param gc        The target JavaFX graphical context surface.
     * @param troopUnit The troop unit whose fractional hit points ratio is being rendered.
     */
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

    /**
     * Renders all structural and unit components aggregated inside an individual cell snapshot.
     *
     * @param gc          The target JavaFX graphical context surface.
     * @param tile        The tile DTO container tracking nested city or military components.
     * @param ownersColor A map pairing participant names onto distinct JavaFX color configurations.
     */
    public void renderTile(GraphicsContext gc, TileDTO tile, Map<String, Color> ownersColor) {
        for (EntityDTO entity: tile.entities) {
            if (entity instanceof CityDTO city) {
                renderCity(gc, city, ownersColor.get(city.ownerName));
            } else if (entity instanceof TroopUnitDTO troopUnit) {
                renderTroop(gc, troopUnit, ownersColor.get(troopUnit.ownerName));
            }
        }
    }

    /**
     * Clears pixel rendering attributes inside a single specific grid coordinate bounding box region.
     *
     * @param gc   The target JavaFX graphical context surface.
     * @param tile The specific structural tile block cell area destination to erase.
     */
    public void clearTile(GraphicsContext gc, TileDTO tile) {
        int viewX = tile.x * TILE_SIZE;
        int viewY = tile.y * TILE_SIZE;

        gc.clearRect(viewX, viewY, TILE_SIZE, TILE_SIZE);
    }

    /**
     * Superimposes a selection halo highlight overlay graphics asset on top of a locked entity.
     *
     * @param gc     The target JavaFX graphical context surface.
     * @param entity The targeted entity model to encircle.
     */
    public void renderSelection(GraphicsContext gc, EntityDTO entity) {
        int viewX = getEntityViewX(entity);
        int viewY = getEntityViewY(entity);

        Image image = new Image("selection_circle.png");
        gc.drawImage(image, viewX, viewY, TILE_SIZE, TILE_SIZE);
    }

    /**
     * Iterates through a set checklist of cell positions to superimpose movement range highlighting overlays.
     *
     * @param gc    The target JavaFX graphical context surface.
     * @param tiles A {@link Set} of reachable navigation tile coordinate blocks to highlight.
     */
    public void renderAvailableMoves(GraphicsContext gc, Set<TileDTO> tiles) {
        for (TileDTO tile: tiles) {
            renderAvailableMove(gc, tile);
        }
    }

    /**
     * Draws an individual move option highlight icon ring onto a single specific cell location.
     *
     * @param gc   The target JavaFX graphical context surface.
     * @param tile The specific coordinate tile destination block to highlight.
     */
    public void renderAvailableMove(GraphicsContext gc, TileDTO tile) {
        int viewX = tile.x * TILE_SIZE;
        int viewY = tile.y * TILE_SIZE;

        Image image = new Image("move_circle.png");
        gc.drawImage(image, viewX, viewY, TILE_SIZE, TILE_SIZE);
    }

    /**
     * Iterates through a set checklist of cell positions to superimpose offensive combat threat range overlays.
     *
     * @param gc    The target JavaFX graphical context surface.
     * @param tiles A {@link Set} of targetable combat engagement tile coordinate blocks to highlight.
     */
    public void renderAvailableAttacks(GraphicsContext gc, Set<TileDTO> tiles) {
        for (TileDTO tile: tiles) {
            renderAvailableAttack(gc, tile);
        }
    }

    /**
     * Draws an individual combat engagement highlight icon ring onto a single specific cell location.
     *
     * @param gc   The target JavaFX graphical context surface.
     * @param tile The specific coordinate tile destination block containing an enemy asset to highlight.
     */
    public void renderAvailableAttack(GraphicsContext gc, TileDTO tile) {
        int viewX = tile.x * TILE_SIZE;
        int viewY = tile.y * TILE_SIZE;

        Image image = new Image("attack_circle.png");
        gc.drawImage(image, viewX, viewY, TILE_SIZE, TILE_SIZE);
    }
}