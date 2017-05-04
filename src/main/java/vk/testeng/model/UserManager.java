package vk.testeng.model;

import java.util.ArrayList;

public class UserManager {
    public enum LoginState {NOSUCHUSER, WRONGPASSWORD, ISUSER, ISADMIN};
    public class LoginResult
    {
        private LoginState loginState;
        private int userId;
        private LoginResult(LoginState loginState, int userId)
        {
            this.loginState = loginState;
            this.userId = userId;
        }
        public LoginState getState()
        {
            return loginState;
        }
        public int getUserId()
        {
            return userId;
        }
    }
    private ArrayList<User> users = new ArrayList<User>();
    public void addUser(User user)
    {
        users.add(user);
    }
    public LoginResult login(User user)
    {
        int counter = 0;
        for (User u : users)
        {
            if (users.get(counter).getUserName()==user.getUserName())
            {
                if (users.get(counter).getPassword()==user.getPassword())
                {
                    return new LoginResult((users.get(counter).getAccessType()==User.AccessType.ADMIN)?LoginState.ISADMIN:LoginState.ISUSER,users.get(counter).getId());
                } else {
                    return new LoginResult(LoginState.WRONGPASSWORD,users.get(counter).getId());
                }
            }
        }
        return new LoginResult(LoginState.NOSUCHUSER,-1);
    }
}
