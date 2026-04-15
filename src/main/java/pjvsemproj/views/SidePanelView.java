package pjvsemproj.views;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import pjvsemproj.models.entities.IGridEntity;
import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopType;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.maps.Tile;
import pjvsemproj.models.game.players.Player;

import java.util.List;
import java.util.function.Consumer;

import static pjvsemproj.views.ViewConstants.GAME_SIDE_PANEL_WIDTH;


/**
 * UI panel displaying player info and actions.
 *
 * Allows interaction with selected entities.
 */

public class SidePanelView {

    private final VBox root;
    private final Button quitBtn;
    private final Label currentPlayerLabel;
    private final Label ballanceLabel;
    private final HBox switcherBox;
    // TODO add entity switcher (if there is more than one entity on one tile)
    private final Label entityInfoLabel;
    private final VBox actionMenuBox;
    private final Button nextTurnBtn;

    private Runnable onQuitGameAction;
    private Consumer<IGridEntity> onEntitySelectedAction;
    private Runnable onNextTurnAction;

    public SidePanelView() {
        root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setPrefWidth(GAME_SIDE_PANEL_WIDTH);
        root.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #ccc; -fx-border-width: 0 0 0 1;");

        quitBtn = new Button("Quit game");
        quitBtn.setOnAction(e -> onQuitGameAction.run());

        currentPlayerLabel = new Label("Current Player: ");
        currentPlayerLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        ballanceLabel = new Label("Balance: ");

        switcherBox = new HBox(10);

        entityInfoLabel = new Label("Selected: None");
        entityInfoLabel.setWrapText(true);

        actionMenuBox = new VBox(10);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        nextTurnBtn = new Button("Next Turn");
        nextTurnBtn.setMaxWidth(Double.MAX_VALUE);
        nextTurnBtn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-base: #ff7e67; -fx-padding: 8px;");

        nextTurnBtn.setOnAction(e -> onNextTurnAction.run());

        root.getChildren().addAll(quitBtn ,currentPlayerLabel, ballanceLabel, switcherBox, entityInfoLabel, actionMenuBox, spacer, nextTurnBtn);
    }

    /**
     * Reads the tile's getEntities() list and builds the switcher if anything is there.
     */
    public void updateForTile(Tile tile) {
        // Clear previous buttons and text
        switcherBox.getChildren().clear();
        actionMenuBox.getChildren().clear();

        // If the tile is empty or null, show nothing
        if (tile == null || tile.getEntities().isEmpty()) {
            entityInfoLabel.setText("Selected: none");
            return;
        }

        City foundCity = null;
        TroopUnit foundTroop = null;

        for (IGridEntity entity : tile.getEntities()) {
            if (entity instanceof City) {
                foundCity = (City) entity;
            } else if (entity instanceof TroopUnit) {
                foundTroop = (TroopUnit) entity;
            }
        }

        if (foundCity != null) {
            final City cityRef = foundCity; // 'effectively final' for the button action
            Button viewCityBtn = new Button("View City");
            viewCityBtn.setMaxWidth(Double.MAX_VALUE);
            viewCityBtn.setOnAction(e -> onEntitySelectedAction.accept(cityRef));
            switcherBox.getChildren().add(viewCityBtn);
        }

        if (foundTroop != null) {
            final TroopUnit troopRef = foundTroop;
            Button viewTroopBtn = new Button("View Troop");
            viewTroopBtn.setMaxWidth(Double.MAX_VALUE);
            viewTroopBtn.setOnAction(e -> onEntitySelectedAction.accept(troopRef));
            switcherBox.getChildren().add(viewTroopBtn);
        }
    }

    public VBox getView() {
        return root;
    }

    public void updatePlayersBalance(List<Player> players) {
        ballanceLabel.setText(
                "Balance:\n" +
                players.getFirst().getName() + ": " + players.getFirst().getBalance() + "\n" +
                players.getLast().getName() + ": " + players.getLast().getBalance() + "\n"
        );
    }

    public void updateCurrentPlayer(Player currentPlayer) {
        if (currentPlayer != null) {
            currentPlayerLabel.setText(
                    "Current Player: " + currentPlayer.getName()
            );
        }
    }

    public void clearEntityInfo() {
        entityInfoLabel.setText("Selected: None");
        actionMenuBox.getChildren().clear();
    }

    public void updateEntityInfo(IGridEntity entity) {
        actionMenuBox.getChildren().clear();

        if (entity instanceof City city) {
            String ownerName = city.getOwner() != null ? city.getOwner().getName() : "Neutral";
            entityInfoLabel.setText("City (" + city.getCityType() + ")\nOwner: " + ownerName);

            // TODO: Later, your Controller will provide these buttons!
            Button upgradeBtn = new Button("Upgrade City (" + city.getUpgradePrice() + "g)");
            actionMenuBox.getChildren().add(upgradeBtn);

            if (!city.getTile().isBlocked()) {
                Button buyMilitiaBtn = new Button("Buy " + TroopType.Militia.name()
                        + ": " + TroopType.Militia.getPrice() + " gold");
                Button buyInfantryBtn = new Button("Buy " + TroopType.Infantry.name()
                        + ": " + TroopType.Infantry.getPrice() + " gold");
                Button buyCavalryBtn = new Button("Buy " + TroopType.Cavalry.name()
                        + ": " + TroopType.Cavalry.getPrice() + " gold");
                Button buyArtilleryBtn = new Button("Buy " + TroopType.Artillery.name()
                        + ": " + TroopType.Artillery.getPrice() + " gold");

                actionMenuBox.getChildren().addAll(
                        buyMilitiaBtn, buyInfantryBtn,
                        buyCavalryBtn, buyArtilleryBtn
                );
            }

        } else if (entity instanceof TroopUnit troop) {
            entityInfoLabel.setText("Troop: " + troop.getName() +
                    "\nOwner: " + troop.getOwner().getName() +
                    "\nHP: " + troop.getHealth() + " / " + troop.getMaxHealth() +
                    "\nDamage: " + troop.getMinDamage() + "-" + troop.getMaxDamage() +
                    "\nMoved: " + (troop.hasMovedThisTurn() ? "Yes" : "No"));
        }
    }

    public void setNextTurnButtonDisabled(boolean disabled) {
        nextTurnBtn.setDisable(disabled);
    }

    public void setOnEntitySelectedAction(Consumer<IGridEntity> onEntitySelectedAction) {
        this.onEntitySelectedAction = onEntitySelectedAction;
    }

    public void setOnNextTurnAction(Runnable action) {
        this.onNextTurnAction = action;
    }

    public void setOnQuitGameAction(Runnable onQuitGameAction) {
        this.onQuitGameAction = onQuitGameAction;
    }
}