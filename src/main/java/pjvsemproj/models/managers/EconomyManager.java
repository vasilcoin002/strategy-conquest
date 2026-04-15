package pjvsemproj.models.managers;

import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopType;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.managers.helpers.OwnershipHelper;

public class EconomyManager implements ITurnListener {

    private Player currentPlayer;

    public EconomyManager(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    @Override
    public void onTurnStart(Player activePlayer) {
        currentPlayer = activePlayer;
        currentPlayer.addGold(countProducedGold());
    }

    @Override
    public void onTurnEnd(Player endingPlayer) {

    }

    public int countProducedGold() {
        return currentPlayer.getCities().stream().reduce(
            0, (accumulator, city) ->
                accumulator + city.getGoldProducedPerRound(),
                Integer::sum);
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player player) {
        currentPlayer = player;
    }

    // TODO test
    public boolean upgradeCity(City city) {
        if (!canPlayerUpgradeCity(city, currentPlayer)) return false;

        currentPlayer.spendGold(city.getUpgradePrice());
        city.upgrade();
        return true;
    }

    public boolean canPlayerUpgradeCity(City city, Player player) {
        return city.canBeUpgraded()
                && city.getOwner() == player
                && player.getBalance() >= city.getUpgradePrice();
    }

    // TODO test
    public boolean buyTroopUnit(TroopType troopType, City city) {
        if (!canPlayerBuyTroopUnit(troopType, city, currentPlayer)) return false;

        currentPlayer.spendGold(troopType.getPrice());
        TroopUnit troopUnit = new TroopUnit(troopType, city);
        OwnershipHelper.addTroopUnitToPlayer(troopUnit, currentPlayer);
        return true;
    }

    public boolean canPlayerBuyTroopUnit(TroopType troopType, City city, Player player) {
        return player.getBalance() >= troopType.getPrice()
                && city.getOwner() == currentPlayer
                && !city.getTile().isBlocked();
    }
}
