package pjvsemproj.config;

import pjvsemproj.dto.*;
import pjvsemproj.models.entities.troopUnits.TroopType;

public class GameConfigSanitizer {

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
                        } catch (IllegalArgumentException ignored) {}
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