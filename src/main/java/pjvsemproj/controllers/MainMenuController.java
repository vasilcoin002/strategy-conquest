package pjvsemproj.controllers;

import javafx.application.Platform;
import pjvsemproj.views.MainMenuView;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogManager;

/**
 * Controller for main menu UI.
 * <p>
 * Handles menu actions such as:
 * - starting game
 * - enabling logging
 * - exiting application
 */
public class MainMenuController {
    private static final Logger LOGGER = Logger.getLogger(MainMenuController.class.getName());
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
        configureGlobalLogging(enableLogs);
        LOGGER.info("Triggered Local Game Load. Logging Enabled: " + enableLogs);

        try {
            director.showLocalGame(clientName);
        } catch (Exception e) {
            view.showError("Error loading game: " + e.getMessage());
            LOGGER.log(Level.SEVERE, "Game load failed: {0}", e.getMessage());
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

        boolean enableLogs = view.isLoggerEnabled();
        configureGlobalLogging(enableLogs);
        LOGGER.info("Triggered Multiplayer Lobby Load. Logging Enabled: " + enableLogs);

        director.showMultiplayerLobby(playerName);
    }

    /**
     * Shuts down internal platform runtimes and terminates the application execution lifecycle.
     */
    private void handleExit() {
        Platform.exit();
        System.exit(0);
    }

    /**
     * Actively configures the Java util logging framework based on UI selections.
     * Mutes all handlers if logging is disabled.
     */
    private void configureGlobalLogging(boolean enableLogs) {
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        if (enableLogs) {
            rootLogger.setLevel(Level.ALL);
        } else {
            rootLogger.setLevel(Level.OFF);
        }
    }
}