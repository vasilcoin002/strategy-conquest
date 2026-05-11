package pjvsemproj.models.services;

import pjvsemproj.dto.TileDTO;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.players.Player;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class AbstractClientService extends AbstractGameService implements ClientGameEngine {

    protected String clientName;
    protected Consumer<String> onGameOver;
    protected Runnable onBoardUpdated;

    public AbstractClientService(Game game) {
        super(game);

        super.addWinListener(winner -> {
            if (onGameOver != null) {
                onGameOver.accept(winner.getName());
            }
        });
    }

    @Override
    public void login(String playerName) {
        clientName = playerName;
    }

    // TODO check if method ready() is needed in service or we should move it out from it
    @Override
    public void ready() {

    }

    @Override
    public boolean isMyTurn() {
        return Objects.equals(
                turnManager.getCurrentPlayer().getName(),
                clientName
        );
    }

    @Override
    public void quit() {
        // assigning some default value
        Player winner = game.getPlayers().getFirst();
        // searching for the next player after current player
        for (Player player : game.getPlayers()) {
            if (player != turnManager.getCurrentPlayer()) {
                winner = player;
                break;
            }
        }

        System.out.println("Game quit");
    }

    @Override
    public void setOnGameOver(Consumer<String> callback) {
        this.onGameOver = callback;
    }

    @Override
    public String getClientName() {
        return clientName;
    }

    private boolean troopBelongsToClient(String unitId) {
        TroopUnit troopUnit = findTroopById(unitId);
        return Objects.equals(troopUnit.getOwner().getName(), clientName);
    }

    @Override
    public void setOnBoardUpdated(Runnable callback) {
        onBoardUpdated = callback;
    }

    protected void notifyBoardUpdated() {
        if (onBoardUpdated != null) {
            onBoardUpdated.run();
        }
    }

    @Override
    public boolean moveUnit(String unitId, int x, int y) {
        super.moveUnit(unitId, x, y);
        notifyBoardUpdated();
        return false;
    }

    @Override
    public boolean attack(String attackerId, String targetId) {
        super.attack(attackerId, targetId);
        notifyBoardUpdated();
        return false;
    }

    @Override
    public boolean buyUnit(String cityId, String troopType) {
        super.buyUnit(cityId, troopType);
        notifyBoardUpdated();
        return false;
    }

    @Override
    public boolean upgradeCity(String cityId) {
        super.upgradeCity(cityId);
        notifyBoardUpdated();
        return false;
    }

    @Override
    public void endTurn() {
        super.endTurn();
        notifyBoardUpdated();
    }

    @Override
    public Set<TileDTO> getAvailableTilesDTOForMovement(String unitId) {
        if (!troopBelongsToClient(unitId)) return new HashSet<>();
        return super.getAvailableTilesDTOForMovement(unitId);
    }

    @Override
    public Set<TileDTO> getAvailableTilesDTOForAttack(String unitId) {
        if (!troopBelongsToClient(unitId)) return new HashSet<>();
        return super.getAvailableTilesDTOForAttack(unitId);
    }

    protected Set<TileDTO> getUnrestrictedAttackTiles(String unitId) {
        return super.getAvailableTilesDTOForAttack(unitId);
    }

    protected Set<TileDTO> getUnrestrictedMovementTiles(String unitId) {
        return super.getAvailableTilesDTOForMovement(unitId);
    }
}
