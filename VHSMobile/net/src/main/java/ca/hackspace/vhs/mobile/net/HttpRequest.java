package ca.hackspace.vhs.mobile.net;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.NoCache;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Thomas on 8/17/2016.
 */
public class HttpRequest {

    public enum HTTP_METHOD {
        HEAD,
        POST,
        GET,
        DELETE,
        PUT,
        CONNECT,
        OPTIONS,
        TRACE,

    };

    protected String urlString;
    protected URL url;
    protected boolean isHTTPS = false;
    protected HTTP_METHOD method;
    protected String params;
    protected HashMap<String, String> param;
    protected HashMap<String, String> headers;
    protected int timeout;
    protected RequestQueue queue;
    protected String postBody;
    protected String postContentType;

    public HttpRequest() {
        this.param = new HashMap<>();
        this.headers = new HashMap<>();
        this.queue = new RequestQueue(new NoCache(), new BasicNetwork(new HurlStack()));
        this.queue.start();
    }

    public HttpRequest Url(URL url) {
        this.url = url;

        this.isHTTPS = url.getProtocol().equals("https");

        return this;
    }

    public HttpRequest Url(String url) {
        this.urlString = url;

        return this;
    }

    public HttpRequest Method(HTTP_METHOD method) {
        this.method = method;

        return this;
    }

    public HttpRequest Post(URL url) {
        return Url(url).Method(HTTP_METHOD.POST);
    }

    public HttpRequest Post(String url) {
        return Url(url).Method(HTTP_METHOD.POST);
    }

    public HttpRequest Get(URL url) {
        return Url(url).Method(HTTP_METHOD.GET);
    }

    public HttpRequest Get(String url) {
        return Url(url).Method(HTTP_METHOD.GET);
    }

    public HttpRequest Params(String params) {
        this.params = params;

        return this;
    }

    public HttpRequest Param(String key, String value) {
        this.param.put(key, value);

        return this;
    }

    public HttpRequest Send(String body) {

        this.postBody = body;

        return this;
    }

    public HttpRequest Type(String type) {
        this.postContentType = type;

        return this;
    }

    public HttpRequest Set(String key, String value) {
        this.headers.put(key, value);

        return this;
    }

    public HttpRequest Timeout(int timeout) {
        this.timeout = timeout;

        return this;
    }

    public boolean Validate() throws Exception {

        if (url == null && (urlString == null || urlString.equals(""))) throw new Exception("Missing URL");
        if (method == null) throw new Exception("Missing Method");

        if (url == null) {
            Url(new URL(urlString));
        }

        String protocol = url.getProtocol();

        if (!protocol.equals("http") && !protocol.equals("https")) throw new Exception("Unsupported protocol");

        return true;
    }


    public void End(IHttpResponseHandler handler) {
        HttpRequestRunner runner = new HttpRequestRunner(this, handler);

        Thread thread = new Thread(runner);

        thread.start();
    }

    private HttpResponse syncResponse;
    private Exception syncException;
    public HttpResponse SyncEnd() throws Exception {
        new HttpRequestRunner(this, new IHttpResponseHandler() {
            @Override
            public void handle(Exception ex, HttpResponse response) {
                syncException = ex;
                syncResponse = response;
            }
        }).run();

        if (syncException != null) throw syncException;

        return syncResponse;
    }
}
