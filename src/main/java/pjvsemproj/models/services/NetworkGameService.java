package pjvsemproj.models.services;

import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.managers.utils.GridPositionHelper;
import pjvsemproj.models.managers.utils.OwnershipHelper;
import pjvsemproj.server.Client;
import pjvsemproj.server.NetworkGameListener;
import pjvsemproj.server.Protocol;

// TODO add enemy wins when I close window
// TODO implement upgrade city
/**
 * Network-based implementation of GameService.
 * Sends commands to the server instead of executing them locally.
 */
public class NetworkGameService extends AbstractClientService {

    private final Client client;

    public NetworkGameService(Client client, Game game) {
        super(game);
        this.client = client;

        client.setServerEventListener(new NetworkGameListener(this) {
        });
    }

    @Override
    public void login(String playerName) {
        client.sendToServer(Protocol.LOGIN, playerName);
        super.login(playerName);
    }

    @Override
    public void ready() {
        client.ready();
        super.ready();
    }

    @Override
    public boolean moveUnit(String unitId, int x, int y) {
        client.moveUnit(unitId, x, y);
        return true;
    }

    @Override
    public boolean attack(String attackerId, String targetId) {
        client.attack(attackerId, targetId);
        return true;
    }

    @Override
    public boolean buyUnit(String cityId, String troopType) {
        client.buyUnit(cityId, troopType);
        return true;
    }

    @Override
    public boolean upgradeCity(String cityId) {
        client.upgradeCity(cityId);
        return true;
    }

    @Override
    public void endTurn() {
        // Send the request to the server.
        // DO NOT call super.endTurn() here! Let the server confirm the turn change first.
        client.endTurn();
    }

    @Override
    public void quit() {
        client.quit();
//        super.quit();
    }

    public void applyServerMove(String unitId, int x, int y) {
        super.moveUnit(unitId, x, y);
    }

    public void applyServerAttack(String attackerId, String targetId, int newHp){
        super.attack(attackerId, targetId, newHp);
    }

    public void applyServerUnitDeath(String unitId) {
        TroopUnit troop = findTroopById(unitId);

        OwnershipHelper.removeTroopUnitFromPlayer(troop);
        GridPositionHelper.removeFromBoard(troop);

        notifyBoardUpdated();
    }

    public void applyServerTurnStarted(String playerName){
        super.endTurn();
    }

    public void applyServerCityUpgrade(String cityId) {
        super.upgradeCity(cityId);
    }

    public void applyServerUnitBought(
            String cityId,
            String unitId,
            String troopType
    ) {
        super.buyUnitWithId(unitId, cityId, troopType);
    }

    public void applyServerGameOver(String winnerName) {
        System.out.println("Service triggering UI for " + winnerName);

        // Use the exact variable name that AbstractGameService/CoreGameService uses.
        // It might be 'onGameOver', 'onGameOverListener', or a protected method.
        if (this.onGameOver != null) {
            this.onGameOver.accept(winnerName);
        } else {
            // IF YOU SEE THIS PRINT, GO BACK TO STEP 1.
            System.err.println("CRITICAL UI ERROR: The onGameOver listener is NULL. GameController did not connect it!");
        }
    }

    /**
     * Explicitly tells the server that the game was naturally won,
     * prompting the server to shut down.
     */
    public void notifyServerOfWin(String winnerName) {
        client.sendToServer(Protocol.GAME_OVER, winnerName);
    }
}