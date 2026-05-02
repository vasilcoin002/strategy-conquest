package pjvsemproj.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import pjvsemproj.dto.EntityDTO;
import pjvsemproj.dto.GameDTO;

import java.io.*;

/**
 * Parser which reads configuration files for games
 * and returns data for game setup manager to set up game
 */
public class GameConfigParser {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(EntityDTO.class, new EntityDTODeserializer())
            .setPrettyPrinting() // Makes the saved JSON file readable for humans
            .create();
    /**
     * Reads a level file and translates it into raw configuration data.
     */
    public GameDTO parseLevelConfig(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, GameDTO.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config file", e);
        }
    }

    /**
     * Reads game state and writes it into file
     */
    public void saveLevelConfig(GameDTO gameDTO, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(gameDTO, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save config file", e);
        }
    }
}
