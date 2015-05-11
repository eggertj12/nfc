package is.valitor.lokaverkefni.oturgjold.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import is.valitor.lokaverkefni.oturgjold.R;
import is.valitor.lokaverkefni.oturgjold.repository.Repository;
import is.valitor.lokaverkefni.oturgjold.repository.Token;

/**
 * Created by kla on 23.3.2015.
 */
public class GetTokenTask extends AsyncTask <String, Void, JSONObject> {
    private Exception exception;
    final private Context appContext;

    public GetTokenTask(Context appContext) {
        this.appContext = appContext;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        try {
            // params comes from the execute() call: params[0] is the url.
            return postUrl(params[0], params[1]);
        } catch (IOException e) {
            return null;
        } catch (Exception e) {
            this.exception = e;
        }
        return null;
    }

    // onPostExecute runs after the AsyncTask finishes.
    @Override
    protected void onPostExecute(JSONObject result) {
        //textView.setText(result);

        // TODO: Handle failed connections attempts somehow more elegantly than ignoring them
        if (result == null) {
            return;
        }

        int rCode = result.optInt("responseCode");

        if (rCode == 200) {
            try {
                System.out.println(result.toString());
                Gson gson = new Gson();
                Token token  = gson.fromJson(result.toString(), Token.class);
                Log.d("response",result.toString());

                int currentCard = token.getCard_id();
                Repository.addToken(this.appContext, currentCard, token);

                // Sequentially fill upp token limit
                // Could this be solved more elegantly?
                if (Repository.getTokenCount(this.appContext, currentCard) < 3) {

                    // Clear token data, but reuse rest
                    token.setTokenitem("");
                    String tokenJson = gson.toJson(token, Token.class);
                    new GetTokenTask(this.appContext)
                            .execute(this.appContext.getString(R.string.service_token_url), tokenJson);
                }

            } catch (Exception e) {

            }
        } else {
            //editAccountName.setText("Misheppnuð skráning ahahahah!");
        }

    }

    private JSONObject postUrl(String serviceURL, String json_accountInfo) throws IOException {
        InputStream is = null;

        // Remake json-string into json object.
        // There has to be a smarter way to do this, but I cant pass a string and json object
        JSONObject msg = new JSONObject();
        JSONObject ret = null;
        try {
            msg = new JSONObject(json_accountInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            URL url = new URL(serviceURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            // Establish connection
            conn.connect();
            // Get ready to write data
            OutputStream os = conn.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");

            osw.write(msg.toString());
            System.out.println(msg.toString());
            osw.flush();
            osw.close();
            // The service will respond with a JSON string of its own.
            int response = conn.getResponseCode();
            //Log.d( "The response is: " + response);
            System.out.println("The response code is: " + response);
            String responseMessage = conn.getResponseMessage();
            System.out.println("The response message is: " + responseMessage);


            // Convert the InputStream into a string
            is = conn.getInputStream();
            //System.out.println(is.available());
            ret = readJSON(is, 5000);
            try {
                ret.put("responseCode", response);
                ret.put("sentMessage", msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return ret;
    }

    // Reads an InputStream and converts it to a JSONObject.
    public JSONObject readJSON(InputStream stream, int len) throws IOException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);

        String msg = new String(buffer);
        JSONObject ret = new JSONObject();
        try {
            ret = new JSONObject(msg);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("GETTOKEN", "Error reading token " + msg);
        }
        return ret;
    }
}
