package pjvsemproj.models.services;

import pjvsemproj.dto.*;
import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopType;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.maps.Tile;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.managers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

// TODO fix getAvailableTilesDTOForAttack (it returns empty list)
/**
 * Local (single-player) implementation of GameService.
 * <p>
 * Directly interacts with managers to execute game actions.
 */
public class LocalGameService extends AbstractGameService {

    public LocalGameService(Game game) {
        super(game);
    }

    @Override
    public void login(String playerName) {

    }

    @Override
    public void ready() {

    }

    @Override
    public void endTurn() {
        super.endTurn();

        // TODO extend it with bot's turn logic
    }
}
