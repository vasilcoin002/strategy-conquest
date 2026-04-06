package pjvsemproj.views.renderers;

import javafx.scene.canvas.Canvas;
import pjvsemproj.models.entities.cities.City;

import java.util.List;

import static pjvsemproj.views.ViewConstants.TILE_SIZE;

public class CityRenderer extends EntityRenderer {

    public CityRenderer(Canvas canvas) {
        super(canvas);
    }

    public void renderCity(City city) {
        renderEntity(city, TILE_SIZE, TILE_SIZE, "city.png");
    }

    public void renderCities(List<City> cities) {
        for (City city: cities) {
            renderCity(city);
        }
    }
}
