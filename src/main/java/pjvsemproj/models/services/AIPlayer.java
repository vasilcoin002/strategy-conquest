package pjvsemproj.models.services;

import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.managers.CombatManager;
import pjvsemproj.models.managers.EconomyManager;
import pjvsemproj.models.managers.MovementManager;
import pjvsemproj.models.managers.TurnManager;

public class AIPlayer {
    private final Game game;
    private final Player aiPlayer;

    private final MovementManager movementManager;
    private final CombatManager combatManager;
    private final EconomyManager economyManager;
    private final TurnManager turnManager;

    public AIPlayer(Game game, Player aiPlayer, MovementManager movementManager, CombatManager combatManager, EconomyManager economyManager, TurnManager turnManager) {
        this.game = game;
        this.aiPlayer = aiPlayer;
        this.movementManager = movementManager;
        this.combatManager = combatManager;
        this.economyManager = economyManager;
        this.turnManager = turnManager;
    }

    private void makeTurn(){}
    private void tryAttack(){}
    private void tryMove(){}
    private void endAiTurn(){}
    private void tryBuyUnit(){}
    private void tryUpgradeCity(){}

}
