package ca.hackspace.vhs.mobile.net;

/**
 * Created by Thomas on 8/17/2016.
 */
public interface IHttpResponseHandler {
    void handle(Exception ex, HttpResponse response);
}
