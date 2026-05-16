package pjvsemproj.models.services;

import pjvsemproj.dto.*;
import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopType;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.maps.Tile;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.managers.*;

import java.util.*;
import java.util.stream.Collectors;

public class AbstractGameService implements CoreGameService {

    protected final Game game;

    protected final MovementManager movementManager;
    protected final CombatManager combatManager;
    protected final EconomyManager economyManager;
    protected final TurnManager turnManager;
    protected final ConquestManager conquestManager;

    public AbstractGameService(Game game) {
        this.game = game;

        Player player1 = game.getPlayers().get(0);
        Player player2 = game.getPlayers().get(1);
        this.turnManager = new TurnManager(player1, player2, game.getCurrentPlayer());

        this.conquestManager = new ConquestManager(game.getPlayers(), turnManager.getCurrentPlayer());
        this.movementManager = new MovementManager(
                game.getMap(), turnManager.getCurrentPlayer(), conquestManager);

        this.combatManager = new CombatManager(this.game.getMap(), turnManager.getCurrentPlayer());
        this.economyManager = new EconomyManager(turnManager.getCurrentPlayer());

        this.turnManager.addTurnListener(new ITurnListener() {
            @Override
            public void onTurnStart(Player activePlayer) {
                game.setCurrentPlayer(activePlayer); // Syncs the state
            }

            @Override
            public void onTurnEnd(Player endingPlayer) {}
        });

        this.turnManager.addTurnListener(movementManager);
        this.turnManager.addTurnListener(combatManager);
        this.turnManager.addTurnListener(economyManager);
        this.turnManager.addTurnListener(conquestManager);
    }

    @Override
    public boolean moveUnit(String unitId, int x, int y) {
        TroopUnit troopUnit = findTroopById(unitId);

        Tile tile = game.getMap().getTile(x, y);
        Set<Tile> availableTiles = movementManager.getAvailableTilesForMovement(troopUnit);

        if (!availableTiles.contains(tile)) {
            return false;
        }
        return movementManager.moveTroopUnit(troopUnit, tile);
    }

    public void addWinListener(IWinListener listener) {
        conquestManager.addWinListener(listener);
    }

    @Override
    public boolean attack(String attackerId, String targetId) {
        TroopUnit attacker = findTroopById(attackerId);
        TroopUnit target = findTroopById(targetId);

        Set<TroopUnit> attackableTroops = combatManager.getAttackableTroops(attacker);

        if (!attackableTroops.contains(target)) {
            return false;
        }
        return combatManager.attackTroop(attacker, target);
    }

    @Override
    public boolean buyUnit(String cityId, String troopType) {
        City city = findCityById(cityId);

        TroopType type = TroopType.valueOf(troopType);
        return economyManager.buyTroopUnit(type, city);
    }

    public boolean buyUnitWithId(String unitId, String cityId, String troopType) {

        boolean idIsUnique = false;
        try {
            findTroopById(unitId);
        } catch (Exception e) {
            try {
                findCityById(unitId);
            } catch (Exception ex) {
                idIsUnique = true;
            }
        }
        if (!idIsUnique) {
            return false;
        }

        City city = findCityById(cityId);
        TroopType type = TroopType.valueOf(troopType);
        return economyManager.buyTroopUnitWithId(unitId, type, city);
    }

    @Override
    public boolean upgradeCity(String cityId) {
        City city = findCityById(cityId);

        return economyManager.upgradeCity(city);
    }

    @Override
    public void endTurn() {
        turnManager.endTurn();
    }

    protected TroopUnit findTroopById(String id) {
        for (Player player : game.getPlayers()) {
            for (TroopUnit troopUnit : player.getTroops()) {
                if (troopUnit.getId().equals(id)) {
                    return troopUnit;
                }
            }
        }
        throw new EntityNotFoundException("TROOP_UNIT", id);
    }

    protected City findCityById(String id) {
        for (Player player : game.getPlayers()) {
            for (City city : player.getCities()) {
                if (city.getId().equals(id)) {
                    return city;
                }
            }
        }
        throw new EntityNotFoundException("CITY", id);
    }

    @Override
    public GameDTO getGameDTO() {
        return new GameDTO(game);
    }

    @Override
    public EntityDTO getEntityDTO(String entityId) {
        for (Player player : game.getPlayers()) {
            for (TroopUnit troopUnit : player.getTroops()) {
                if (Objects.equals(troopUnit.getId(), entityId)) {
                    return new TroopUnitDTO(troopUnit);
                }
            }
            for (City city : player.getCities()) {
                if (Objects.equals(city.getId(), entityId)) {
                    return new CityDTO(city);
                }
            }
        }
        return null;
    }

    @Override
    public int getMapWidth() {
        return game.getMap().getWidth();
    }

    @Override
    public int getMapHeight() {
        return game.getMap().getHeight();
    }

    @Override
    public TileDTO getTileDTO(int x, int y) {
        return new TileDTO(game.getMap().getTile(x, y));
    }

    @Override
    public List<PlayerDTO> getPlayersDTO() {
        List<PlayerDTO> playerDTOs = new ArrayList<>();
        playerDTOs.add(new PlayerDTO(game.getPlayers().getFirst()));
        playerDTOs.add(new PlayerDTO(game.getPlayers().getLast()));
        return playerDTOs;
    }

    @Override
    public PlayerDTO getCurrentPlayerDTO() {
        return new PlayerDTO(turnManager.getCurrentPlayer());
    }

    @Override
    public Set<TileDTO> getAvailableTilesDTOForMovement(String unitId) {
        Set<Tile> availableTiles = movementManager
                .getAvailableTilesForMovement(findTroopById(unitId));
        return availableTiles.stream()
                .map(TileDTO::new)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<TileDTO> getAvailableTilesDTOForAttack(String unitId) {
        Set<Tile> availableTiles = combatManager
                .getAttackableTiles(findTroopById(unitId));
        return availableTiles.stream()
                .map(TileDTO::new)
                .collect(Collectors.toSet());
    }
}
