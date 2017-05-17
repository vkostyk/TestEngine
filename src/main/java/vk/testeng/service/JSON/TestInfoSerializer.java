package vk.testeng.service.JSON;

import com.google.gson.*;
import vk.testeng.model.Test;

import java.lang.reflect.Type;

public class TestInfoSerializer implements JsonSerializer<Test> {
    @Override
    public JsonElement serialize(Test test, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject result = new JsonObject();
        result.addProperty("id", test.getId());
        result.addProperty("name", test.getName());
        result.addProperty("description", test.getDescription());
        result.addProperty("maxPoints", test.getMaxPoints());
        return result;
    }
}
