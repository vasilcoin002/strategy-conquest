package pjvsemproj.models.game.players;

import pjvsemproj.models.managers.CombatManager;
import pjvsemproj.models.managers.EconomyManager;
import pjvsemproj.models.managers.MovementManager;
import pjvsemproj.models.managers.TurnManager;

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

    private void makeTurn() {
    }

    private void tryAttack() {
    }

    private void tryMove() {
    }

    private void endAiTurn() {
    }

    private void tryBuyUnit() {
    }

    private void tryUpgradeCity() {
    }

}
