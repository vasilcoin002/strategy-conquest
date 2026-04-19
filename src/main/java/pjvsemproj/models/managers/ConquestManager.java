package pjvsemproj.models.managers;

import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.players.Player;

import java.util.ArrayList;
import java.util.List;

public class ConquestManager {

    private final List<Player> players;
    private Player currentPlayer;

    private final List<IWinListener> listeners = new ArrayList<>();


    public ConquestManager(List<Player> players, Player currentPlayer) {
        this.players = players;
        this.currentPlayer = currentPlayer;
    }

    public void conquerCity(TroopUnit troopUnit, City city) {

    }

    public boolean checkIfWinnerExists() {
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
