package ca.hackspace.vhs.mobile.net;

/**
 * Created by Thomas on 8/17/2016.
 */
public class HttpResponse {

    private int code;
    private String body;

    public HttpResponse(int code, String body) {
        this.code = code;
        this.body = body;
    }

    public int getCode() {
        return this.code;
    }

    public String getBody() {
        return this.body;
    }
}
