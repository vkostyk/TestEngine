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
                alert("login");
            }
            function register()
            {
                alert("register");
            }
            function doRegister()
            {
                document.getElementById("registerButton").setAttribute("type", "password");
                document.getElementById("registerButton").setAttribute("value", "");
                document.getElementById("loginButton").setAttribute("value", "Retype password and submit");
                document.getElementById("loginButton").setAttribute("onclick", "register()");
            }

        </script>
    </head>
    <body>
    <div class="flex-container">
        <input type="text" value="username" id="username" class="element">
        <input type="password" value="password" id="password" class="element">
        <input type="button" value="Login" onclick="doLogin()" id="loginButton" class="element">
        <input type="button" value="Register new" onclick="doRegister()" id="registerButton" class="element">

    </div>
    </body>
</html>
