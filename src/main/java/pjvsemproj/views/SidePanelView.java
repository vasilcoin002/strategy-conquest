package pjvsemproj.views;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import pjvsemproj.models.entities.IGridEntity;
import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopType;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.players.Player;

import java.util.List;

import static pjvsemproj.views.ViewConstants.GAME_SIDE_PANEL_WIDTH;

public class SidePanelView {

    private final VBox root;
    private final Label currentPlayerLabel;
    private final Label ballanceLabel;
    // TODO add entity switcher (if there is more than one entity on one tile)
    private final Label entityInfoLabel;
    private final VBox actionMenuBox;

    public SidePanelView() {
        root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setPrefWidth(GAME_SIDE_PANEL_WIDTH);
        root.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #ccc; -fx-border-width: 0 0 0 1;");

        currentPlayerLabel = new Label("Current Player: ");
        currentPlayerLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        ballanceLabel = new Label("Balance: ");

        entityInfoLabel = new Label("Selected: None");
        entityInfoLabel.setWrapText(true);

        actionMenuBox = new VBox(10);

        root.getChildren().addAll(currentPlayerLabel, ballanceLabel, entityInfoLabel, actionMenuBox);
    }

    // --- Public Methods for the GameView / Controller to call ---

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
}