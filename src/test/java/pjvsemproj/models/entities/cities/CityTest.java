package pjvsemproj.models.entities.cities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CityTest {

    @Test
    void testCityUpgradeSequence() {
        City city = new City(CityType.LEVEL_1);

        // Initial state
        assertTrue(city.canBeUpgraded());
        assertEquals(CityType.LEVEL_1, city.getCurrentLevel());

        // First upgrade
        city.upgrade();
        assertEquals(CityType.LEVEL_2, city.getCurrentLevel());
        assertTrue(city.canBeUpgraded());

        // Second upgrade
        city.upgrade();
        assertEquals(CityType.LEVEL_3, city.getCurrentLevel());
        assertFalse(city.canBeUpgraded());

        // Attempting to upgrade past the maximum level should do nothing and not throw errors
        city.upgrade();
        assertEquals(CityType.LEVEL_3, city.getCurrentLevel());
    }
}