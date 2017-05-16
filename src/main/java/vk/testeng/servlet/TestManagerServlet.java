package vk.testeng.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import vk.testeng.model.Question;
import vk.testeng.model.Test;
import vk.testeng.model.User;
import vk.testeng.service.JSON.JSONError;
import vk.testeng.service.JSON.QuestionDeserializer;
import vk.testeng.service.TestManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class TestManagerServlet  extends HttpServlet {
    private enum Action {ADD, EDIT}
    private enum Error
    {

        NO_ACTION_SET("No action set"),
        WRONG_ACTION_PARAMETER("Wrong action parameter"),
        USER_NOT_ADMIN("Only admin can manage tests"),
        EDIT_SESSION_ALIVE("Finish your previous edition first"),
        EDIT_SESSION_DEAD("Start editing session prior to finishing editing session"),
        NO_MAX_POINTS("Max points must be provided at initiation of test addition"),
        NOT_LOGGED("Login first in order to be able to manage tests"),
        TEST_ID_NOT_SET("Test id must be set"),
        NO_QUESTION_JSON("Question JSON data not sent"),
        JSON_FORMAT("Wrong JSON format"),
        JSON_ID("Question id not set or wrong"),
        JSON_TYPE("Question type not set or wrong"),
        JSON_MAX_POINTS("Question max points not set or wrong"),
        JSON_TASK("Question task not set or wrong"),
        JSON_OPTIONS("Question options not set or wrong"),
        JSON_ANSWER("Question answer not set or wrong")
        ;
        private static final String LEFT = "{\"error\":\"";
        private static final String RIGHT = "\"}";

        private String error;
        Error(String error)
        {
            this.error = LEFT +error+ RIGHT;
        }
        public String get()
        {
            return error;
        }
    }
    private enum Success
    {
        ID(""), EDIT("Edit successful");
        private static final String LEFT = "{\"success\":\"";
        private static final String RIGHT = "\"}";

        private String success;
        Success(String success)
        {
            if (!this.name().equals("ID"))
            {
                this.success = LEFT +success+ RIGHT;
            }
        }
        //null for ID constant
        public String get()
        {
            return success;
        }
        //includes id for ID constant
        public String set(int id)
        {
            if (this.name().equals("ID"))
            {
                return LEFT + id + RIGHT;
            } else {
                return success;
            }
        }
    }
    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String action = request.getParameter("action");
        if (action==null||action.length()==0)
        {
            response.getWriter().write(Error.NO_ACTION_SET.get());
        } else {
            switch (action)
            {
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
                    response.getWriter().write(Error.WRONG_ACTION_PARAMETER.get());
            }
        }
    }
    private void initEdition(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession(false);
        if (session!=null && session.getAttribute("currentSessionUser")!=null)
        {
            User user = (User)session.getAttribute("currentSessionUser");
            if (user.getAccessType()!= User.AccessType.ADMIN)
            {
                response.getWriter().write(Error.USER_NOT_ADMIN.get());
            } else {
                String maxPointsS = request.getParameter("points");
                if (maxPointsS!=null&&maxPointsS.matches("\\d+")) {
                    if (session.getAttribute("currentTestId")!=null) {
                        int maxPoints = Integer.parseInt(maxPointsS);
                        Test test = new Test();
                        test.setMaxPoints(maxPoints);
                        TestManager testManager = new TestManager();
                        int testId = testManager.addTest(test);
                        session.setAttribute("currentTestId", testId);
                        response.getWriter().write("{\"testId\":"+"\""+testId+"\"}");
                    } else {
                        response.getWriter().write(Error.EDIT_SESSION_ALIVE.get());
                    }
                } else {
                    response.getWriter().write(Error.NO_MAX_POINTS.get());
                }
            }
        } else {
            response.getWriter().write(Error.NOT_LOGGED.get());
        }
    }

    private void finishEdition(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession(false);
        if (session!=null && session.getAttribute("currentSessionUser")!=null)
        {
            if (session.getAttribute("currentTestId")!=null) {
                session.removeAttribute("currentTestId");
                response.getWriter().write("{\"state\":\"success\"}");
            } else {
                response.getWriter().write(Error.EDIT_SESSION_DEAD.get());
            }
        } else {
            response.getWriter().write(Error.NOT_LOGGED.get());
        }
    }

    //on success returns question id if action==ADD, else returns 0; on fail returns -1
    private void processQuestion(HttpServletRequest request, HttpServletResponse response, Action action) throws ServletException, IOException
    {
        PrintWriter writer =  response.getWriter();
        HttpSession session = request.getSession(false);
        if (session==null || session.getAttribute("currentSessionUser")==null) {
            writer.write(Error.NOT_LOGGED.get());
            return;
        }
        User user = (User)session.getAttribute("currentSessionUser");
        if (session.getAttribute("currentTestId") == null) {
            writer.write(Error.TEST_ID_NOT_SET.get());
            return;
        }
        int testId = (Integer) session.getAttribute("currentTestId");
        if (user.getAccessType() != User.AccessType.ADMIN) {
            writer.write(Error.USER_NOT_ADMIN.get());
            return;
        }

        if (request.getParameter("questionJSON")==null)
        {
            writer.write(Error.NO_QUESTION_JSON.get());
            return;
        }
        String json = request.getParameter("questionJSON");

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Question.class, new QuestionDeserializer())
                .create();
        Question question;
        try {
            question = gson.fromJson(json, Question.class);
        } catch (JsonSyntaxException e) {
            JSONError error = JSONError.valueOf(e.getMessage());
            switch (error)
            {
                case QUESTION:
                    writer.write(Error.JSON_FORMAT.get());
                    break;
                case QUESTION_ID:
                    writer.write(Error.JSON_ID.get());
                    break;
                case TYPE:
                    writer.write(Error.JSON_TYPE.get());
                    break;
                case QUESTION_MAX_POINTS:
                    writer.write(Error.JSON_MAX_POINTS.get());
                    break;
                case TASK:
                    writer.write(Error.JSON_TASK.get());
                    break;
                case OPTIONS:
                    writer.write(Error.JSON_OPTIONS.get());
                    break;
                case ANSWER:
                    writer.write(Error.JSON_ANSWER.get());
                break;
                default:
                    writer.write(Error.JSON_FORMAT.get());
            }
            return;
        }
        TestManager testManager = new TestManager();
        switch (action) {
            case ADD:
                int questionId = testManager.addQuestion(testId, question);
                writer.write(Success.ID.set(questionId));
                return;
            case EDIT:
                testManager.editQuestion(testId, question);
                writer.write(Success.EDIT.get());
                return;
            default:
                return;
        }

    }
}
