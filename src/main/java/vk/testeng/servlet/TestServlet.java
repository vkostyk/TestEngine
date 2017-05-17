package vk.testeng.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import vk.testeng.model.Test;
import vk.testeng.service.JSON.TestInfoArraySerializer;
import vk.testeng.service.JSON.TestInfoSerializer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class TestServlet extends HttpServlet {
    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {


        ArrayList<Test> tests = new ArrayList<>();
        tests.add(new Test(0, 100, "test1", "some testst"));
        tests.add(new Test(1, 200, "test2", "some another test"));
        Type testListType = new TypeToken<ArrayList<Test>>(){}.getType();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Test.class,  new TestInfoSerializer())
                .registerTypeAdapter(testListType, new TestInfoArraySerializer())
                .setPrettyPrinting()
                .create();
        String json = gson.toJson(tests);
        response.getWriter().write(json);
/*
            Test test = new Test();
            test.setId(0);
            test.setMaxPoints(10+15);
            ArrayList<String> o1 = new ArrayList<>();
            o1.add("truro");
            o1.add("true");
            o1.add("false");
            test.addQuestion(new Question(0, Question.AnswerType.ONE_OPTION, 10, "which is true?", o1, new OneOptionAnswer(1)));
            ArrayList<String> o2 = new ArrayList<>();
            o2.add("fels");
            o2.add("false");
            o2.add("false");
            o2.add("falsee");
            test.addQuestion(new Question(1, Question.AnswerType.FEW_OPTIONS, 15, "which is false?", o2, new FewOptionsAnswer(1, 2)));
            String json = gson.toJson(test);



        Test test2 = gson.fromJson(json, Test.class);
        json = gson.toJson(test2);

            response.getWriter().write(json);*/

    }
}
