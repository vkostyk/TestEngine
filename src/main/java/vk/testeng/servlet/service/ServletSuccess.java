package vk.testeng.servlet.service;

public enum ServletSuccess
{
    ID {
        @Override
        public String get(int id)
        {
            return ID_LEFT+id+ID_RIGHT;
        }
    },
    EDIT("Edit successful"),
    FINISH_EDIT("Edition session finished successfully"),
    QUIZ_FINISHED("Quiz successfully finished"),
    LOGGED("Successfully logged in"),
    LOGGED_OUT("Successfully logged out"),
    REGISTERED("Successfully registered");
    private static final String ID_LEFT = "{\"id\":";
    private static final String ID_RIGHT = "}";
    private static final String LEFT = "{\"success\":\"";
    private static final String RIGHT = "\"}";

    private String success;
    ServletSuccess(String success)
    {
        this.success = LEFT +success+ RIGHT;
    }
    ServletSuccess()
    {

    }
    public String get(int id)
    {
        return success;
    }
    public String get()
    {
        return success;
    }
}