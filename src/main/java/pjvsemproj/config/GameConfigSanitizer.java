package pjvsemproj.config;

import pjvsemproj.dto.*;
import pjvsemproj.models.entities.troopUnits.TroopType;

/**
 * Normalizes a game configuration by injecting fallback values into missing or null fields.
 * <p>
 * This pre-processor prepares a raw {@link GameDTO} for strict validation. It does not enforce
 * gameplay rules or assert enum correctness (which is strictly delegated to the validator).
 * Instead, it repairs fragmented data structures commonly resulting from manual save file edits
 * or network payload omissions, ensuring the resulting DTO is structurally complete enough
 * to prevent null pointer exceptions during model hydration.
 */
public class GameConfigSanitizer {

    /**
     * Traverses the configuration object and applies predefined constants to any missing fields,
     * including map dimensions, player economics, and specific entity states.
     * @param game The configuration payload to be sanitized in-place.
     */
    public void sanitize(GameDTO game) {
        if (game.mapWidth == null || game.mapWidth <= 0) game.mapWidth = ConfigDefaultValues.DEFAULT_MAP_WIDTH;
        if (game.mapHeight == null || game.mapHeight <= 0) game.mapHeight = ConfigDefaultValues.DEFAULT_MAP_HEIGHT;

        if (game.players != null) {
            for (PlayerDTO player : game.players) {
                if (player.balance == null) {
                    player.balance = ConfigDefaultValues.DEFAULT_INIT_BALANCE;
                }
            }
        }

        if (game.currentPlayerName == null || game.currentPlayerName.isBlank()) {
            if (game.players != null && !game.players.isEmpty()) {
                game.currentPlayerName = game.players.get(0).name;
            }
        }

        if (game.entities != null) {
            for (EntityDTO entity : game.entities) {
                if (entity instanceof CityDTO city) {
                    if (city.cityLevel == null || city.cityLevel.isBlank()) {
                        city.cityLevel = ConfigDefaultValues.DEFAULT_CITY_LEVEL;
                    }
                }
                else if (entity instanceof TroopUnitDTO troop) {
                    if (troop.hp == null) {
                        try {
                            TroopType type = TroopType.valueOf(troop.entityType);
                            troop.hp = ConfigDefaultValues.getDefaultHp(type);
                        } catch (IllegalArgumentException ignored) {
                            // Ignored intentionally: If the entityType is an invalid string,
                            // we leave HP null and allow the downstream GameConfigValidator
                            // to intercept this exception.
                        }
                    }

                    if (troop.hasMovedThisTurn == null) {
                        troop.hasMovedThisTurn = ConfigDefaultValues.DEFAULT_HAS_MOVED_THIS_TURN;
                    }
                    if (troop.hasAttackedThisTurn == null) {
                        troop.hasAttackedThisTurn = ConfigDefaultValues.DEFAULT_HAS_ATTACKED_THIS_TURN;
                    }
                }
            }
        }
    }
}