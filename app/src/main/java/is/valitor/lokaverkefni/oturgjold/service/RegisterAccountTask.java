package is.valitor.lokaverkefni.oturgjold.service;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by eggert on 20/03/15.
 */
public class RegisterAccountTask extends AsyncTask<String, Void, RegisterResult> {

    private Context context;
    private AsyncTaskCompleteListener <RegisterResult>listener;
    private Exception exception;

    public RegisterAccountTask(Context ctx, AsyncTaskCompleteListener<RegisterResult> listener)
    {
        this.context = ctx;
        this.listener = listener;
    }

    protected void onPreExecute()
    {
        super.onPreExecute();
    }

    @Override
    protected RegisterResult doInBackground(String... params)
    {
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

    @Override
    protected void onPostExecute(RegisterResult result)
    {
        super.onPostExecute(result);
        listener.onTaskComplete(result);
    }

    private RegisterResult postUrl(String serviceURL, String json_accountInfo) throws IOException {
        InputStream is = null;

        // Remake json-string into json object. There has to be a smarter way to do this, but I cant pass a string and json object
        JSONObject msg = new JSONObject();

        RegisterResult result = new RegisterResult();
        try {
            msg = new JSONObject(json_accountInfo);
        }
        catch (Exception e){
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

            //handle the result
            result.setResultCode(conn.getResponseCode());
            result.setResultMessage(conn.getResponseMessage());

            if(result.getResultCode() == 200)
            {
                is = conn.getInputStream();
            }
            else
            {
                is = conn.getErrorStream();
            }
            result.setResultContent(readInput(is, conn.getContentLength()));
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return result;
            //System.out.println(is.available());
            /*result = readJSON(is, 5000);
            try {
                ret.put("responseCode", response);
                ret.put("sentMessage", msg);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return ret;*/
    }
    private String readInput(final InputStream stream, int length)
    {
        try{
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[length];
            reader.read(buffer);
            return new String(buffer);
        }catch(Exception ex)
        {
            return "";
        }
    }
    // Reads an InputStream and converts it to a JSONObject.
    /*JSONObject readJSON(InputStream stream, int len) throws IOException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);

        String msg = new String(buffer);
        JSONObject ret = new JSONObject();
        try {
            ret = new JSONObject(msg);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return ret;
    }*/

}
