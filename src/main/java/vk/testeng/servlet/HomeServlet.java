package vk.testeng.servlet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String page = request.getParameter("page");
        if (page==null) {page="home";}
        RequestDispatcher dispatcher;
        HttpSession session=request.getSession(false);
        switch (page)
        {
            case "home":
                dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/view/home.jsp");
                break;
            case "testManager":
                if(session!=null) {
                    dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/view/testManager.jsp");
                } else {dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/view/404.jsp");}
                break;
            case "login":
                dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/view/login.jsp");
                break;
            case "logout":
                request.setAttribute("action", "logout");
                dispatcher = this.getServletContext().getRequestDispatcher("/login");
                break;
            default:
                dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/view/404.jsp");
        }


        dispatcher.forward(request, response);

    }

}



