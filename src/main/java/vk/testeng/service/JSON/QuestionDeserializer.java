package vk.testeng.service.JSON;
import com.google.gson.*;
import vk.testeng.model.*;
import vk.testeng.model.answer.AbstractAnswer;
import vk.testeng.model.answer.FewOptionsAnswer;
import vk.testeng.model.answer.MatchingAnswer;
import vk.testeng.model.answer.OneOptionAnswer;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class QuestionDeserializer implements JsonDeserializer<Question> {

    @Override
    public Question deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject jsonObject;
        int id;
        Question.AnswerType type;
        int maxPoints;
        String task;
        ArrayList<String> optionsObj = new ArrayList<>();
        ArrayList<Integer> answers;
        AbstractAnswer abstractAnswer;
        JsonArray JSONAnswers;
        try {
            jsonObject = json.getAsJsonObject();
        } catch (IllegalStateException e) {
            throw new JsonParseException(JSONError.QUESTION.name());
        }
        if (jsonObject.get("id")==null)
        {
            throw new JsonParseException(JSONError.QUESTION_ID.name());
        }
        try {
            id = jsonObject.get("id").getAsInt();
        } catch (ClassCastException|IllegalStateException e)
        {
            throw new JsonParseException(JSONError.QUESTION_ID.name());
        }
        if (jsonObject.get("type")==null)
        {
            throw new JsonParseException(JSONError.TYPE.name());
        }
        try {
            type = Question.AnswerType.valueOf(jsonObject.get("type").getAsString());
        } catch (ClassCastException|IllegalStateException e) {
            throw new JsonParseException(JSONError.TYPE.name());
        }
        if (jsonObject.get("maxPoints")==null)
        {
            throw new JsonParseException(JSONError.QUESTION_MAX_POINTS.name());
        }
        try {
            maxPoints = jsonObject.get("maxPoints").getAsInt();
        } catch (ClassCastException|IllegalStateException e)
        {
            throw new JsonParseException(JSONError.QUESTION_MAX_POINTS.name());
        }
        if (jsonObject.get("task")==null)
        {
            throw new JsonParseException(JSONError.TASK.name());
        }
        try {
            task = jsonObject.get("task").getAsString();
        } catch (ClassCastException|IllegalStateException e) {
            throw new JsonParseException(JSONError.TASK.name());
        }
        if ((type!= Question.AnswerType.INPUT)&&(type!=Question.AnswerType.ESSAY))
        {
            JsonArray options;
            try {
                options = jsonObject.getAsJsonArray("options");
            } catch (IllegalStateException e)
            {
                throw new JsonParseException(JSONError.OPTIONS.name());
            }
            for (JsonElement option : options) {
                try {
                    optionsObj.add(option.getAsString());
                } catch (ClassCastException|IllegalStateException e)
                {
                    throw new JsonParseException(JSONError.OPTIONS.name());
                }
            }
        }
        switch (type)
        {
            case ONE_OPTION:
                if (jsonObject.get("answer")==null)
                {
                    throw new JsonParseException(JSONError.ANSWER.name());
                }
                try {
                    abstractAnswer = new OneOptionAnswer(jsonObject.get("answer").getAsInt());
                } catch(ClassCastException|IllegalStateException e) {
                    throw new JsonParseException(JSONError.ANSWER.name());
                }
                break;
            case FEW_OPTIONS:
            case MATCHING:
                answers = new ArrayList<Integer>();
                try {
                    JSONAnswers = jsonObject.getAsJsonArray("answer");
                    for (JsonElement answer : JSONAnswers)
                    {
                        try {
                            answers.add(answer.getAsInt());
                        } catch (ClassCastException|IllegalStateException e) {
                            throw new JsonParseException(JSONError.ANSWER.name());
                        }
                    }
                } catch (IllegalStateException e) {
                    throw new JsonParseException(JSONError.ANSWER.name());
                }
                switch (type)
                {
                    case FEW_OPTIONS:
                        abstractAnswer = new FewOptionsAnswer(answers);
                        break;
                    case ONE_OPTION:
                        abstractAnswer = new MatchingAnswer(answers);
                        break;
                    default:
                        throw new JsonParseException(JSONError.TYPE.name());
                }
                break;
            default:
                throw new JsonParseException(JSONError.TYPE.name());
        }
        try {
        return new Question(id, type, maxPoints, task, optionsObj, abstractAnswer);
        } catch  (RuntimeException e) {
            throw new JsonParseException(JSONError.QUESTION.name());
        }
    }
}
