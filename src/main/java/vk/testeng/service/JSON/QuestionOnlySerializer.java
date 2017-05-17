package vk.testeng.service.JSON;

import com.google.gson.*;
import vk.testeng.model.Question;

import java.lang.reflect.Type;

public class QuestionOnlySerializer implements JsonSerializer<Question> {
    @Override
    public JsonElement serialize(Question question, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject result = new JsonObject();
        result.addProperty("id", Integer.toString(question.getId()));
        Question.AnswerType type = question.getType();
        result.addProperty("type", type.name());
        result.addProperty("maxPoints", question.getMaxPoints());
        result.addProperty("task", question.getTask());

        if ((type!=Question.AnswerType.INPUT)&&(type!=Question.AnswerType.ESSAY))
        {
            JsonArray options = new JsonArray();
            result.add("options", options);
            for (String option : question.getOptions())
            {
                options.add(new JsonPrimitive(option));
            }
        }
        return result;
    }
}
