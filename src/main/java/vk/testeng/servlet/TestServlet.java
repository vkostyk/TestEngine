package vk.testeng.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import vk.testeng.model.FewOptionsAnswer;
import vk.testeng.model.OneOptionAnswer;
import vk.testeng.model.Question;
import vk.testeng.model.Test;
import vk.testeng.service.JSON.QuestionDeserializer;
import vk.testeng.service.JSON.QuestionSerializer;
import vk.testeng.service.JSON.TestDeserializer;
import vk.testeng.service.JSON.TestSerializer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class TestServlet extends HttpServlet {
    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(Question.class, new QuestionSerializer())
                    .registerTypeAdapter(Test.class, new TestSerializer())
                    .registerTypeAdapter(Test.class, new TestDeserializer())
                    .registerTypeAdapter(Question.class, new QuestionDeserializer())
                    .create();

            Test test = new Test();
            test.setId(0);
            test.setMaxPoints(10+15);
            ArrayList<String> o1 = new ArrayList<>();
            o1.add("truro");
            o1.add("true");
            o1.add("false");
            test.addQuestion(new Question(0, Question.AnswerType.ONEOPTION, 10, "which is true?", o1, new OneOptionAnswer(1)));
            ArrayList<String> o2 = new ArrayList<>();
            o2.add("fels");
            o2.add("false");
            o2.add("false");
            o2.add("falsee");
            test.addQuestion(new Question(1, Question.AnswerType.FEWOPTIONS, 15, "which is false?", o2, new FewOptionsAnswer(1, 2)));
            String json = gson.toJson(test);



        Test test2 = gson.fromJson(json, Test.class);
        json = gson.toJson(test2);

            response.getWriter().write(json);

    }
}
