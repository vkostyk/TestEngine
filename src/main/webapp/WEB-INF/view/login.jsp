<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <script src="jquery.js"></script>
        <title>Login</title>
        <style>
            .flex-container{
                padding: 70px 0;
                margin:auto;
                display: flex;
                justify-content: space-between;
                width: 400px;
                flex-flow: row wrap;
            }
            .element {
                width: 200px;
            }
            .big {
                width: 400px;
            }
        </style>
        <script>
            function displayIfLogged()
            {
                $.post("login", {
                            action: "login",
                        },
                        function( data ) {
                            var obj = jQuery.parseJSON( data );
                            if (obj.error==="You are already logged in")
                            {
                                $("#logout").show();
                            } else {
                                $("#main").css("display", "flex");
                            }
                        }).fail(function() {
                    alert( "Error" );
                });
            }
            function comment(data)
            {
                var obj = jQuery.parseJSON(data);
                if (data===null)
                {
                    $("#comment").text("null");
                }
                if (obj.error!==null){
                    $("#comment").text(obj.error);}
                if (obj.success!==null) {
                    $("#comment").text(obj.success);
                }
            }
            function doLogin()
            {
                var username =  $("#username").val();
                var password = $("#password").val();
                $.post("login", {
                            action: "login",
                            username: username,
                            password: password
                        },
                        function( data ) {
                            var obj = jQuery.parseJSON(data);
                            comment(obj);
                        }).fail(function() {
                    alert( "Error" );
                });

            }
            function doRegister()
            {

                var username =  $("#username").val();
                var password = $("#password").val();
                var passRetype = $("#registerButton").val();;
                if (password===passRetype) {
                $.post("login", {
                    action: "register",
                    username: username,
                    password: password,
                    access: "USER"
                },
                        function( data ) {
                            var obj = jQuery.parseJSON(data);
                            comment(obj);
                        }).fail(function() {
                    alert( "Error" );
                });
                } else {
                    alert("Passwords do not match");
                }
            }
            function changeToRegister()
            {

                $("#registerButton")
                        .unbind("click", changeToRegister)
                        .attr("type", "password")
                        .attr("value", "");
                $("#loginButton")
                        .unbind("click", doLogin)
                        .bind("click", doRegister)
                    .attr("value","Retype password and submit");
            }
            function logout()
            {
                $.post("login", {
                            action: "logout",
                        },
                        function( data ) {

                            comment(data);
                        }).fail(function() {
                    alert( "Error" );
                });
            }
        </script>
    </head>
    <body>
    <a href="javascript:logout()" id="logout">logout</a>
<form>
    <div class="flex-container" id="main">

        <input type="text" value="username" id="username" name="username" class="element">
        <input type="password" value="" id="password" name="password" class="element">

        <input type="button" value="Login" id="loginButton" class="element">
        <input type="button" value="Register new" id="registerButton" class="element">
        <div id="comment" class="big"></div>

    </div>
</form>
    <script>
    $(document).ready(function(){
        $("#loginButton").bind("click", doLogin);
        $("#registerButton").bind("click", changeToRegister);

    });
        </script>
    </body>
</html>
