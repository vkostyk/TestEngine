<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>TestEng</title>
    <style>
        .flex-container{
            padding: 70px 0;
            margin:auto;
            display: flex;
            justify-content: space-between;
            width: 800px;
            flex-flow: row wrap;
        }
        .flex-options {
            padding: 20px 0;
            flex-flow: column wrap;
            display: flex;
            justify-content: space-between;

        }
        textarea.question {
            width: 800px;
            height: 200px;
            resize:none;
        }

        select.question-type{
            width: 200px;

        }
        input[type=button].lister {
            width: 150px;
        }

        label.points {
            width:150px;
            text-align: right;
        }
        label.question-type {
            width:300px;
            text-align:center;
        }
        input[type=text].points{
            width:150px;
        }

        input[type=button].add-option {
            width: 200px;
        }


        .draggable {
            cursor: pointer;
        }
    </style>
    <script>
		var questionType = {ONEOPTION:1, FEWOPTIONS:2, MATCHING:3, INPUT:4, ESSAY:5};
		var questionText = ["One correct", "Few correct", "Matching", "User input", "Essay"];
		var optionsHTML = [];

        /*function setQuestionType(question)
         {
         // if (typeof questionType!=='number') {return false;}
         switch (question)
         {
         case questionType.ONEOPTION:
         return "<option value=''></option>";
         break;
         }
         }*/
		function displayOptions()
		{
			document.getElementById("options").innerHTML = "<br>";
			var index = document.getElementById("questionType").selectedIndex;
			switch(++index)
			{
				case questionType.ONEOPTION:
					for (var i = 0; i<3; i++)
                    {
						var container = document.createElement("div");
						container.setAttribute("id", "options"+i);
						document.getElementById("options").appendChild(container);
						var selector = document.createElement("input");
						selector.setAttribute("type", "radio");
						selector.setAttribute("name", "option");
						selector.setAttribute("id", "option"+i)
						document.getElementById("options"+i).appendChild(selector);
						var optionInput = document.createElement("input");
						optionInput.setAttribute("type", "text");
						optionInput.setAttribute("id", "optionInput"+i);
						document.getElementById("options"+i).appendChild(optionInput);
                    }
                    document.getElementById("option0").checked = true;
					break;
				case questionType.FEWOPTIONS:
                    for (var i = 0; i<3; i++) {
                        var container = document.createElement("div");
                        container.setAttribute("id", "options" + i);
                        document.getElementById("options").appendChild(container);
                        var selector = document.createElement("input");
                        selector.setAttribute("type", "checkbox");
                        selector.setAttribute("name", "option");
                        selector.setAttribute("id", "option" + i)
                        document.getElementById("options" + i).appendChild(selector);
                        var optionInput = document.createElement("input");
                        optionInput.setAttribute("type", "text");
                        optionInput.setAttribute("id", "optionInput" + i);
                        document.getElementById("options" + i).appendChild(optionInput);
                    }
                    document.getElementById("option0").checked = true;
					break;
                case questionType.MATCHING:
                    for (var i = 0; i<3; i++)
                    {
                        var container = document.createElement("div");
                        container.setAttribute("id", "options" + i);
                        document.getElementById("options").appendChild(container);

                        var optionLeft = document.createElement("input");
                        optionLeft.setAttribute("type", "text");
                        optionLeft.setAttribute("id", "optionLeft" + i);
                        document.getElementById("options" + i).appendChild(optionLeft);

                        document.getElementById("options" + i).innerHTML+="=&gt;";

                        var optionRight = document.createElement("input");
                        optionRight.setAttribute("type", "text");
                        optionRight.setAttribute("id", "optionRight" + i);
                        document.getElementById("options" + i).appendChild(optionRight);
                    }
					break;
				case questionType.INPUT:
                    var container = document.createElement("div");
                    container.setAttribute("id", "options0");
                    document.getElementById("options").appendChild(container);
                    var optionInput = document.createElement("input");
                    optionInput.setAttribute("type", "text");
                    optionInput.setAttribute("id", "optionInput0");
                    document.getElementById("options0").appendChild(optionInput);
                    break;
				case questionType.ESSAY:
                    //document.getElementById("options").innerHTML = "";
					break;
			}
		}
		function setOptions ()
		{
			for (var i = 0; i< questionText.length; i++)
			{
				var option = document.createElement("option");
				option.text =  questionText[i];
				option.value = option.id = "questionType"+(i+1);
				document.getElementById("questionType").add(option);
			}
			document.getElementById("questionType").options[0].selected = true;
		}

		function loadQuestion() {
            var xhttp = new XMLHttpRequest();
            xhttp.onreadystatechange = function() {
                if (this.readyState == 4 && this.status == 200) {
                    document.getElementById("task").innerHTML = this.responseText;
                }
            };
            xhttp.open("POST", "TestDBServlet", true);
            xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
            xhttp.send("name=test&password=hello");


        }
    </script>
</head>
<body onLoad="setOptions();displayOptions();">
    <div class="flex-container">
        <textarea class="question" id="task">Enter question text here</textarea>

        <select id="questionType" class="question-type" onchange="displayOptions()">

        </select>

        <input type="button" value="&lt;&lt;Previous" class="lister" onclick="loadQuestion()">
        <input type="button" value="Next&gt;&gt;" class="lister">
        <label for="maxPoints" class="points">Max points:</label>
        <input type="text" value="10" id="maxPoints" class="points">

        <input type="button" value="Add option" class="add-option">
        <label class="question-type">Choose question type</label>
        <label for="totalPoints" class="points">Total points:</label>
        <input type="text" value="10" id="totalPoints" class="points">
        <div class="flex-options" id="options">

        </div>
    </div>

</body>

</html>