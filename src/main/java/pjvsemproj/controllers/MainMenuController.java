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
    /**
     * Handles local game start.
     */
    private void handleLoadLocalGame() {
        // 1. Read the state of the toggle
        boolean enableLogs = view.isLoggerEnabled();

        // 2. Apply it to your logging system (pseudo-code depending on your setup)
        // LoggerConfig.setEnabled(enableLogs);

        System.out.println("Triggered Local Game Load. Logging Enabled: " + enableLogs);

        // 3. Continue loading the game...
        director.showLocalGame();
    }
    /**
     * Handles multiplayer game start.
     */
    private void handleLoadMultiplayerGame() {
        boolean enableLogs = view.isLoggerEnabled();

        // LoggerConfig.setEnabled(enableLogs);

        System.out.println("Triggered Multiplayer Game Load. Logging Enabled: " + enableLogs);

        // Continue loading the game...
    }

    private void handleExit() {
        Platform.exit();
        System.exit(0);
    }
}