package vk.testeng;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class QuestionDeserializer implements JsonDeserializer<Question> {
    @Override
    public Question deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject jsonObject = json.getAsJsonObject();
        Question question = new Question();
        Question.AnswerType type = Question.AnswerType.valueOf(jsonObject.get("type").getAsString());
        question.setType(type);
        question.setMaxPoints(jsonObject.get("maxPoints").getAsInt());
        question.setTask(jsonObject.get("task").getAsString());
        if ((type!= Question.AnswerType.INPUT)&&(type!=Question.AnswerType.ESSAY))
        {
            ArrayList<String> optionsObj = new ArrayList<>();
            JsonArray options = jsonObject.getAsJsonArray("options");
            for (JsonElement option : options)
            {
                optionsObj.add(option.getAsString());
            }
            question.setOptions(optionsObj);
        }
        ArrayList<Integer> answersInt;
        JsonArray answers;
        switch (type)
        {
            case ONEOPTION:
                question.setCorrectAnswer(new OneOptionAnswer(jsonObject.get("answer").getAsInt()));
                break;
            case FEWOPTIONS:
                answersInt = new ArrayList<Integer>();
                answers = jsonObject.getAsJsonArray("answer");
                for (JsonElement answer : answers)
                {
                    answersInt.add(answer.getAsInt());
                }
                question.setCorrectAnswer(new FewOptionsAnswer(answersInt));
                break;
            case MATCHING:
                answersInt = new ArrayList<Integer>();
                answers = jsonObject.getAsJsonArray("answer");
                for (JsonElement answer : answers)
                {
                    answersInt.add(answer.getAsInt());
                }
                question.setCorrectAnswer(new FewOptionsAnswer(answersInt));
                break;
        }
        return question;
    }
}
