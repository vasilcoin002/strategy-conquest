package pjvsemproj.models.entities.troopUnits;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TroopUnitTest {

    private TroopUnit militia;

    @BeforeEach
    void setUp() {
        // Militia has 20 Max HP, 5 Min Damage, 10 Max Damage
        militia = new TroopUnit(TroopType.Militia, null, false, false);
    }

    @Test
    void testTakeDamageFloor() {
        // Taking normal damage
        militia.takeDamage(5);
        assertEquals(15, militia.getHealth());
        assertFalse(militia.isDead());

        // Taking overkill damage should floor at 0
        militia.takeDamage(50);
        assertEquals(0, militia.getHealth());
        assertTrue(militia.isDead());
    }

    @Test
    void testTakeHealCeiling() {
        militia.takeDamage(10);
        assertEquals(10, militia.getHealth());

        // Healing should cap at maxHealth (20)
        militia.takeHeal(50);
        assertEquals(20, militia.getHealth());
    }

    @Test
    void testCalculateDamageBounds() {
        int minDmg = militia.getMinDamage();
        int maxDmg = militia.getMaxDamage();

        // Run the randomizer multiple times to ensure bounds are respected
        for (int i = 0; i < 100; i++) {
            int damage = militia.calculateDamage();
            assertTrue(damage >= minDmg && damage <= maxDmg,
                    "Calculated damage " + damage + " is outside bounds [" + minDmg + ", " + maxDmg + "]");
        }
    }
}