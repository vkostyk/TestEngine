package vk.testeng.service.JSON;

import com.google.gson.*;
import vk.testeng.model.Question;
import vk.testeng.model.Test;

import java.lang.reflect.Type;


public class TestSerializer implements JsonSerializer<Test> {
    @Override
    public JsonElement serialize(Test test, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject result = new JsonObject();
        result.addProperty("id", Integer.toString(test.getId()));
        result.addProperty("maxPoints", Integer.toString(test.getMaxPoints()));
        JsonArray questions = new JsonArray();
        result.add("questions", questions);
        for (Question q : test.getQuestions())
        {
            questions.add(context.serialize(q));
        }
        return result;
    }
}
