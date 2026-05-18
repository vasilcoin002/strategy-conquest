package pjvsemproj.models.managers;

import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.managers.utils.OwnershipHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Handles conquest logic including conquering cities, detecting winners, and notifying listeners.
 * <p>
 * Evaluates structural ownership changes whenever units enter cells holding settlement items,
 * calculates victory criteria conditions, and updates match state listeners.
 */
public class ConquestManager implements ITurnListener {
    private static final Logger LOGGER = Logger.getLogger(ConquestManager.class.getName());

    private final List<Player> players;
    private Player currentPlayer;
    private final List<IWinListener> listeners = new ArrayList<>();

    /**
     * Constructs a conquest manager session layer tracking player rosters.
     *
     * @param players       The master registration list tracking all participating match players.
     * @param currentPlayer The currently active player profile structure assuming turn execution choices.
     */
    public ConquestManager(List<Player> players, Player currentPlayer) {
        this.players = players;
        this.currentPlayer = currentPlayer;
    }

    /**
     * Updates the local active player reference tracker upon turn initialization.
     *
     * @param activePlayer The {@link Player} profile assuming control over this turn.
     */
    @Override
    public void onTurnStart(Player activePlayer) {
        currentPlayer = activePlayer;
    }

    /**
     * Handles operations executed immediately after a participant concludes their action choices.
     *
     * @param endingPlayer The {@link Player} context wrapping up its turn block.
     */
    @Override
    public void onTurnEnd(Player endingPlayer) {

    }

    /**
     * Re-assigns structural ownership indices of settlement nodes to the capturing player.
     * <p>
     * Verifies player permissions, executes asset transfers via {@link OwnershipHelper},
     * evaluates elimination matches, and fires victory declarations if a winner is found.
     *
     * @param troopUnit The capturing {@link TroopUnit} instance occupying the settlement space.
     * @param city      The targeted {@link City} node structure undergoing transfer.
     */
    public void conquerCity(TroopUnit troopUnit, City city) {
        if (!troopUnit.getTile().equals(city.getTile())) {
            LOGGER.warning("Conquest rejected: unit " + troopUnit.getId()
                    + " is on pos ("
                    + troopUnit.getTile().getX() + ", " + troopUnit.getTile().getY()
                    + "), but the city is on pos ("
                    + city.getTile().getX() + ", " + city.getTile().getY()
                    + ")"
            );
            return;
        }

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

    /**
     * Evaluates victory condition patterns based on active settlement distributions.
     * <p>
     * A victory condition is met if the number of players owning at least one city
     * drops down to one or fewer.
     *
     * @return {@code true} if an elimination pattern matches victory conditions; {@code false} if multiple players still own cities.
     */
    public boolean winnerExists() {
        long playersWithCities = players.stream()
                .filter(player -> !player.getCities().isEmpty())
                .count();

        return playersWithCities <= 1;
    }

    /**
     * Registers an interface listener to receive callbacks when game-over victory conditions occur.
     *
     * @param listener The {@link IWinListener} subscriber monitoring match resolution events.
     */
    public void addWinListener(IWinListener listener) {
        listeners.add(listener);
    }

    /**
     * Determines the winner of the game.
     *
     * @return The winning {@link Player} instance still holding city properties;
     * or {@code null} if the game is ongoing or ends in a total draw.
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

    /**
     * Loops through the registered observer registry to broadcast victory notifications.
     *
     * @param winner The victorious {@link Player} profile instance to announce.
     */
    public void announceWinner(Player winner) {
        for (IWinListener listener : listeners) {
            listener.onWin(winner);
        }
    }
}