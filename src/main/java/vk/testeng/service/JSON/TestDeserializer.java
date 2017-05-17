package vk.testeng.service.JSON;

import com.google.gson.*;
import vk.testeng.model.Question;
import vk.testeng.model.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TestDeserializer implements JsonDeserializer<Test> {
    @Override
    public Test deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject jsonObject;
        int maxPoints;
        int id;
        String name;
        String description;
        JsonArray JSONQuestions;
        ArrayList<Question> questions = new ArrayList<>();

        try {
            jsonObject = json.getAsJsonObject();
        } catch (IllegalStateException e)
        {
            throw new JsonParseException(JSONError.TEST.name());
        }
        if (jsonObject.get("id")==null)
        {
            throw new JsonParseException(JSONError.TEST_ID.name());
        }
        try {
            id = jsonObject.get("id").getAsInt();
        } catch (ClassCastException|IllegalStateException e) {
            throw new JsonParseException(JSONError.TEST_ID.name());
        }

        if (jsonObject.get("name")==null)
        {
            throw new JsonParseException(JSONError.TEST_NAME.name());
        }
        try {
            name = jsonObject.get("name").getAsString();
        } catch (ClassCastException|IllegalStateException e) {
            throw new JsonParseException(JSONError.TEST_NAME.name());
        }

        if (jsonObject.get("description")==null)
        {
            throw new JsonParseException(JSONError.TEST_DESCRIPTION.name());
        }
        try {
            description = jsonObject.get("description").getAsString();
        } catch (ClassCastException|IllegalStateException e) {
            throw new JsonParseException(JSONError.TEST_DESCRIPTION.name());
        }

        if (jsonObject.get("maxPoints")==null)
        {
            throw new JsonParseException(JSONError.TEST_MAX_POINTS.name());
        }
        try {
            maxPoints = jsonObject.get("maxPoints").getAsInt();
        } catch (ClassCastException|IllegalStateException e) {
            throw new JsonParseException(JSONError.TEST_MAX_POINTS.name());
        }
        try {
            JSONQuestions = jsonObject.getAsJsonArray("questions");
        } catch (IllegalStateException e) {
            throw new JsonParseException(JSONError.QUESTION.name());
        }
        for (JsonElement question : JSONQuestions)
        {
                questions.add(context.deserialize(question, Question.class));
        }

        return new Test(id, maxPoints, name, description, questions);
    }
}
