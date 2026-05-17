package pjvsemproj.views;

/**
 * Global configuration data registry holding constant metric variables used across the UI presentation views.
 * <p>
 * This class serves as a centralized, non-instantiable utility container to ensure visual layout consistency.
 * Any changes to sizes, dimension scaling parameters, or panel metrics across the game canvas window should be configured here.
 */
public final class ViewConstants {

    /**
     * Private constructor to explicitly prevent instantiation of this utility constant container.
     * <p>
     * Throws an {@link AssertionError} if instantiation is accidentally triggered via internal reflection loops.
     */
    private ViewConstants() {
        throw new AssertionError("Utility class 'ViewConstants' cannot be instantiated.");
    }

    /**
     * The dimensions of a single square map grid cell layout block, measured in screen pixels.
     * <p>
     * Utilized uniformly by renderers, input coordinators, and path highlighting overlays to map
     * mathematical simulation coordinates cleanly into JavaFX display viewport spaces.
     * <p>
     * <b>Value:</b> {@code 64} pixels
     */
    public static final int TILE_SIZE = 64;

    /**
     * The fixed horizontal layout width allocation reserved for the interactive side panel view window container.
     * <p>
     * Controls the bounding size of the player statistics description layout and unit purchase menus,
     * preventing elements from squishing or altering dimensions when canvas grid counts change.
     * <p>
     * <b>Value:</b> {@code 250} pixels
     */
    public static final int GAME_SIDE_PANEL_WIDTH = 250;
}