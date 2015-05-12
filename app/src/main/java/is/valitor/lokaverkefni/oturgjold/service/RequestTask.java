package is.valitor.lokaverkefni.oturgjold.service;

import android.os.AsyncTask;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidParameterException;

/**
 * Created by kla on 11.5.2015.
 * Super class to take care of requests
 */
public class RequestTask extends AsyncTask<String, Void, RequestResult> {

    private String method;
    private String hdrContentType;
    private String hdrAccept;

    public RequestTask(String m, String ct, String a) {
        this.method = m;
        this.hdrContentType = ct;
        this.hdrAccept = a;
    }

    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected RequestResult doInBackground(String... params) {
        try {
            // params comes from the execute() call: params[0] is the url.
            if (params.length == 2) {
                return postUrl(params[0], params[1]);
            } else if (params.length == 1) {
                return postUrl(params[0], null);
            } else {
                throw new InvalidParameterException("Invalid number of parameters to execute()");
            }
        } catch (Exception e) {
            // All exceptions are caught and returned to be handled by caller
            e.printStackTrace();
            return new RequestResult(e);
        }
    }

    @Override
    protected void onPostExecute(RequestResult result) {
        super.onPostExecute(result);
    }

    private RequestResult postUrl(String serviceURL, String requestData) throws IOException {
        InputStream is = null;

        // Remake json-string into json object. There has to be a smarter way to do this, but I cant pass a string and json object
        JSONObject msg = new JSONObject();

        RequestResult result = new RequestResult();

        boolean doOutput = false;
        if (requestData != null) {
            try {
                msg = new JSONObject(requestData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            doOutput = true;
        }

        try {
            URL url = new URL(serviceURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod(this.method);
            conn.setDoInput(true);
            conn.setDoOutput(doOutput);
            conn.setUseCaches(false);
            conn.setRequestProperty("Content-Type", this.hdrContentType);
            conn.setRequestProperty("Accept", this.hdrAccept);
            // Establish connection
            conn.connect();

            // Need to write request data?
            if (doOutput) {
                // Get ready to write data
                OutputStream os = conn.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");

                osw.write(msg.toString());
                osw.flush();
                osw.close();
            }

            // The service will respond with a string of its own.
            int response = conn.getResponseCode();

            //handle the result
            result.setResultCode(conn.getResponseCode());
            result.setResultMessage(conn.getResponseMessage());

            if (result.getResultCode() == 200) {
                is = conn.getInputStream();
            } else {
                is = conn.getErrorStream();
            }
            result.setResultContent(readInput(is, conn.getContentLength()));
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return result;

    }

    private String readInput(final InputStream stream, int length) throws IOException {

        StringWriter result = new StringWriter();
        IOUtils.copy(stream, result);
        return result.toString();

    }

}
