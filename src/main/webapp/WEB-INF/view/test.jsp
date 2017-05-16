
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <script src="jquery.js"></script>
    <script>

        $( document ).ready(function() {

            $.getJSON('./test', function(data){
                document.write(data.questions[0].task);
            });


        })
    </script>
    <title>test</title>
</head>
<body>

</body>
</html>
