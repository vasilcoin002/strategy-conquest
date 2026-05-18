package pjvsemproj.models.game.players;

import pjvsemproj.dto.*;
import pjvsemproj.models.entities.troopUnits.TroopType;
import pjvsemproj.models.services.LocalGameService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Handles the artificial intelligence logic for bot-controlled players.
 * <p>
 * This executor uses a stateless evaluation approach, reading the current game state
 * via Data Transfer Objects (DTOs) and issuing commands back to the {@link LocalGameService}.
 * It defines a strict priority of actions: Attack, Move, Buy, and Upgrade.
 */
public class BotExecutor {

    private static final Logger LOGGER = Logger.getLogger(BotExecutor.class.getName());
    private final LocalGameService service;
    private final Random random = new Random();

    /**
     * Constructs a new BotExecutor linked to a specific game service.
     *
     * @param service the local game service used to read state and execute actions
     */
    public BotExecutor(LocalGameService service) {
        this.service = service;
    }

    /**
     * Executes the complete sequence of AI actions for a single turn.
     * The bot follows a strict procedural priority:
     * 1. Attack enemies in range
     * 2. Move units towards targets
     * 3. Purchase new units (up to a cap)
     * 4. Upgrade existing cities
     */
    public void playTurnActionsOnly() {
        attackIfPossible();
        moveUnits();
        buyUnitIfPossible();
        upgradeCityIfPossible();
    }

    /**
     * Scans all bot-owned troops and commands them to attack if an enemy is within range.
     * <p>
     * AI Heuristic: The bot targets the weakest available enemy (lowest HP) to maximize kills.
     * Note: Uses Thread.sleep() to create an artificial delay between attacks.
     */
    private void attackIfPossible() {
        String botName = service.getCurrentPlayerDTO().name;
        GameDTO game = service.getGameDTO();

        for (EntityDTO entity : game.entities) {
            if (!(entity instanceof TroopUnitDTO troop)) continue;
            if (!botName.equals(troop.ownerName)) continue;

            Set<TileDTO> attackTiles = service.getBotAttackTiles(troop.id);
            TroopUnitDTO target = findWeakestEnemy(attackTiles, botName);
            LOGGER.info("Attack tiles count: " + attackTiles.size());

            if (target != null) {
                service.attack(troop.id, target.id);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * Evaluates the bot's economy and attempts to purchase a random affordable unit.
     * <p>
     * AI Heuristic: The bot imposes a hard cap of 4 troops. If it has fewer than 4,
     * it randomly selects a troop type it can afford and spawns it at the first available city.
     */
    private void buyUnitIfPossible() {
        String botName = service.getCurrentPlayerDTO().name;
        GameDTO game = service.getGameDTO();

        int botTroopsCount = countBotTroops(game, botName);

        if (botTroopsCount >= 4) {
            return;
        }

        TroopType troopToBuy = chooseRandomAffordableTroop(game, botName);

        if (troopToBuy == null) {
            return;
        }

        for (EntityDTO entity : game.entities) {
            if (!(entity instanceof CityDTO city)) {
                continue;
            }

            if (!botName.equals(city.ownerName)) {
                continue;
            }

            if (!city.canSpawnTroops) {
                continue;
            }

            boolean bought = service.buyUnit(city.id, troopToBuy.name());

            if (bought) {
                return;
            }
        }
    }

    /**
     * Scans the bot's cities and attempts to upgrade the first eligible one.
     */
    private void upgradeCityIfPossible() {
        String botName = service.getCurrentPlayerDTO().name;
        GameDTO game = service.getGameDTO();

        for(EntityDTO entity: game.entities) {
            if (!(entity instanceof CityDTO city)) continue;
            if (!botName.equals(city.ownerName)) continue;

            if (city.canBeUpgraded) {
                boolean upgraded = service.upgradeCity(city.id);
                if (upgraded) return;
            }
        }
    }

    /**
     * Locates the closest enemy entity (either a troop or a city) using Manhattan distance.
     *
     * @param troop   the bot troop looking for a target
     * @param game    the current game state DTO
     * @param botName the name of the current bot player
     * @return the nearest enemy EntityDTO, or {@code null} if no enemies exist
     */
    private EntityDTO findNearestEnemyOrCity(TroopUnitDTO troop, GameDTO game, String botName) {
        EntityDTO nearest = null;
        int bestDist = Integer.MAX_VALUE;

        for(EntityDTO entity: game.entities){
            if(botName.equals(entity.ownerName)){
                continue;
            }
            boolean isTroop = entity instanceof TroopUnitDTO;
            boolean isCity = entity instanceof CityDTO;

            if(!isTroop && !isCity){
                continue;
            }
            int dist = distance(troop.x, troop.y, entity.x, entity.y);
            if (dist < bestDist) {
                bestDist = dist;
                nearest = entity;
            }
        }
        return nearest;
    }

    /**
     * Iterates through all bot-owned troops and moves them towards the nearest enemy.
     * <p>
     * AI Heuristic: Evaluates all legal move tiles and chooses the one that minimizes
     * the Manhattan distance to the closest enemy target.
     * Note: Uses Thread.sleep() to create an artificial delay between movements.
     */
    private void moveUnits() {
        String botName = service.getCurrentPlayerDTO().name;
        GameDTO game = service.getGameDTO();

        for (EntityDTO entity : game.entities) {
            if (!(entity instanceof TroopUnitDTO troop)) continue;
            if (!botName.equals(troop.ownerName)) continue;

            Set<TileDTO> moves = service.getBotMoveTiles(troop.id);
            if (moves.isEmpty()) continue;

            EntityDTO target = findNearestEnemyOrCity(troop, game, botName);
            if (target == null) continue;

            TileDTO bestMove = null;
            int bestDistance = Integer.MAX_VALUE;

            for (TileDTO tile : moves) {
                int dist = distance(tile.x, tile.y, target.x, target.y);

                if (dist < bestDistance) {
                    bestDistance = dist;
                    bestMove = tile;
                }
            }

            if (bestMove != null) {
                service.moveUnit(troop.id, bestMove.x, bestMove.y);
                try {
                    Thread.sleep(900);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * Scans a set of attackable tiles to find the enemy troop with the lowest health.
     *
     * @param attackTiles the set of tiles within the troop's attack range
     * @param botName     the name of the current bot player
     * @return the enemy troop with the lowest HP, or {@code null} if none found
     */
    private TroopUnitDTO findWeakestEnemy(Set<TileDTO> attackTiles, String botName) {
        TroopUnitDTO weakestEnemy = null;
        int lowestHp = Integer.MAX_VALUE;

        for(TileDTO tile: attackTiles){
            TroopUnitDTO enemy = getEnemyTroopFromTile(tile, botName);

            if(enemy == null){
                continue;
            }

            if(enemy.hp < lowestHp){
                lowestHp = enemy.hp;
                weakestEnemy = enemy;
            }
        }
        return weakestEnemy;
    }

    /**
     * Extracts an enemy troop from a specific tile, if one exists.
     *
     * @param tile    the tile to inspect
     * @param botName the name of the current bot player
     * @return the enemy troop, or {@code null} if the tile contains no enemies
     */
    private TroopUnitDTO getEnemyTroopFromTile(TileDTO tile, String botName) {
        for(EntityDTO entity: tile.entities){
            if (entity instanceof TroopUnitDTO troop && !botName.equals(troop.ownerName)) {
                return troop;
            }
        }
        return null;
    }

    /**
     * Evaluates the bot's current balance and randomly selects a troop type it can afford.
     *
     * @param game    the current game state DTO
     * @param botName the name of the current bot player
     * @return a random affordable {@link TroopType}, or {@code null} if none can be afforded
     */
    private TroopType chooseRandomAffordableTroop(GameDTO game, String botName) {
        int balance = getBotBalance(game, botName);

        List<TroopType> availableTroops = new ArrayList<>();

        for (TroopType type : TroopType.values()) {
            if (balance >= type.getPrice()) {
                availableTroops.add(type);
            }
        }

        if (availableTroops.isEmpty()) {
            return null;
        }

        int index = random.nextInt(availableTroops.size());
        return availableTroops.get(index);
    }

    /**
     * Extracts the bot's current gold balance from the game state.
     *
     * @param game    the current game state DTO
     * @param botName the name of the current bot player
     * @return the bot's gold balance
     */
    private int getBotBalance(GameDTO game, String botName) {
        for (PlayerDTO player : game.players) {
            if (botName.equals(player.name)) {
                return player.balance;
            }
        }
        return 0;
    }

    /**
     * Counts the total number of troops currently owned by the bot.
     *
     * @param game    the current game state DTO
     * @param botName the name of the current bot player
     * @return the total troop count
     */
    private int countBotTroops(GameDTO game, String botName) {
        int count = 0;

        for (EntityDTO entity : game.entities) {
            if (entity instanceof TroopUnitDTO troop) {
                if (botName.equals(troop.ownerName)) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Calculates the Manhattan distance (grid distance) between two coordinates.
     *
     * @param x1 the starting X coordinate
     * @param y1 the starting Y coordinate
     * @param x2 the target X coordinate
     * @param y2 the target Y coordinate
     * @return the distance in tiles
     */
    private int distance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }
}