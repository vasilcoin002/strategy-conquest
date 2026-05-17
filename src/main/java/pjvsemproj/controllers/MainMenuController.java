package pjvsemproj.controllers;

import javafx.application.Platform;
import pjvsemproj.views.MainMenuView;

/**
 * Controller for main menu UI.
 * <p>
 * Handles menu actions such as:
 * - starting game
 * - enabling logging
 * - exiting application
 */
public class MainMenuController {
    private final MainMenuView view;
    private final SceneDirector director;
    private String clientName;

    /**
     * Constructs a main menu controller instance and binds buttons to their respective actions.
     *
     * @param view       The JavaFX view presentation layer representing the main menu user interface.
     * @param director   The application's scene director responsible for routing and switching active windows.
     * @param clientName The default username string tracking configuration defaults passed into this controller.
     */
    public MainMenuController(MainMenuView view, SceneDirector director, String clientName) {
        this.view = view;
        this.director = director;
        this.clientName = clientName;

        bindActions();
    }

    /**
     * Binds UI buttons to actions.
     * Links functional action listeners onto the execution closures exposed from the main menu view component.
     */
    private void bindActions() {
        view.setOnLoadLocalGameAction(this::handleLoadLocalGame);
        view.setOnLoadMultiplayerGameAction(this::handleLoadMultiplayerGame);
        view.setOnExitAction(this::handleExit);
    }

    /**
     * Handles local game start.
     * <p>
     * Extracts and validates the custom username typed by the user. If valid, it reads the
     * logging configuration state and prompts the {@link SceneDirector} to hydrate and launch a single-player
     * match map against local AI bot structures.
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
     * <p>
     * Extracts and validates the custom username text field string. If non-empty, it updates
     * internal tracking credentials and shifts the graphical screen flow directly over to the multiplayer matchmaking lobby scene.
     */
    private void handleLoadMultiplayerGame() {
        String playerName = view.getPlayerName();

        if (playerName == null || playerName.trim().isEmpty()) {
            view.showError("Please enter a name first.");
            return;
        }

        this.clientName = playerName;

        director.showMultiplayerLobby(playerName);
    }

    /**
     * Shuts down internal platform runtimes and terminates the application execution lifecycle.
     */
    private void handleExit() {
        Platform.exit();
        System.exit(0);
    }
}