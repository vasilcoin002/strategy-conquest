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

    public MainMenuController(MainMenuView view, SceneDirector director) {
        this.view = view;
        this.director = director;

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

    // TODO add checking if client name is at least one of those in config
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

        boolean enableLogs = view.isLoggerEnabled();
        System.out.println("Triggered Local Game Load. Logging Enabled: " + enableLogs);

        director.showLocalGame(playerName);
    }

    /**
     * Handles multiplayer game start.
     */
    private void handleLoadMultiplayerGame() {
        String playerName = view.getPlayerName();

        if (playerName.isEmpty()) {
            view.showError("You must enter a player name before starting!");
            return;
        }
        view.clearError();

        boolean enableLogs = view.isLoggerEnabled();
        System.out.println("Triggered Multiplayer Game Load. Logging Enabled: " + enableLogs);

        director.showMultiplayerGame(playerName);
    }

    private void handleExit() {
        Platform.exit();
        System.exit(0);
    }
}