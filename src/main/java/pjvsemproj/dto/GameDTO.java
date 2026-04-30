package pjvsemproj.dto;

import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.players.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Container holding the raw data for a game level.
 */
public class GameDTO {
    public final int mapWidth;
    public final int mapHeight;
    public final List<EntityDTO> entities;
    public final List<PlayerDTO> players;
    public PlayerDTO currentPlayer;

    public GameDTO(int mapWidth, int mapHeight, List<EntityDTO> entities, List<PlayerDTO> players, PlayerDTO currentPlayer) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.entities = entities;
        this.players = players;
        this.currentPlayer = currentPlayer;
    }

    public GameDTO(Game game) {
        this.mapWidth = game.getMap().getWidth();
        this.mapHeight = game.getMap().getHeight();

        this.players = new ArrayList<>();
        this.players.add(new PlayerDTO(game.getPlayers().getFirst()));
        this.players.add(new PlayerDTO(game.getPlayers().getLast()));

        this.currentPlayer = new PlayerDTO(game.getCurrentPlayer());

        this.entities = new ArrayList<>();
        for (Player player: game.getPlayers()) {
            for (City city: player.getCities()) {
                this.entities.add(new CityDTO(city));
            }
            for (TroopUnit troopUnit: player.getTroops()) {
                this.entities.add(new TroopUnitDTO(troopUnit));
            }
        }
    }
}