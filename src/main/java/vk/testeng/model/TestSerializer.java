package vk.testeng.model;

import com.google.gson.*;

import java.lang.reflect.Type;


public class TestSerializer implements JsonSerializer<Test> {
    @Override
    public JsonElement serialize(Test test, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject result = new JsonObject();
        for (Question q : test.getQuestions())
        {
            result.add(Integer.toString(q.getId()), context.serialize(q));
        }
        return result;
    }
}
