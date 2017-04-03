package ca.hackspace.vhs.mobile.nomos;

/**
 * Created by Thomas on 8/18/2016.
 */
public class User {
    public int id;
    public String username;
    public String email;
    public String fname;
    public String lname;
    public String active;
    public String expires;
    public String created;


    public User() {}

    public interface IUserResultHandler {
        void handle(User user);
        void error(Exception ex);
    }
}
