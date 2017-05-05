package vk.testeng.servlet;

import vk.testeng.service.TestDB;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class TestDBServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        TestDB db = new TestDB();
        //Question q = db.getQuestion(0);

        response.setContentType("text/html");
        PrintWriter out=response.getWriter();


        String name=request.getParameter("name");
        String password=request.getParameter("password");

        out.print(name+password);
        out.close();
    }

}
