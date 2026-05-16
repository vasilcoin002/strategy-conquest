package pjvsemproj.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import pjvsemproj.dto.EntityDTO;
import pjvsemproj.dto.GameDTO;

import java.io.*;

/**
 * Manages the I/O boundary for game configuration and save files.
 * <p>
 * This utility utilizes Gson to translate between human-readable JSON files and the
 * engine's raw Data Transfer Objects (DTOs). It registers custom type adapters
 * to handle polymorphic entity resolution during the parsing phase.
 */
public class GameConfigParser {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(EntityDTO.class, new EntityDTODeserializer())
            .setPrettyPrinting() // Makes the saved JSON file readable for humans
            .create();

    /**
     * Reads a JSON configuration file from disk and translates it into a raw {@link GameDTO}.
     *
     * @param filePath The absolute or relative path to the configuration JSON file.
     * @return The deserialized game data representation.
     * @throws RuntimeException if the file cannot be found, opened, or parsed successfully.
     */
    public GameDTO parseLevelConfig(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, GameDTO.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config file at: " + filePath, e);
        }
    }

    /**
     * Serializes a game state transfer object and writes it to disk as a JSON file.
     *
     * @param gameDTO  The data transfer object representing the current game state.
     * @param filePath The destination path where the save file should be written.
     * @throws RuntimeException if the file cannot be created or written to.
     */
    public void saveLevelConfig(GameDTO gameDTO, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(gameDTO, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save config file to: " + filePath, e);
        }
    }
}