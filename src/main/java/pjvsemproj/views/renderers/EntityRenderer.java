package pjvsemproj.views.renderers;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import pjvsemproj.models.entities.IGridEntity;
import pjvsemproj.models.game.maps.Tile;

import java.util.List;

import static pjvsemproj.views.ViewConstants.TILE_SIZE;

public abstract class EntityRenderer extends Renderer {

    public EntityRenderer(Canvas canvas) {
        super(canvas);
    }

    protected void renderEntity(IGridEntity entity, int xSize, int ySize, String imageName) {
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

    protected void renderEntities(List<? extends IGridEntity> entities, int xSize, int ySize, String imageName) {
        for (IGridEntity entity: entities) {
            renderEntity(entity, xSize, ySize, imageName);
        }
    }

    protected void clearEntity(IGridEntity entity) {
        int viewX = getEntityViewX(entity);
        int viewY = getEntityViewY(entity);

        gc.clearRect(viewX, viewY, TILE_SIZE, TILE_SIZE);
    }
}
