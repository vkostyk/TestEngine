package vk.testeng.servlet;

import vk.testeng.model.User;
import vk.testeng.service.UserManager;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        RequestDispatcher dispatcher;
        if (action==null||action.length()==0)
        {
            dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/view/404.jsp");
            dispatcher.forward(request, response);
        } else {

            switch (action)
            {
                case "login":
                    login(request, response);
                    break;
                case "register":
                    register(request, response);
                    break;

                case "logout":

                    break;

                default:
                    dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/view/404.jsp");
                    dispatcher.forward(request, response);
            }

        }
    }
    private void login(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        HttpSession session;
        if (username==null||password==null||username.length()<5||password.length()<5)
        {
            request.setAttribute("state", "wrongcredentials");
            request.getRequestDispatcher("./view/loginFailure.jsp").forward(request, response);
        } else {
            User user = new User(username, password);
            UserManager userManager = new UserManager();
            UserManager.LoginResult result = userManager.login(user);
            switch (result.getState())
            {
                case NOSUCHUSER:
                    request.setAttribute("state", "nouser");
                    request.getRequestDispatcher("./view/loginFailure.jsp").forward(request, response);
                    break;
                case WRONGPASSWORD:
                    request.setAttribute("state", "wrongpassword");
                    request.getRequestDispatcher("./view/loginFailure.jsp").forward(request, response);
                    break;
                case ISUSER:
                    user.setAccessType(User.AccessType.USER);
                    session = request.getSession(true);
                    session.setAttribute("currentSessionUser", user);

                    response.sendRedirect("./view/loginSuccess.jsp");
                    break;

                case ISADMIN:
                    user.setAccessType(User.AccessType.ADMIN);
                    session = request.getSession(true);
                    session.setAttribute("currentSessionUser", user);
                    response.sendRedirect("./view/loginSuccess.jsp");
                    break;
            }
        }
    }
    private void register (HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String access = request.getParameter("access");
        User.AccessType accessType = User.AccessType.valueOf(access);
        if ((username==null||password==null||username.length()<5||password.length()<5)||!(accessType==User.AccessType.ADMIN||accessType== User.AccessType.USER))
        {

            request.setAttribute("state", "wrongcredentials");
            request.getRequestDispatcher("./view/regFailure.jsp").forward(request, response);
        } else {
            User user = new User(username, password, User.AccessType.valueOf(access));
            UserManager userManager = new UserManager();
            UserManager.RegResult result = userManager.register(user);
            switch (result.getState())
            {
                case USEREXISTS:

                    break;
                case SUCCESS:

                    break;
            }
        }
    }
}
