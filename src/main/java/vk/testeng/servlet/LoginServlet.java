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

               /*case "logout":
                    logout(request, response);
                    break;
                */
                default:
                    dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/view/404.jsp");
                    dispatcher.forward(request, response);
            }

        }
    }
    protected void doGet (HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        String action = (String)request.getAttribute("action");
        if (action=="logout") {logout(request, response);}
    }
    private void login(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        HttpSession session;
        if (username==null||password==null||username.length()<5||password.length()<5)
        {
            request.setAttribute("state", "Wrong credentials, username and password should have at least 5 characters");
            request.getRequestDispatcher("/WEB-INF/view/accountAction.jsp").forward(request, response);
        } else {
            User user = new User(username, password);
            UserManager userManager = new UserManager();
            UserManager.LoginResult result = userManager.login(user);
            user.setAccessType(User.AccessType.USER);
            switch (result.getState()) {
                case NOSUCHUSER:
                    request.setAttribute("state", "No such user");
                    request.getRequestDispatcher("/WEB-INF/view/accountAction.jsp").forward(request, response);
                    break;
                case WRONGPASSWORD:
                    request.setAttribute("state", "Wrong password");
                    request.getRequestDispatcher("/WEB-INF/view/accountAction.jsp").forward(request, response);
                    break;
                case ISADMIN:
                    user.setAccessType(User.AccessType.ADMIN);
                case ISUSER:
                    session = request.getSession(false);
                    if (session != null && session.getAttribute("currentSessionUser")!=null) {
                        request.setAttribute("state", "Logout first");
                        request.getRequestDispatcher("/WEB-INF/view/accountAction.jsp").forward(request, response);
                    } else {
                        session = request.getSession(true);
                        session.setAttribute("currentSessionUser", user);

                        request.setAttribute("state", "Successfully logged in");
                        request.getRequestDispatcher("/WEB-INF/view/accountAction.jsp").forward(request, response);
                    }
                    break;

            }
        }
    }
    private void register (HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String access = request.getParameter("access");
        User.AccessType accessType;
        if ((username==null||password==null||username.length()<5||password.length()<5)) //||!(accessType==User.AccessType.ADMIN||accessType== User.AccessType.USER)
        {
            request.setAttribute("state", "Wrong credentials, username and password should have at least 5 characters");
            request.getRequestDispatcher("/WEB-INF/view/accountAction.jsp").forward(request, response);
        } else {
            if (access==null) {
                accessType = User.AccessType.USER;
            } else {
                if (!(access=="USER"||access== "ADMIN"))
                {
                    accessType = User.AccessType.USER;
                } else {
                    accessType=User.AccessType.valueOf(access);
                }
            }
            User user = new User(username, password, accessType);
            UserManager userManager = new UserManager();
            UserManager.RegResult result = userManager.register(user);
            switch (result.getState())
            {
                case USEREXISTS:
                    request.setAttribute("state", "Such user already exists");
                    request.getRequestDispatcher("/WEB-INF/view/accountAction.jsp").forward(request, response);
                    break;
                case SUCCESS:
                    request.setAttribute("state", "Successfully registered");
                    request.getRequestDispatcher("/WEB-INF/view/accountAction.jsp").forward(request, response);
                    break;
            }
        }
    }
    private void logout (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession(false);
        if (session!=null && session.getAttribute("currentSessionUser")!=null)
        {

            session.removeAttribute("currentSessionUser");
            session.invalidate();
            request.setAttribute("state", "Successfully logged out");
            request.getRequestDispatcher("/WEB-INF/view/accountAction.jsp").forward(request, response);
        } else {
            request.setAttribute("state", "Login first in order to be able to logout");
            request.getRequestDispatcher("/WEB-INF/view/accountAction.jsp").forward(request, response);
        }
    }
}
