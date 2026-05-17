package pjvsemproj.dto;

import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.players.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Container holding the raw data for a game level.
 * <p>
 * Acts as the master snapshot wrapper aggregating dimensions, registered player configurations,
 * active turn tracking parameters, and all board entity states for file-system serialization or transmission.
 */
public class GameDTO {
    public Integer mapWidth;
    public Integer mapHeight;
    public List<EntityDTO> entities;
    public List<PlayerDTO> players;
    public String currentPlayerName;

    /**
     * Constructs a master game data container with explicit structural parameters.
     *
     * @param mapWidth          The total number of columns representing map grid horizontal dimensions.
     * @param mapHeight         The total number of rows representing map grid vertical dimensions.
     * @param entities          The collective list of all active asset structures and units currently residing on the map.
     * @param players           The tracking registry list of participating profiles configured for this match session.
     * @param currentPlayerName The username string identifying the player whose turn action sequence is currently open.
     */
    public GameDTO(
            int mapWidth, int mapHeight, List<EntityDTO> entities,
            List<PlayerDTO> players, String currentPlayerName
    ) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.entities = entities;
        this.players = players;
        this.currentPlayerName = currentPlayerName;
    }

    /**
     * Contextual hydration constructor that translates a live simulation game state down into an isolated data transfer map.
     * <p>
     * Extracts map sizes, maps the human and bot participants list, resolves turn positions, and performs
     * nested iteration sweeps to convert all cities and troops into their DTO wrappers.
     *
     * @param game The live running {@link Game} engine simulation instance to extract complete session properties from.
     */
    public GameDTO(Game game) {
        this.mapWidth = game.getMap().getWidth();
        this.mapHeight = game.getMap().getHeight();

        this.players = new ArrayList<>();
        this.players.add(new PlayerDTO(game.getPlayers().getFirst()));
        this.players.add(new PlayerDTO(game.getPlayers().getLast()));

        this.currentPlayerName = new PlayerDTO(game.getCurrentPlayer()).name;

        this.entities = new ArrayList<>();
        for (Player player : game.getPlayers()) {
            for (City city : player.getCities()) {
                this.entities.add(new CityDTO(city));
            }
            for (TroopUnit troopUnit : player.getTroops()) {
                this.entities.add(new TroopUnitDTO(troopUnit));
            }
        }
    }

    /**
     * Default no-argument constructor.
     * <p>
     * Provided to accommodate standard reflective serialization and deserialization frameworks (e.g., GSON).
     */
    public GameDTO() {

    }
}