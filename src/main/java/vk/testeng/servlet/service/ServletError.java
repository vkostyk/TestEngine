package vk.testeng.servlet.service;

    public enum ServletError
    {

        NO_ACTION_SET("No action set"),
        WRONG_ACTION_PARAMETER("Wrong action parameter"),
        USER_NOT_ADMIN("Only admin can manage tests"),
        EDIT_SESSION_ALIVE("Finish your previous edition first"),
        EDIT_SESSION_DEAD("Start editing session prior to finishing editing session"),
        NO_MAX_POINTS("Max points must be provided at initiation of test addition"),
        NOT_LOGGED("Login first"),
        TEST_ID_NOT_SET("Test id must be set"),
        NO_QUESTION_JSON("Question JSON data not sent"),
        USER_IS_ADMIN("Login as user to pass a test"),
        JSON_FORMAT("Wrong JSON format"),
        JSON_ID("Question id not set or wrong"),
        JSON_TYPE("Question type not set or wrong"),
        JSON_MAX_POINTS("Question max points not set or wrong"),
        JSON_TASK("Question task not set or wrong"),
        JSON_OPTIONS("Question options not set or wrong"),
        JSON_ANSWER("Question answer not set or wrong"),
        QUIZ_SESSION_ALIVE("Finish active quiz/test prior to starting a new one")
        ;
        private static final String LEFT = "{\"error\":\"";
        private static final String RIGHT = "\"}";

        private String error;
        ServletError(String error)
        {
            this.error = LEFT +error+ RIGHT;
        }
        public String get()
        {
            return error;
        }
    }

