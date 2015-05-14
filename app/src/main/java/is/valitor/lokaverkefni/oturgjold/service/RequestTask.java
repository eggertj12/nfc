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

    public RequestTask(String httpMethod, String contentType, String accept) {
        this.method = httpMethod;
        this.hdrContentType = contentType;
        this.hdrAccept = accept;
    }

    @Override
    protected RequestResult doInBackground(String... params) {
        try {
            // params comes from the execute() call:
            // params[0] is the url
            // params[1] contains request data if present
            if (params.length == 2) {
                return postUrl(params[0], params[1]);
            } else if (params.length == 1) {
                return postUrl(params[0], null);
            } else {
                throw new InvalidParameterException("Invalid number of parameters to execute()");
            }
        } catch (Exception e) {
            // All exceptions are caught, wrapped and returned to be handled by caller
            e.printStackTrace();
            return new RequestResult(e);
        }
    }

    /**
     * Setup and execute the request
     *
     * @param serviceURL  Requested URL
     * @param requestData Request data, should be null if not present
     * @return The result of the request wrapped in RequestResult instance
     * @throws IOException
     */
    private RequestResult postUrl(String serviceURL, String requestData) throws IOException {
        InputStream is = null;

        // Remake json-string into json object. There has to be a smarter way to do this,
        // but I cant pass a string and json object
        JSONObject msg = new JSONObject();

        RequestResult result = new RequestResult();

        // Check if request data is present and prepare it
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

            // Timeout values in milliseconds
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
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
                // Prepare writer
                OutputStream os = conn.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");

                osw.write(msg.toString());
                osw.flush();
                osw.close();
            }

            // Handle the result
            result.setResultCode(conn.getResponseCode());
            result.setResultMessage(conn.getResponseMessage());

            if (result.getResultCode() == 200) {
                is = conn.getInputStream();
            } else {
                is = conn.getErrorStream();
            }
            result.setResultContent(readInput(is));
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return result;
    }

    /**
     * Read the connection response from stream
     *
     * @param stream The inputstream to read from
     * @return Read response
     * @throws IOException
     */
    private String readInput(final InputStream stream) throws IOException {
        StringWriter result = new StringWriter();
        IOUtils.copy(stream, result);
        return result.toString();
    }
}
