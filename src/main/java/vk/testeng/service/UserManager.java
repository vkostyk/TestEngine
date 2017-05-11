package vk.testeng.service;

import vk.testeng.model.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class UserManager {
    public enum LoginState {NOSUCHUSER, WRONGPASSWORD, ISUSER, ISADMIN}
    public enum RegState {USEREXISTS, SUCCESS}
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
    public class RegResult
    {
        private RegState regState;
        private int userId;
        private RegResult(RegState regState, int userId)
        {
            this.regState = regState;
            this.userId  = userId;
        }
        public RegState getState() {return this.regState;}
        public int getUserId() {return this.userId;}
    }
    public LoginResult login(User user)
    {

        Connection c = null;
        Statement stmt = null;

        try {
            c = ConnectionManager.connect();
            stmt = c.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE name='"+user.getUsername()+"';");
            boolean userExists = rs.next();
            if (!userExists)

            {
                return new LoginResult(LoginState.NOSUCHUSER, -1);
            }
            int userId = rs.getInt("id");
            if (rs.getString("password")!=user.getPassword())
            {
                return new LoginResult(LoginState.WRONGPASSWORD, userId);
            }
            return  new LoginResult((rs.getString("access")=="ADMIN"?LoginState.ISADMIN:LoginState.ISUSER), rs.getInt(userId));


        } catch (Exception e){
            throw new RuntimeException("DB interaction failed on users table");
        }
    }

    public RegResult register(User user)
    {
        Connection c = null;
        Statement stmt = null;
        try {
            c = ConnectionManager.connect();
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE name='"+user.getUsername()+"';");
            boolean userExists = rs.next();
            if (userExists)
            {
                return new RegResult(RegState.USEREXISTS, rs.getInt("id"));
            } else {
                rs = stmt.executeQuery("INSERT INTO users(name, password, access) VALUES('"+user.getUsername()+"', '"+user.getPassword()+"', '"+user.getAccessType().name()+"') RETURNING id;");
                return new RegResult(RegState.SUCCESS, rs.getInt("id"));
            }
        } catch (Exception e){
            throw new RuntimeException("DB interaction failed on users table");
        }
    }

}
