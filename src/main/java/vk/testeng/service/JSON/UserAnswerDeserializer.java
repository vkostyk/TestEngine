package vk.testeng.service.JSON;

import com.google.gson.*;
import vk.testeng.model.Question;
import vk.testeng.model.answer.*;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class UserAnswerDeserializer implements JsonDeserializer<AbstractAnswer> {
    private Question.AnswerType type;
    public UserAnswerDeserializer(Question.AnswerType type)
    {
        this.type = type;
    }
    @Override
    public AbstractAnswer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject jsonObject;
        AbstractAnswer abstractAnswer;
        ArrayList<Integer> answers;
        JsonArray JSONAnswers;
        try {
            jsonObject = json.getAsJsonObject();
        } catch (IllegalStateException e) {
            throw new JsonParseException(JSONError.QUESTION.name());
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
                    case MATCHING:
                        abstractAnswer = new MatchingAnswer(answers);
                        break;
                    default:
                        throw new JsonParseException(JSONError.TYPE.name());
                }
                break;
            case INPUT:
            case ESSAY:
                if (jsonObject.get("answer")==null)
                {
                    throw new JsonParseException(JSONError.ANSWER.name());
                }
                try {
                    switch (type)
                    {
                        case INPUT:
                            abstractAnswer = new InputAnswer(jsonObject.get("answer").getAsString());
                            break;
                        case ESSAY:
                            abstractAnswer = new EssayAnswer(jsonObject.get("answer").getAsString());
                            break;
                        default:
                            throw new JsonParseException(JSONError.TYPE.name());
                    }

                } catch(ClassCastException|IllegalStateException e) {
                    throw new JsonParseException(JSONError.ANSWER.name());
                }
                break;
            default:
                throw new JsonParseException(JSONError.TYPE.name());
        }
        return abstractAnswer;
    }
}
