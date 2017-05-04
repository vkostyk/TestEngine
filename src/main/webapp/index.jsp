<%@ page import="vk.testeng.Question" %><%--
  Created by IntelliJ IDEA.
  User: vkostyk
  Date: 04.05.2017
  Time: 10:54
  To change this template use File | Settings | File Templates.
--%>
<%
  Question question = new Question();
  question.setType(Question.AnswerType.MATCHING);
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>$Title$</title>
  </head>
  <body>
  <%=question.getType().name()%>
  $END$
  </body>
</html>
