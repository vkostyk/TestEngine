package vk.testeng.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import vk.testeng.model.Question;
import vk.testeng.model.Test;
import vk.testeng.model.User;
import vk.testeng.service.JSON.QuestionIdsSerializer;
import vk.testeng.service.JSON.QuestionOnlySerializer;
import vk.testeng.service.JSON.TestInfoArraySerializer;
import vk.testeng.service.JSON.TestInfoSerializer;
import vk.testeng.service.TestManager;
import vk.testeng.servlet.service.ServletError;
import vk.testeng.servlet.service.ServletSuccess;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class QuizServlet extends HttpServlet {
    private enum Action {SEND_ANSWER, GET_QUESTION}
    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String action = request.getParameter("action");
        if (action==null||action.length()==0)
        {
            response.getWriter().write(ServletError.NO_ACTION_SET.get());
        } else {
            switch (action)
            {
                case "init":
                    initQuiz(request, response);
                    break;
                case "getTestsInfo":
                    getTests(request, response);
                    break;
                case "sendAnswer":
                    processJSON(request, response, Action.SEND_ANSWER);
                    break;
                case "getQuestion":
                    processJSON(request, response, Action.GET_QUESTION);
                    break;
                case "finish":
                    finishQuiz(request, response);
                    break;
                default:
                    response.getWriter().write(ServletError.WRONG_ACTION_PARAMETER.get());
            }
        }
    }
    private void getTests(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession(false);
        PrintWriter writer = response.getWriter();
        if (session==null || session.getAttribute("currentSessionUser")==null)
        {
            writer.write(ServletError.NOT_LOGGED.get());
            return;
        }
        User user = (User)session.getAttribute("currentSessionUser");
        if (user.getAccessType()== User.AccessType.ADMIN)
        {
            writer.write(ServletError.USER_IS_ADMIN.get());
            return;
        }
        TestManager testManager = new TestManager();

        Type testListType = new TypeToken<ArrayList<Test>>(){}.getType();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Test.class,  new TestInfoSerializer())
                .registerTypeAdapter(testListType, new TestInfoArraySerializer())
                .setPrettyPrinting()
                .create();
        String json = gson.toJson(testManager.getTestsInfo());
        writer.write(json);
    }
    private void initQuiz(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession(false);
        PrintWriter writer = response.getWriter();
        if (session==null || session.getAttribute("currentSessionUser")==null)
        {
            writer.write(ServletError.NOT_LOGGED.get());
            return;
        }
        User user = (User)session.getAttribute("currentSessionUser");
        if (user.getAccessType()== User.AccessType.ADMIN)
        {
            writer.write(ServletError.USER_IS_ADMIN.get());
            return;
        }
        if (session.getAttribute("currentTestId")!=null)
        {
            writer.write(ServletError.QUIZ_SESSION_ALIVE.get());
            return;
        }
        if (request.getParameter("testId")==null||!request.getParameter("testId").matches("\\d+"))
        {
            writer.write(ServletError.TEST_ID_NOT_SET.get());
            return;
        }
        int testId = Integer.parseInt(request.getParameter("testId"));
        session.setAttribute("currentTestId", testId);
        TestManager testManager = new TestManager();


        Type idsListType = new TypeToken<ArrayList<Integer>>(){}.getType();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(idsListType,  new QuestionIdsSerializer())
                .setPrettyPrinting()
                .create();
        String json = gson.toJson(testManager.getQuestionIds(testId));
        writer.write(json);
    }
    private void finishQuiz(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession(false);
        PrintWriter writer = response.getWriter();
        if (session==null || session.getAttribute("currentSessionUser")==null)
        {
            writer.write(ServletError.NOT_LOGGED.get());
            return;
        }
        User user = (User)session.getAttribute("currentSessionUser");
        if (user.getAccessType()== User.AccessType.ADMIN)
        {
            writer.write(ServletError.USER_IS_ADMIN.get());
            return;
        }
        if (session.getAttribute("currentTestId")==null)
        {
            writer.write(ServletError.QUIZ_SESSION_DEAD.get());
            return;
        }
        session.removeAttribute("currentTestId");
        writer.write(ServletSuccess.QUIZ_FINISHED.get());
    }
    private void processJSON(HttpServletRequest request, HttpServletResponse response, Action action) throws ServletException, IOException
    {
        PrintWriter writer =  response.getWriter();
        HttpSession session = request.getSession(false);
        if (session==null || session.getAttribute("currentSessionUser")==null) {
            writer.write(ServletError.NOT_LOGGED.get());
            return;
        }
        User user = (User)session.getAttribute("currentSessionUser");
        if (session.getAttribute("currentTestId") == null) {
            writer.write(ServletError.TEST_ID_NOT_SET.get());
            return;
        }
        int testId = (Integer) session.getAttribute("currentTestId");
        if (user.getAccessType() == User.AccessType.ADMIN) {
            writer.write(ServletError.USER_IS_ADMIN.get());
            return;
        }
        if (request.getParameter("questionId")==null||!request.getParameter("questionId").matches("\\d+"))
        {
            writer.write(ServletError.QUESTION_ID_NOT_SET.get());
            return;
        }
        int questionId = Integer.parseInt(request.getParameter("questionId"));
        TestManager testManager = new TestManager();
        Gson gson;
        switch (action)
        {
            case GET_QUESTION:
                gson = new GsonBuilder()
                        .registerTypeAdapter(Question.class,  new QuestionOnlySerializer())
                        .setPrettyPrinting()
                        .create();
                String json = gson.toJson(testManager.getQuestion(questionId));
                writer.write(json);
                break;

            case SEND_ANSWER:

                break;
        }
    }
}
