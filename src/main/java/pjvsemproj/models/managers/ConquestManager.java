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

    /**
     * Constructs a ConquestManager to track city ownership and victory conditions.
     *
     * @param players       the list of all players in the game
     * @param currentPlayer the player whose turn is currently active
     */
    public ConquestManager(List<Player> players, Player currentPlayer) {
        this.players = players;
        this.currentPlayer = currentPlayer;
    }

    /**
     * Updates the internal reference to the active player at the beginning of a turn.
     *
     * @param activePlayer the player whose turn has just started
     */
    @Override
    public void onTurnStart(Player activePlayer) {
        currentPlayer = activePlayer;
    }

    /**
     * Handles logic required at the end of a player's turn.
     * * @param endingPlayer the player whose turn has just concluded
     */
    @Override
    public void onTurnEnd(Player endingPlayer) {

    }

    /**
     * Attempts to transfer ownership of a city to the attacking unit's owner.
     * Evaluates victory conditions immediately after a successful conquest.
     *
     * @param attacker the military unit claiming the city
     * @param city     the city being conquered
     * @throws IllegalStateException if the attacker is not physically located on the city's tile
     */
    public void conquerCity(TroopUnit attacker, City city) {
        if (!attacker.getTile().equals(city.getTile())) {
            throw new IllegalStateException(
                    "Conquest failed: Attacker at " + attacker.getTile() +
                            " is not on the city tile at " + city.getTile()
            );
        }
        if (attacker.getOwner() == currentPlayer) {
            OwnershipHelper.transferCity(city, attacker.getOwner());
        }
        if (winnerExists()) {
            announceWinner(currentPlayer);
        }
    }

    /**
     * Evaluates whether the game has reached a victory state.
     * A winner exists if 1 or fewer players currently hold any cities.
     *
     * @return {@code true} if a player has met the victory condition
     */
    public boolean winnerExists() {
        long playersWithCities = players.stream()
                .filter(player -> !player.getCities().isEmpty())
                .count();

        return playersWithCities <= 1;
    }

    /**
     * Registers a new listener to receive notifications when a player wins the game.
     *
     * @param listener the observer implementing the IWinListener interface
     */
    public void addWinListener(IWinListener listener) {
        listeners.add(listener);
    }

    /**
     * Broadcasts the victory event to all registered listeners.
     *
     * @param winner the player who has won the game
     */
    public void announceWinner(Player winner) {
        for (IWinListener listener : listeners) {
            listener.onWin(winner);
        }
    }
}