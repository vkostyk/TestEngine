package vk.testeng.service.JSON;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class QuestionIdsSerializer implements JsonSerializer<ArrayList<Integer>> {

@Override
public JsonElement serialize(ArrayList<Integer> questionIds, Type typeOfSrc, JsonSerializationContext context)
        {
            JsonObject result = new JsonObject();
            JsonArray idsArray = new JsonArray();
            result.add("questionIds", idsArray);
            for (int questionId : questionIds)
            {
                idsArray.add(questionId);
            }
            return result;
        }

}
