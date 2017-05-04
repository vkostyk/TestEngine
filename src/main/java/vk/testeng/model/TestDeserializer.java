package vk.testeng.model;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by vkostyk on 21.04.2017.
 */
public class TestDeserializer implements JsonDeserializer<Test> {
    @Override
    public Test deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        Test test = new Test();
        JsonObject jsonObject = json.getAsJsonObject();
        for(Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            Question question = context.deserialize(entry.getValue(), Question.class);
            question.setId(Integer.parseInt(entry.getKey()));
            test.addQuestion(question);
        }
        return test;
    }
}
