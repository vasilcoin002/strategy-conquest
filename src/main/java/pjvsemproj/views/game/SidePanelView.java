package pjvsemproj.views.game;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import pjvsemproj.dto.*;
import pjvsemproj.models.entities.troopUnits.TroopType;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static pjvsemproj.views.ViewConstants.GAME_SIDE_PANEL_WIDTH;

/**
 * UI panel displaying player info and actions.
 * Allows interaction with selected entities.
 */
public class SidePanelView {

    private final ScrollPane root;   // The new scrolling container

    private final Label currentPlayerLabel;
    private final Label ballanceLabel;
    private final HBox switcherBox;
    private final Label entityInfoLabel;
    private final VBox actionMenuBox;
    private final Button nextTurnBtn;

    private Runnable onQuitGameAction;
    private Runnable onSaveGameAction;
    private Consumer<EntityDTO> onEntitySelectedAction;
    private Runnable onNextTurnAction;
    private BiConsumer<String, String> onBuyUnitAction;
    private Consumer<String> onUpgradeCityAction;

    public SidePanelView() {
        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(20));
        contentBox.setStyle("-fx-background-color: #f4f4f4;");

        HBox topButtonsBox = new HBox(10);

        Button saveBtn = new Button("Save Game");
        saveBtn.setOnAction(e -> {
            if (onSaveGameAction != null) onSaveGameAction.run();
        });
        Button quitBtn = new Button("Quit game");
        quitBtn.setOnAction(e -> {
            if (onQuitGameAction != null) onQuitGameAction.run();
        });
        topButtonsBox.getChildren().addAll(saveBtn, quitBtn);

        currentPlayerLabel = new Label("Current Player: ");
        currentPlayerLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        ballanceLabel = new Label("Balance: ");
        ballanceLabel.setMinHeight(Region.USE_PREF_SIZE);

        switcherBox = new HBox(10);
        entityInfoLabel = new Label("Selected: None");
        entityInfoLabel.setMinHeight(Region.USE_PREF_SIZE);

        actionMenuBox = new VBox(10);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        nextTurnBtn = new Button("Next Turn");
        nextTurnBtn.setMaxWidth(Double.MAX_VALUE);
        nextTurnBtn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-base: #ff7e67; -fx-padding: 8px;");
        nextTurnBtn.setOnAction(e -> {
            if (onNextTurnAction != null) onNextTurnAction.run();
        });

        contentBox.getChildren().addAll(topButtonsBox, currentPlayerLabel, ballanceLabel, switcherBox, entityInfoLabel, actionMenuBox, spacer, nextTurnBtn);

        root = new ScrollPane(contentBox);
        root.setPrefWidth(GAME_SIDE_PANEL_WIDTH);

        root.setFitToWidth(true);  // Stops elements from squishing horizontally
        root.setFitToHeight(true); // Allows your 'spacer' to push the Next Turn button to the bottom!

        root.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);     // Hide horizontal scroll
        root.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Show vertical scroll only when full

        root.setStyle("-fx-background: #f4f4f4; -fx-background-color: transparent; -fx-border-color: #ccc; -fx-border-width: 0 0 0 1;");
    }

    /**
     * Reads the tile's entities list and builds the switcher if anything is there.
     */
    public void updateForTile(TileDTO tile) {
        switcherBox.getChildren().clear();
//        actionMenuBox.getChildren().clear();

        if (tile == null || tile.entities.isEmpty()) {
//            entityInfoLabel.setText("Selected: None");
            return;
        }

        CityDTO foundCity = null;
        TroopUnitDTO foundTroop = null;

        for (EntityDTO entity : tile.entities) {
            if (entity instanceof CityDTO city) {
                foundCity = city;
            } else if (entity instanceof TroopUnitDTO troopUnit) {
                foundTroop = troopUnit;
            }
        }

        if (foundCity != null) {
            final CityDTO finalCity = foundCity;
            Button viewCityBtn = new Button("View City");
            viewCityBtn.setMaxWidth(Double.MAX_VALUE);
            viewCityBtn.setOnAction(e -> onEntitySelectedAction.accept(finalCity));
            switcherBox.getChildren().add(viewCityBtn);
        }

        if (foundTroop != null) {
            final TroopUnitDTO finalTroop = foundTroop;
            Button viewTroopBtn = new Button("View Troop");
            viewTroopBtn.setMaxWidth(Double.MAX_VALUE);
            viewTroopBtn.setOnAction(e -> onEntitySelectedAction.accept(finalTroop));
            switcherBox.getChildren().add(viewTroopBtn);
        }
    }

    public ScrollPane getView() {    // Update return type
        return root;
    }

    public void updatePlayersBalance(List<PlayerDTO> players) {
        ballanceLabel.setText(
                "Balance:\n" +
                        players.getFirst().name + ": " + players.getFirst().balance + "\n" +
                        players.getLast().name + ": " + players.getLast().balance + "\n"
        );
    }

    public void updateCurrentPlayer(String currentPlayerName) {
        if (currentPlayerName != null) {
            currentPlayerLabel.setText("Current Player: " + currentPlayerName);
        }
    }

    public void clearEntityInfo() {
        entityInfoLabel.setText("Selected: None");
        actionMenuBox.getChildren().clear();
    }

    public void updateEntityInfo(EntityDTO entity, boolean isOwner) {
        actionMenuBox.getChildren().clear();

        if (entity instanceof CityDTO city) {
            String ownerName = city.ownerName != null ? city.ownerName : "Neutral";
            entityInfoLabel.setText("City (" + city.cityLevel + ")\nOwner: " + ownerName);

            if (isOwner) {
                if (city.canBeUpgraded) {
                    Button upgradeBtn = new Button("Upgrade City (" + city.upgradePrice + "g)");
                    upgradeBtn.setOnAction(e -> {
                        if (onUpgradeCityAction != null) onUpgradeCityAction.accept(city.id);
                    });
                    actionMenuBox.getChildren().add(upgradeBtn);
                }
                
                if (city.canSpawnTroops) {
                    Button buyMilitiaBtn = new Button("Buy " + TroopType.Militia.name() + ": " + TroopType.Militia.getPrice() + " gold");
                    Button buyInfantryBtn = new Button("Buy " + TroopType.Infantry.name() + ": " + TroopType.Infantry.getPrice() + " gold");
                    Button buyCavalryBtn = new Button("Buy " + TroopType.Cavalry.name() + ": " + TroopType.Cavalry.getPrice() + " gold");
                    Button buyArtilleryBtn = new Button("Buy " + TroopType.Artillery.name() + ": " + TroopType.Artillery.getPrice() + " gold");

                    buyMilitiaBtn.setOnAction(e -> triggerBuy(city.id, TroopType.Militia.name()));
                    buyInfantryBtn.setOnAction(e -> triggerBuy(city.id, TroopType.Infantry.name()));
                    buyCavalryBtn.setOnAction(e -> triggerBuy(city.id, TroopType.Cavalry.name()));
                    buyArtilleryBtn.setOnAction(e -> triggerBuy(city.id, TroopType.Artillery.name()));

                    actionMenuBox.getChildren().addAll(buyMilitiaBtn, buyInfantryBtn, buyCavalryBtn, buyArtilleryBtn);
                }
            }

        } else if (entity instanceof TroopUnitDTO troop) {
            entityInfoLabel.setText("Troop: " + troop.entityType +
                    "\nOwner: " + troop.ownerName +
                    "\nHP: " + troop.hp + " / " + troop.maxHp +
                    "\nDamage: " + troop.minDamage + "-" + troop.maxDamage +
                    "\nMoved: " + (troop.hasMovedThisTurn ? "Yes" : "No") +
                    "\nAttacked: " + (troop.hasAttackedThisTurn ? "Yes" : "No"));
        }
    }

    private void triggerBuy(String cityId, String troopType) {
        if (onBuyUnitAction != null) {
            onBuyUnitAction.accept(cityId, troopType);
        }
    }

    public void setOnBuyUnitAction(BiConsumer<String, String> onBuyUnitAction) {
        this.onBuyUnitAction = onBuyUnitAction;
    }

    public void setOnUpgradeCityAction(Consumer<String> onUpgradeCityAction) {
        this.onUpgradeCityAction = onUpgradeCityAction;
    }

    public void setNextTurnButtonDisabled(boolean disabled) {
        nextTurnBtn.setDisable(disabled);
    }

    public void setOnEntitySelectedAction(Consumer<EntityDTO> onEntitySelectedAction) {
        this.onEntitySelectedAction = onEntitySelectedAction;
    }

    public void setOnNextTurnAction(Runnable action) {
        this.onNextTurnAction = action;
    }

    public void setOnQuitGameAction(Runnable onQuitGameAction) {
        this.onQuitGameAction = onQuitGameAction;
    }

    public void setOnSaveGameAction(Runnable onSaveGameAction) {
        this.onSaveGameAction = onSaveGameAction;
    }
}