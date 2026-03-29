package pjvsemproj.models.managers;

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
}
