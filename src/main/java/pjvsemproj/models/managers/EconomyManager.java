package pjvsemproj.models.managers;

import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopType;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.managers.utils.GridPositionHelper;
import pjvsemproj.models.managers.utils.OwnershipHelper;


/**
 * Handles economic logic including:
 * - gold generation
 * - unit purchases
 * - city upgrades
 */
public class EconomyManager implements ITurnListener {

    private Player currentPlayer;

    /**
     * Constructs an EconomyManager for the specified starting player.
     *
     * @param currentPlayer the player whose turn is initially active
     */
    public EconomyManager(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    /**
     * Calculates and credits the generated gold to the active player's balance.
     *
     * @param activePlayer the player whose turn has just started
     */
    @Override
    public void onTurnStart(Player activePlayer) {
        currentPlayer = activePlayer;
        currentPlayer.addGold(countProducedGold());
    }

    @Override
    public void onTurnEnd(Player endingPlayer) {

    }

    /**
     * Calculates total gold produced by all cities owned by the current player.
     *
     * @return total gold income
     */
    public int countProducedGold() {
        return currentPlayer.getCities().stream().reduce(
                0, (accumulator, city) ->
                        accumulator + city.getGoldProducedPerRound(),
                Integer::sum);
    }

    /**
     * Retrieves the player currently recognized by the economy manager.
     * @return the active player
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Manually updates the active player context.
     * @param player the new active player
     */
    public void setCurrentPlayer(Player player) {
        currentPlayer = player;
    }

    /**
     * Attempts to upgrade a city.
     * Deducts the required gold from the player and advances the city level.
     *
     * @param city target city
     * @return true if upgrade was successful
     */
    public boolean upgradeCity(City city) {
        if (!canPlayerUpgradeCity(city, currentPlayer)) return false;

        currentPlayer.spendGold(city.getUpgradePrice());
        city.upgrade();
        return true;
    }

    /**
     * Verifies if a player has the funds and ownership rights to upgrade the city.
     *
     * @param city   the target city
     * @param player the player attempting the upgrade
     * @return {@code true} if all upgrade conditions are met
     */
    public boolean canPlayerUpgradeCity(City city, Player player) {
        return city.canBeUpgraded()
                && city.getOwner() == player
                && player.getBalance() >= city.getUpgradePrice();
    }


    /**
     * Attempts to buy a troop unit in a city.
     * Spends the player's gold, instantiates the unit, and places it on the board.
     *
     * @param troopType the type of unit to buy
     * @param city      the city where the unit will spawn
     * @return {@code true} if the purchase and placement were successful
     */
    public boolean buyTroopUnit(TroopType troopType, City city) {
        return this.buyTroopUnitWithId(java.util.UUID.randomUUID().toString(), troopType, city);
    }

    /**
     * Attempts to buy a troop unit with provided id in a city.
     */
    public boolean buyTroopUnitWithId(String id, TroopType troopType, City city) {
        if (!canPlayerBuyTroopUnit(troopType, city, currentPlayer)) return false;

        currentPlayer.spendGold(troopType.getPrice());
        TroopUnit troopUnit = new TroopUnit(id, troopType, city);
        OwnershipHelper.addTroopUnitToPlayer(troopUnit, currentPlayer);
        GridPositionHelper.placeEntity(troopUnit, city.getTile());
        return true;
    }

    /**
     * Verifies if a player has the funds, ownership, and space to buy a unit.
     *
     * @param troopType the type of unit to buy
     * @param city      the city where the unit will spawn
     * @param player    the player attempting the purchase
     * @return {@code true} if all purchase conditions are met
     */
    public boolean canPlayerBuyTroopUnit(TroopType troopType, City city, Player player) {
        return player.getBalance() >= troopType.getPrice()
                && city.getOwner() == currentPlayer
                && !city.getTile().isBlocked();
    }
}