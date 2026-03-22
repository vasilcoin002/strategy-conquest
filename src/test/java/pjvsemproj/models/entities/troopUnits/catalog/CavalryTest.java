package pjvsemproj.models.entities.troopUnits.catalog;

import org.junit.jupiter.api.*;
import pjvsemproj.models.entities.troopUnits.TroopType;
import pjvsemproj.models.entities.troopUnits.TroopUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CavalryTest {

    private static TroopUnit cavalry;
    private static TroopUnit enemy;

    @BeforeAll
    public static void init() {
        cavalry = new TroopUnit(TroopType.Cavalry, 0, 0);
        enemy = new TroopUnit(TroopType.Cavalry, 0, 1);
    }

    @BeforeEach
    public void refreshStamina() {
        cavalry.refreshStamina();
    }

    @Test
    @Order(1)
    public void moveUp_by1_ok() {
        cavalry.moveUp(1);

        assertEquals(1, cavalry.getY());
    }

    @Test
    @Order(2)
    public void moveDown_by1_ok() {
        cavalry.moveDown(1);

        assertEquals(0, cavalry.getY());
    }

    @Test
    @Order(3)
    public void moveLeft_by1_ok() {
        cavalry.moveLeft(1);

        assertEquals(-1, cavalry.getX());
    }


    @Test
    @Order(4)
    public void moveRight_by1_ok() {
        cavalry.moveRight(1);

        assertEquals(0, cavalry.getX());
    }

    @Test
    @Order(5)
    public void moveUp_byStaminaPlus1_doesntMove() {
        cavalry.moveUp(cavalry.getStamina() + 1);

        assertEquals(0, cavalry.getY());
    }

    @Test
    @Order(7)
    public void attackEnemy_void_enemyLostHealth() {
        int initialEnemyHealth = enemy.getHealth();

        cavalry.attack(enemy);

        assertFalse(enemy.getHealth() > initialEnemyHealth - cavalry.getMinDamage());
    }

    @Test
    public void moveOnEnemy_rightBy1_doesntMove() {
        cavalry.moveRight(1);

        assertEquals(0, cavalry.getX());
    }
}
