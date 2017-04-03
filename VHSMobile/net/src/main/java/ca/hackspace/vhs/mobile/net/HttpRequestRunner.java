package ca.hackspace.vhs.mobile.net;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.NoCache;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Thomas on 8/17/2016.
 */
public class HttpRequestRunner implements Runnable {

    private HttpRequest request;
    private IHttpResponseHandler handler;
    private boolean isRequestFinished = false;

    public HttpRequestRunner(HttpRequest request, IHttpResponseHandler handler) {
        this.request = request;
        this.handler = handler;
    }

    private void handle(Exception ex, HttpResponse response) {
        try {
            this.handler.handle(ex, response);
        } catch(Exception e) {
            e.printStackTrace();
        }

        this.isRequestFinished = true;
    }

    @Override
    public void run() {

        try {
            request.Validate();
        } catch (Exception e) {
            e.printStackTrace();

            handle(e, null);
            return;
        }

        final StringRequest req = new StringRequest(Request.Method.POST, request.url.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                handle(null, new HttpResponse(200, response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorString = "Unknown error";
                int statusCode = 500;
                if (error != null && error.networkResponse != null) {
                    if (error.networkResponse.data != null)
                        errorString = new String(error.networkResponse.data, StandardCharsets.UTF_8);

                    statusCode = error.networkResponse.statusCode;
                }

                handle(new Exception(errorString), new HttpResponse(statusCode, errorString));
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return request.param;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {

                if (request.postBody != null) return request.postBody.getBytes();

                return super.getBody();
            }

            @Override
            public String getBodyContentType() {

                if (request.postContentType != null) return request.postContentType;

                return super.getBodyContentType();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return request.headers;
            }
        };

        request.queue.add(req);

        while(!this.isRequestFinished) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                this.isRequestFinished = true;
            }
        }

        /*URLConnection connection;

        try {
             connection = request.url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();

            handler.handle(e, null);
            return;
        }

        HttpURLConnection http = (HttpURLConnection) connection;

        byte[] bytes;

        try {
             bytes = request.params.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

            handler.handle(e, null);
            return;
        }

        http.setConnectTimeout(request.timeout);
        http.setReadTimeout(request.timeout);

        http.setUseCaches(false);
        http.setDoOutput(true);
        http.setFixedLengthStreamingMode(bytes.length);

        try {
            http.setRequestMethod(request.method.name());
        } catch (ProtocolException e) {
            e.printStackTrace();

            handler.handle(e, null);
            return;
        }

        for (String key : request.headers.keySet()) {
            http.setRequestProperty(key, request.headers.get(key));
        }

        try {
            OutputStream outputStream = http.getOutputStream();

            outputStream.write(bytes);

            outputStream.close();

            int responseCode = http.getResponseCode();

            String responseString = "";

            String line;

            InputStreamReader inputStreamReader = new InputStreamReader(new BufferedInputStream(http.getInputStream()));

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while ((line = bufferedReader.readLine()) != null) {
                responseString += line;
            }

            HttpResponse response = new HttpResponse(responseCode, responseString);

            handler.handle(null, response);

            http.disconnect();
        } catch (Exception e) {
            e.printStackTrace();

            handler.handle(e, null);
        }
        */
    }
}
