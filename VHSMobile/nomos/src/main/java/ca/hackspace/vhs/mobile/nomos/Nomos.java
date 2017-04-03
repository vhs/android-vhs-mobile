package ca.hackspace.vhs.mobile.nomos;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import ca.hackspace.vhs.mobile.net.HttpRequest;
import ca.hackspace.vhs.mobile.net.HttpResponse;
import ca.hackspace.vhs.mobile.net.IHttpResponseHandler;

/**
 * Created by Thomas on 8/18/2016.
 */
public class Nomos {

    private class KEYS {
        public static final String TOKEN = "nomos:keys:token";
    }

    private String clientId;
    private String clientSecret;
    private AccessToken token;
    private UserPrincipal principal;
    private IServiceCache cache;

    private static Nomos instance;
    public static Nomos Services() {
        if (instance == null) instance = new Nomos();

        return instance;
    }

    private Nomos() {

    }

    private void PutCacheValue(String key, Object value) {
        if (this.cache != null)
            this.cache.PutValue(key, value);
    }

    private Object GetCacheValue(String key) {
        if (this.cache != null)
            return this.cache.Get(key);

        return null;
    }

    public void Cache(IServiceCache cache) {
        this.cache = cache;
    }

    private AccessToken GetToken() {
        Object obj = GetCacheValue(KEYS.TOKEN);

        if (obj != null) return (AccessToken) obj;

        return null;
    }

    public boolean IsAuthenticated() {
        return (token != null);
    }

    public void SetClient(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public boolean Authenicate(String username, String password) {
        HttpResponse response;

        try {
            response = new HttpRequest()
                    .Post("https://oauth.hackspace.ca/oauth/token")
                    .Set("Authorization", "Basic " + Base64.encodeToString((clientId + ":" + clientSecret).getBytes(), Base64.NO_WRAP))
                    .Set("Content-Type", "application/x-www-form-urlencoded")
                    .Param("grant_type", "password")
                    .Param("username", username)
                    .Param("password", password)
                    .SyncEnd();
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }

        if (response != null && response.getCode() == 200) {
            String body = response.getBody();

            Gson gson = new Gson();

            token = gson.fromJson(body, AccessToken.class);

            return true;
        }

        return false;
    }

    public void GetCurrentUserPrincipal(final UserPrincipal.IUserPrincipalResultHandler handler) {
        new HttpRequest()
        .Get("http://membership.hackspace.ca/services/web/AuthService1.svc/CurrentUser")
        .Set("Authorization", "Bearer " + token.access_token)
        .End(new IHttpResponseHandler() {
            @Override
            public void handle(Exception ex, HttpResponse response) {
                if (ex != null) {
                    ex.printStackTrace();
                    handler.error(ex);
                    return;
                }

                if (response != null && response.getCode() == 200) {
                    principal = new Gson().fromJson(response.getBody(), UserPrincipal.class);
                    handler.handle(principal);
                    return;
                }

                handler.error(new Exception("Unknown Error"));
            }
        });
    }

    public void GetCurrentUser(final User.IUserResultHandler handler) {
        if (principal == null) {
            GetCurrentUserPrincipal(new UserPrincipal.IUserPrincipalResultHandler() {
                @Override
                public void handle(UserPrincipal user) {
                    GetUser(user.id, handler);
                }

                @Override
                public void error(Exception ex) {
                    handler.error(ex);
                }
            });
        } else {
            GetUser(principal.id, handler);
        }
    }

    public void GetUser(int id, final User.IUserResultHandler handler) {
        new HttpRequest()
        .Post("http://membership.hackspace.ca/services/web/UserService1.svc/GetUser")
        .Send("{ \"userid\":"+id+"}")
        .Type("application/json")
        .Set("Authorization", "Bearer " + token.access_token)
        .End(new IHttpResponseHandler() {
            @Override
            public void handle(Exception ex, HttpResponse response) {
                if (ex != null) {
                    handler.error(ex);
                    return;
                }

                if (response != null && response.getCode() == 200) {
                    handler.handle(new Gson().fromJson(response.getBody(), User.class));
                    return;
                }

                handler.error(new Exception("Unknown Error"));
            }
        });
    }

    public void ValidateGenuineCard(String key, final IBoolResultHandler handler) {
        new HttpRequest()
        .Post("http://membership.hackspace.ca/services/web/MemberCardService1.svc/ValidateGenuineCard")
        .Send("{ \"key\": \""+key+"\" }")
        .Type("application/json")
        .Set("Authorization", "Bearer " + token.access_token)
        .End(new IHttpResponseHandler() {
            @Override
            public void handle(Exception ex, HttpResponse response) {
                if (ex != null) {
                    handler.error(ex);
                    return;
                }

                if (response != null && response.getCode() == 200) {
                    handler.handle(new Gson().fromJson(response.getBody(), Boolean.class));
                    return;
                }

                handler.error(new Exception("Unknown Error"));
            }
        });
    }

    public interface IBoolResultHandler {
        void handle(boolean value);
        void error(Exception ex);
    }
}
