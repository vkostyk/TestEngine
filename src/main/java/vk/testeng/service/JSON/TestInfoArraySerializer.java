package vk.testeng.service.JSON;

import com.google.gson.*;
import vk.testeng.model.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TestInfoArraySerializer  implements JsonSerializer<ArrayList<Test>> {
    @Override
    public JsonElement serialize(ArrayList<Test> tests, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject result = new JsonObject();
        JsonArray testsArray = new JsonArray();
        result.add("tests", testsArray);
        for (Test test : tests)
        {
            testsArray.add(context.serialize(test));
        }
        return result;
    }
}
