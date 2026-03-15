package pjvsemproj.models.entities.troopUnits.catalog;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class KnightsTest {

    private static Knights knights;
    private static Knights enemy;

    @BeforeAll
    public static void init() {
        knights = new Knights(0, 0);
        enemy = new Knights(0, 1);
    }

    @BeforeEach
    public void refreshStamina() {
        knights.refreshStamina();
    }

    @Test
    @Order(1)
    public void moveUp_by1_ok() {
        knights.moveUp(1);

        assertEquals(1, knights.getY());
    }

    @Test
    @Order(2)
    public void moveDown_by1_ok() {
        knights.moveDown(1);

        assertEquals(0, knights.getY());
    }

    @Test
    @Order(3)
    public void moveLeft_by1_ok() {
        knights.moveLeft(1);

        assertEquals(-1, knights.getX());
    }


    @Test
    @Order(4)
    public void moveRight_by1_ok() {
        knights.moveRight(1);

        assertEquals(0, knights.getX());
    }

    @Test
    @Order(5)
    public void moveUp_byStaminaPlus1_doesntMove() {
        knights.moveUp(knights.getStamina() + 1);

        assertEquals(0, knights.getY());
    }

    @Test
    @Order(7)
    public void attackEnemy_void_enemyLostHealth() {
        int initialEnemyHealth = enemy.getHealth();

        knights.attack(enemy);

        assertFalse(enemy.getHealth() > initialEnemyHealth - knights.getMinDamage());
    }

    @Test
    public void moveOnEnemy_rightBy1_doesntMove() {
        knights.moveRight(1);

        assertEquals(0, knights.getX());
    }
}
