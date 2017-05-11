<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Login</title>
        <style>
            .flex-container{
                display: flex;
                justify-content: space-between;
                width: 400px;
                flex-flow: row wrap;
            }
            .element {
                width: 200px;
            }
        </style>
        <script>
            function doLogin()
            {
               // document.getElementById()
            }
            function register()
            {
                alert("register");
            }
            function doRegister()
            {
                document.getElementById("action").setAttribute("value", "register");

                document.getElementById("registerButton").setAttribute("type", "password");
                document.getElementById("registerButton").setAttribute("value", "");

                document.getElementById("loginButton").setAttribute("value", "Retype password and submit");
            }

        </script>
    </head>
    <body><form action="login" method="post" id="main">
    <div class="flex-container">

        <input type="text" value="username" id="username" name="username" class="element">
        <input type="password" value="password" id="password" name="password" class="element">

        <input type="submit" value="Login" id="loginButton" class="element">
        <input type="button" value="Register new" onclick="doRegister()" id="registerButton" class="element">

        <input type="hidden" id="action" name="action" value="login">

    </div></form>
    </body>
</html>
