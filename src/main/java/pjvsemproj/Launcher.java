package pjvsemproj;

import javafx.application.Application;

/**
 * Standard thick-client proxy execution entry point.
 * <p>
 * Serves as a fat-JAR entry point to decouple explicit class references from internal runtime
 * module-path dependencies. By separating the initial static main method from a class
 * extending {@link Application}, it circumvents graphics toolkit initialization checks on legacy setups.
 */
public class Launcher {

    /**
     * Static system process entry point executing standard Java application boots.
     * <p>
     * Explicitly routes process parameters directly into the JavaFX engine configuration pipeline to launch
     * the underlying application lifecycle loops inside {@link HelloApplication} safely.
     *
     * @param args Array of optional system runtime execution command-line parameter arguments.
     */
    public static void main(String[] args) {
        Application.launch(HelloApplication.class, args);
    }
}