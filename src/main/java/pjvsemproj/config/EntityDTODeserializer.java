package pjvsemproj.config;

import com.google.gson.*;
import pjvsemproj.dto.CityDTO;
import pjvsemproj.dto.EntityDTO;
import pjvsemproj.dto.TroopUnitDTO;

import java.lang.reflect.Type;

public class EntityDTODeserializer implements JsonDeserializer<EntityDTO> {

    @Override
    public EntityDTO deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // Read the "entityType" field from the JSON
        String type = jsonObject.get("entityType").getAsString();

        // Decide which specific DTO to build
        if ("City".equals(type)) {
            return context.deserialize(json, CityDTO.class);
        } else {
            // If it's not a city, it must be a troop (Militia, Cavalry, etc.)
            return context.deserialize(json, TroopUnitDTO.class);
        }
    }
}