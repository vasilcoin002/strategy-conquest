package pjvsemproj.models.game.players;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void testGoldEconomy() {
        Player player = new HumanPlayer("TestPlayer", 50);

        assertEquals(50, player.getBalance());

        // Add gold
        player.addGold(20);
        assertEquals(70, player.getBalance());

        // Attempt to spend more than balance (should fail and not deduct)
        boolean spendSuccess1 = player.spendGold(100);
        assertFalse(spendSuccess1);
        assertEquals(70, player.getBalance());

        // Spend within balance (should succeed and deduct)
        boolean spendSuccess2 = player.spendGold(40);
        assertTrue(spendSuccess2);
        assertEquals(30, player.getBalance());
    }
}