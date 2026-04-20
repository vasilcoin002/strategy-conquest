package pjvsemproj.config;

import java.io.*;

/**
 * Parser which reads configuration files for games
 * and returns data for game setup manager to set up game
 */
public class GameConfigParser {
    // TODO add methods which parse config file
    /**
     * Reads a level file and translates it into raw configuration data.
     */
    public GameConfigDTO parseLevelConfig(String filePath) {
        try (
                BufferedReader br = new BufferedReader(new FileReader(filePath));
//                PrintWriter pw = new PrintWriter("output.txt");
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                // TODO finish method
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
