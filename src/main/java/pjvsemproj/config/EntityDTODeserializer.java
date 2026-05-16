package pjvsemproj.config;

import com.google.gson.*;
import pjvsemproj.dto.CityDTO;
import pjvsemproj.dto.EntityDTO;
import pjvsemproj.dto.TroopUnitDTO;

import java.lang.reflect.Type;

/**
 * Custom Gson deserializer responsible for handling the polymorphic nature of game entities.
 * <p>
 * Because the save file stores a flat array of {@link EntityDTO} objects, Gson needs a strategy
 * to determine the specific subclass (e.g., City or Troop) to instantiate. This class intercepts
 * the JSON parsing, inspects the discriminator field ({@code entityType}), and delegates the
 * instantiation to the appropriate DTO subclass.
 */
public class EntityDTODeserializer implements JsonDeserializer<EntityDTO> {

    /**
     * Inspects the JSON payload to determine the concrete DTO type and deserializes it.
     *
     * @param json    The raw JSON element being parsed.
     * @param typeOfT The type of the Object to deserialize to.
     * @param context The current deserialization context provided by Gson.
     * @return A fully populated {@link CityDTO} or {@link TroopUnitDTO} cast as an {@link EntityDTO}.
     * @throws JsonParseException if the JSON structure is malformed or missing the required "entityType" field.
     */
    @Override
    public EntityDTO deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // Read the "entityType" field from the JSON to use as a subclass discriminator
        String type = jsonObject.get("entityType").getAsString();

        // Decide which specific DTO to build
        if ("City".equals(type)) {
            return context.deserialize(json, CityDTO.class);
        } else {
            // If it's not a city, it falls back to a military unit mapping (Militia, Cavalry, etc.)
            return context.deserialize(json, TroopUnitDTO.class);
        }
    }
}