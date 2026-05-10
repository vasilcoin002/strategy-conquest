package pjvsemproj.models.game.players;

import pjvsemproj.dto.*;
import pjvsemproj.models.entities.troopUnits.TroopType;
import pjvsemproj.models.services.LocalGameService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Random;

public class BotExecutor {

    private final LocalGameService service;
    private final Random random = new Random();

    public BotExecutor(LocalGameService service) {
        this.service = service;
    }

    public void playTurnActionsOnly() {
        attackIfPossible();
        moveUnits();
        buyUnitIfPossible();
        upgradeCityIfPossible();
    }

    private void attackIfPossible() {
        String botName = service.getCurrentPlayerDTO().name;
        GameDTO game = service.getGameDTO();

        for (EntityDTO entity : game.entities) {
            if (!(entity instanceof TroopUnitDTO troop)) continue;
            if (!botName.equals(troop.ownerName)) continue;
            Set<TileDTO> attackTiles = service.getBotAttackTiles(troop.id);
            TroopUnitDTO target = findWeakestEnemy(attackTiles, botName);
            System.out.println("Attack tiles count: " + attackTiles.size());

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

    private void buyUnitIfPossible(){
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

    private void upgradeCityIfPossible(){
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

    private EntityDTO findNearestEnemyOrCity(TroopUnitDTO troop, GameDTO game, String botName){
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

    private TroopUnitDTO findWeakestEnemy(Set<TileDTO> attackTiles, String botName){
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


    private TroopUnitDTO getEnemyTroopFromTile(TileDTO tile, String botName){
        for(EntityDTO entity: tile.entities){
            if (entity instanceof TroopUnitDTO troop && !botName.equals(troop.ownerName)) {
                return troop;
            }
        }
        return null;
    }

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

    private int getBotBalance(GameDTO game, String botName) {
        for (PlayerDTO player : game.players) {
            if (botName.equals(player.name)) {
                return player.balance;
            }
        }

        return 0;
    }

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

    private int distance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }
}
