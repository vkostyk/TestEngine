package vk.testeng.servlet;

import vk.testeng.model.User;
import vk.testeng.service.UserManager;
import vk.testeng.servlet.service.ServletError;
import vk.testeng.servlet.service.ServletSuccess;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

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
                    logout(request, response);
                    break;

                default:
                    dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/view/404.jsp");
                    dispatcher.forward(request, response);
            }

        }
    }
    private void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        PrintWriter writer = response.getWriter();
        HttpSession session;
        session = request.getSession(false);
        if (session != null && session.getAttribute("currentSessionUser")!=null) {
            writer.write(ServletError.ALREADY_LOGGED.get());
            return;
        }
        if (username==null||password==null||username.length()<5||password.length()<5)
        {
            writer.write(ServletError.WRONG_CREDENTIALS.get());
            return;
        }

        User user = new User(username, password);
        UserManager userManager = new UserManager();
        UserManager.LoginResult result = userManager.login(user);
        user.setAccessType(User.AccessType.USER);
        switch (result.getState()) {
            case NOSUCHUSER:
                writer.write(ServletError.NO_SUCH_USER.get());
                break;
            case WRONGPASSWORD:
                writer.write(ServletError.WRONG_PASSWORD.get());
                break;
            case ISADMIN:
                user.setAccessType(User.AccessType.ADMIN);
            case ISUSER:
                    session = request.getSession(true);
                    session.setAttribute("currentSessionUser", user);
                    writer.write(ServletSuccess.LOGGED.get());

                break;

            }
    }
    private void register (HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String access = request.getParameter("access");
        PrintWriter writer = response.getWriter();
        User.AccessType accessType;
        if ((username==null||password==null||username.length()<5||password.length()<5))
        {
            writer.write(ServletError.WRONG_CREDENTIALS.get());
            writer.write(username);
            return;
        }
        if (access==null||!(access.equals("USER")||access.equals("ADMIN"))) {
            accessType = User.AccessType.USER;
        } else {
            accessType=User.AccessType.valueOf(access);
        }
        User user = new User(username, password, accessType);
        UserManager userManager = new UserManager();
        UserManager.RegResult result = userManager.register(user);
        switch (result.getState())
        {
            case USEREXISTS:
                writer.write(ServletError.USER_EXISTS.get());
                break;
            case SUCCESS:
                writer.write(ServletSuccess.REGISTERED.get());
                break;
        }

    }
    private void logout (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        PrintWriter writer = response.getWriter();
        HttpSession session = request.getSession(false);
        if (session!=null && session.getAttribute("currentSessionUser")!=null)
        {
            session.removeAttribute("currentSessionUser");
            session.invalidate();
            writer.write(ServletSuccess.LOGGED_OUT.get());
        } else {
            writer.write(ServletError.NOT_LOGGED.get());
        }
    }
}
