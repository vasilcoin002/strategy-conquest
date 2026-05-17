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
            Player actualWinner = getWinner();
            if (actualWinner != null) {
                announceWinner(actualWinner);
            }
        }
    }

    public boolean winnerExists() {
        long playersWithCities = players.stream()
                .filter(player -> !player.getCities().isEmpty())
                .count();

        return playersWithCities <= 1;
    }

    public void addWinListener(IWinListener listener) {
        listeners.add(listener);
    }

    /**
     * Determines the winner of the game.
     * @return The winning Player, or null if the game is still ongoing or ends in a total draw.
     */
    public Player getWinner() {
        if (!winnerExists()) {
            return null;
        }

        // Return the first player found that still owns at least one city
        return players.stream()
                .filter(player -> !player.getCities().isEmpty())
                .findFirst()
                .orElse(null);
    }

    public void announceWinner(Player winner) {
        for (IWinListener listener : listeners) {
            listener.onWin(winner);
        }
    }
}
