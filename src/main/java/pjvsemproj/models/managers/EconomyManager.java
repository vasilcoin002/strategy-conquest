package pjvsemproj.models.managers;

import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.game.players.Player;

public class EconomyManager implements ITurnListener {
    private Player currentPlayer;

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

    public boolean upgradeCity(City city) {
        if (!canPlayerUpgradeCity(city, currentPlayer)) {
            return false;
        }

        currentPlayer.spendGold(city.getUpgradePrice());
        city.upgrade();
        return true;
    }

    public boolean canPlayerUpgradeCity(City city, Player player) {
        return city.canBeUpgraded()
                && city.getOwner() == player
                && player.getBalance() >= city.getUpgradePrice();
    }
}
