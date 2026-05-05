package pjvsemproj.models.managers;

import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.managers.utils.OwnershipHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles conquest logic including:
 * - conquering cities
 * - detecting winner
 * - notifying listeners
 */
public class ConquestManager implements ITurnListener {

    private final List<Player> players;
    private Player currentPlayer;

    private final List<IWinListener> listeners = new ArrayList<>();


    public ConquestManager(List<Player> players, Player currentPlayer) {
        this.players = players;
        this.currentPlayer = currentPlayer;
    }

    @Override
    public void onTurnStart(Player activePlayer) {
        currentPlayer = activePlayer;
    }

    @Override
    public void onTurnEnd(Player endingPlayer) {

    }

    public void conquerCity(TroopUnit troopUnit, City city) {
        if (troopUnit.getOwner() == currentPlayer) {
            OwnershipHelper.transferCity(city, troopUnit.getOwner());
        }
        if (winnerExists()) {
            announceWinner(currentPlayer);
        }
    }

    // TODO implement winnerExists
    public boolean winnerExists() {
        return false;
    }

    public void addWinListener(IWinListener listener) {
        listeners.add(listener);
    }

    public void announceWinner(Player winner) {
        for (IWinListener listener : listeners) {
            listener.onWin(winner);
        }
    }
}
