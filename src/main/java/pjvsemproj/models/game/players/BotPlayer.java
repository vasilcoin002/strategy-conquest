package pjvsemproj.models.game.players;

import pjvsemproj.models.managers.CombatManager;
import pjvsemproj.models.managers.EconomyManager;
import pjvsemproj.models.managers.MovementManager;
import pjvsemproj.models.managers.TurnManager;

/**
 * Represents an AI-controlled player.
 *
 * Uses managers to evaluate actions such as moving, attacking,
 * buying units and upgrading cities.
 */

public class BotPlayer extends Player {
    private final MovementManager movementManager;
    private final CombatManager combatManager;
    private final EconomyManager economyManager;
    private final TurnManager turnManager;

    public BotPlayer(String name, int initialBalance,
                     MovementManager movementManager,
                     CombatManager combatManager,
                     EconomyManager economyManager,
                     TurnManager turnManager) {
        super(name, initialBalance);
        this.movementManager = movementManager;
        this.combatManager = combatManager;
        this.economyManager = economyManager;
        this.turnManager = turnManager;
    }


    /**
     * Performs the full AI turn sequence.
     */

    private void makeTurn() {
    }

    private void tryAttack() {
    }

    /**
     * Attempts to move units strategically.
     */

    private void tryMove() {
    }
    /**
     * Ends the AI player's turn.
     */
    private void endAiTurn() {
    }
    /**
     * Attempts to purchase a new unit.
     */
    private void tryBuyUnit() {
    }
    /**
     * Attempts to upgrade one of the AI-controlled cities.
     */
    private void tryUpgradeCity() {
    }

}
