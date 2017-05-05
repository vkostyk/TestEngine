package vk.testeng.service;

import com.google.gson.*;
import vk.testeng.model.FewOptionsAnswer;
import vk.testeng.model.MatchingAnswer;
import vk.testeng.model.OneOptionAnswer;
import vk.testeng.model.Question;


import java.lang.reflect.Type;

public class QuestionSerializer implements JsonSerializer<Question> {
    @Override
    public JsonElement serialize(Question question, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        Question.AnswerType type = question.getType();
        //result.addProperty("id", question.getId());
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

        JsonArray answers;
        switch (type)
        {
            case ONEOPTION:
                result.addProperty("answer", ((OneOptionAnswer)(question.getCorrectAnswer())).getAnswer());
                break;
            case FEWOPTIONS:
                answers = new JsonArray();
                result.add("answer", answers);
                FewOptionsAnswer fewAnswersObj = (FewOptionsAnswer)question.getCorrectAnswer();
                for (Integer answer : fewAnswersObj.getAnswer())
                {
                    answers.add(new JsonPrimitive(answer));
                }
                break;
            case MATCHING:
                answers = new JsonArray();
                result.add("answer", answers);
                MatchingAnswer matchingAnswersObj = (MatchingAnswer)question.getCorrectAnswer();
                for (Integer answer : matchingAnswersObj.getAnswer())
                {
                    answers.add(new JsonPrimitive(answer));
                }
                break;
        }
        return result;
    }
}
