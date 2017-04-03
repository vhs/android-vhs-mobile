package ca.hackspace.vhs.mobile.nomos;

/**
 * Created by Thomas on 8/21/2016.
 */
public class UserPrincipal {
    public int id;
    public String[] permissions;

    public UserPrincipal() {}

    public interface IUserPrincipalResultHandler {
        void handle(UserPrincipal user);
        void error(Exception ex);
    }
}
