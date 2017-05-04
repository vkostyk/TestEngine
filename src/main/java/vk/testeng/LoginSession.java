package vk.testeng;

public class LoginSession {
    private User currentUser;
    private int sessionId;
    private boolean isAdmin;
    private int generateSessionId()
    {
        return Math.round( (float)Math.random()*100000 );
    }
    public LoginSession(User user)
    {
        createSession(user);
    }
    public void createSession(User user)
    {
        this.currentUser = user;
        this.isAdmin = user.getAccessType()==User.AccessType.ADMIN;
        this.sessionId = generateSessionId();
    }
    public User getCurrentUser()
    {
        return currentUser;
    }
    public int getSessionId()
    {
        return sessionId;
    }
}
