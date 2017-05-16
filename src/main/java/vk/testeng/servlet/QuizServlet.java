package vk.testeng.servlet;

import vk.testeng.model.User;
import vk.testeng.servlet.service.ServletError;
import vk.testeng.servlet.service.ServletSuccess;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

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
        writer.write(ServletSuccess.ID.get(testId));
    }
    private void finishQuiz(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

    }
    private void processJSON(HttpServletRequest request, HttpServletResponse response, Action action) throws ServletException, IOException
    {

    }
}
