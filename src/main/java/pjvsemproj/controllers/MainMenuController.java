package pjvsemproj.controllers;

import javafx.application.Platform;
import pjvsemproj.views.MainMenuView;


/**
 * Controller for main menu UI.
 *
 * Handles menu actions such as:
 * - starting game
 * - enabling logging
 * - exiting application
 */
public class MainMenuController {
    private final MainMenuView view;
    private final SceneDirector director;
    private String clientName;

    public MainMenuController(MainMenuView view, SceneDirector director, String clientName) {
        this.view = view;
        this.director = director;
        this.clientName = clientName;

        bindActions();
    }

    /**
     * Binds UI buttons to actions.
     */
    private void bindActions() {
        view.setOnLoadLocalGameAction(this::handleLoadLocalGame);
        view.setOnLoadMultiplayerGameAction(this::handleLoadMultiplayerGame);
        view.setOnExitAction(this::handleExit);
    }

    /**
     * Handles local game start.
     */
    private void handleLoadLocalGame() {
        String playerName = view.getPlayerName();

        if (playerName.isEmpty()) {
            view.showError("You must enter a player name before starting!");
            return;
        }
        view.clearError();

        this.clientName = playerName;

        boolean enableLogs = view.isLoggerEnabled();
        System.out.println("Triggered Local Game Load. Logging Enabled: " + enableLogs);

        try {
            director.showLocalGame(clientName);
        } catch (Exception e) {
            view.showError("Error loading game: " + e.getMessage());
            System.err.println("Game load failed: " + e.getMessage());
        }
    }

    /**
     * Handles multiplayer game start.
     */
    private void handleLoadMultiplayerGame() {
        String playerName = view.getPlayerName();

        if (playerName == null || playerName.trim().isEmpty()) {
            view.showError("Please enter a name first.");
            return;
        }

        this.clientName = playerName;

        // FIX: Pass the name variable into the director here!
        director.showMultiplayerLobby(playerName);
    }

    private void handleExit() {
        Platform.exit();
        System.exit(0);
    }
}