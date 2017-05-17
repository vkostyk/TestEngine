package vk.testeng.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import vk.testeng.model.Question;
import vk.testeng.model.Test;
import vk.testeng.model.User;
import vk.testeng.service.JSON.*;
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
import java.net.URLDecoder;
import java.util.ArrayList;

public class TestManagerServlet  extends HttpServlet {
    private enum Action {ADD, EDIT, GET}




    private class IDContainer
    {
        int id;
        IDContainer()
        {

        }
    }
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
                case "getTestsInfo":
                    getTests(request, response);
                    break;
                case "initEdition":
                    initEdition(request, response);
                    break;
                case "addQuestion":
                    processQuestion(request, response, Action.ADD);
                    break;
                case "editQuestion":
                    processQuestion(request, response, Action.EDIT);
                    break;
                case "finishEdition":
                    finishEdition(request, response);
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
        if (user.getAccessType()!= User.AccessType.ADMIN)
        {
            writer.write(ServletError.USER_NOT_ADMIN.get());
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
    private void initEdition(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession(false);
        PrintWriter writer = response.getWriter();
        if (session==null || session.getAttribute("currentSessionUser")==null)
        {
            writer.write(ServletError.NOT_LOGGED.get());
            return;
        }
        User user = (User)session.getAttribute("currentSessionUser");
        if (user.getAccessType()!= User.AccessType.ADMIN)
        {
            writer.write(ServletError.USER_NOT_ADMIN.get());
            return;
        }
        String maxPointsS = request.getParameter("points");
        if (maxPointsS==null||!maxPointsS.matches("\\d+"))
        {
            writer.write(ServletError.NO_MAX_POINTS.get());
            return;
        }
        if (session.getAttribute("currentTestId")!=null)
        {
            writer.write(ServletError.EDIT_SESSION_ALIVE.get());
            return;
        }
        int maxPoints = Integer.parseInt(maxPointsS);
        Test test = new Test();
        test.setMaxPoints(maxPoints);
        TestManager testManager = new TestManager();
        int testId = testManager.addTest(test);
        session.setAttribute("currentTestId", testId);
        writer.write(ServletSuccess.ID.get(testId));
    }

    private void finishEdition(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession(false);
        PrintWriter writer = response.getWriter();
        if (session==null || session.getAttribute("currentSessionUser")==null)
        {
            writer.write(ServletError.NOT_LOGGED.get());
            return;
        }
        User user = (User)session.getAttribute("currentSessionUser");
        if (user.getAccessType()!= User.AccessType.ADMIN)
        {
            writer.write(ServletError.USER_NOT_ADMIN.get());
            return;
        }
        if (session.getAttribute("currentTestId")==null)
        {
            writer.write(ServletError.EDIT_SESSION_DEAD.get());
            return;
        }
        session.removeAttribute("currentTestId");
        writer.write(ServletSuccess.FINISH_EDIT.get());
    }

    private void processQuestion(HttpServletRequest request, HttpServletResponse response, Action action) throws ServletException, IOException
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
        if (user.getAccessType() != User.AccessType.ADMIN) {
            writer.write(ServletError.USER_NOT_ADMIN.get());
            return;
        }
        TestManager testManager = new TestManager();
        Question question;
        if (request.getParameter("JSON")==null)
        {
            writer.write(ServletError.JSON_NO_QUESTION.get());
            return;
        }

        String json = URLDecoder.decode(request.getParameter("JSON"), "UTF-8");
        Gson gson;
        switch (action) {
            case ADD:
            case EDIT:
                gson = new GsonBuilder()
                        .registerTypeAdapter(Question.class, new QuestionDeserializer())
                        .create();
                try {
                    question = gson.fromJson(json, Question.class);
                } catch (JsonSyntaxException e) {
                    JSONError error = JSONError.valueOf(e.getMessage());
                    switch (error)
                    {
                        case QUESTION:
                            writer.write(ServletError.JSON_FORMAT.get());
                            break;
                        case QUESTION_ID:
                            writer.write(ServletError.JSON_ID.get());
                            break;
                        case TYPE:
                            writer.write(ServletError.JSON_TYPE.get());
                            break;
                        case QUESTION_MAX_POINTS:
                            writer.write(ServletError.JSON_MAX_POINTS.get());
                            break;
                        case TASK:
                            writer.write(ServletError.JSON_TASK.get());
                            break;
                        case OPTIONS:
                            writer.write(ServletError.JSON_OPTIONS.get());
                            break;
                        case ANSWER:
                            writer.write(ServletError.JSON_ANSWER.get());
                            break;
                        default:
                            writer.write(ServletError.JSON_FORMAT.get());
                    }
                    return;
                }
                switch (action) {
                    case ADD:
                        int questionId = testManager.addQuestion(testId, question);
                        writer.write(ServletSuccess.ID.get(questionId));
                        return;
                    case EDIT:
                        testManager.editQuestion(testId, question);
                        writer.write(ServletSuccess.EDIT.get());
                        return;
                    default:
                        return;
                }
            case GET:
                gson = new Gson();
                IDContainer idContainer;
                try {
                    idContainer = gson.fromJson(json, IDContainer.class);
                } catch (JsonSyntaxException e) {
                    writer.write(ServletError.JSON_FORMAT.get());
                    return;
                }
                question = testManager.getQuestion(idContainer.id);
                gson = new GsonBuilder()
                        .registerTypeAdapter(Question.class, new QuestionSerializer())
                        .setPrettyPrinting()
                        .create();
                json = gson.toJson(question);
                writer.write(json);
                break;
            default:
                return;
        }

    }
}
