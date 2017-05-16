package vk.testeng.service.JSON;

import com.google.gson.*;
import vk.testeng.model.answer.FewOptionsAnswer;
import vk.testeng.model.answer.MatchingAnswer;
import vk.testeng.model.answer.OneOptionAnswer;
import vk.testeng.model.Question;


import java.lang.reflect.Type;

public class QuestionSerializer implements JsonSerializer<Question> {
    @Override
    public JsonElement serialize(Question question, Type typeOfSrc, JsonSerializationContext context) {
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

        JsonArray answers;
        switch (type)
        {
            case ONE_OPTION:
                result.addProperty("answer", ((OneOptionAnswer)(question.getCorrectAnswer())).getAnswer());
                break;
            case FEW_OPTIONS:
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
