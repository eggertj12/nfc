package is.valitor.lokaverkefni.oturgjold.service;


import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;




/**
 * Created by kla on 20.4.2015.
 */
public class GetBalanceTask extends AsyncTask <String, Void, Integer> {

    private AsyncTaskCompleteListener<Integer> listener;

    public GetBalanceTask( AsyncTaskCompleteListener <Integer> listener)
    {
        this.listener = listener;
    }
    private Exception exception;

    @Override
    protected void onPostExecute(Integer balance) {
        super.onPostExecute(balance);
        listener.onTaskComplete(balance);
    }

    @Override
    protected Integer doInBackground(String... params) {
        try {
            // params comes from the execute() call: params[0] is the url.

            Integer balance = getUrl(params[0]);

            return balance;
        } catch (IOException e) {
            return -2;
        } catch (Exception e) {
            this.exception = e;
        }
        return -1;
    }
    private Integer getUrl(String serviceURL) throws IOException {
        InputStream is = null;



        try {
            URL url = new URL(serviceURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(false);
            conn.setUseCaches(false);
            conn.setRequestProperty("Content-Type", "text/plain");
            conn.setRequestProperty("Accept", "text/plain");
            // Establish connection
            conn.connect();
            // Get ready to write data
           /* OutputStream os = conn.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
            osw.flush();
            osw.close();*/
            // The service will respond with a JSON string of its own.
            int response = conn.getResponseCode();
            //Log.d( "The response is: " + response);
            System.out.println("The response code is: " + response);
            String responseMessage = conn.getResponseMessage();
            System.out.println("The response message is: " + responseMessage);


            // Convert the InputStream into a string
            is = conn.getInputStream();
            //System.out.println(is.available());
          return readBalance(is, conn.getContentLength());

        }
        catch (Exception e)
        {
           e.printStackTrace();
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return -3;
    }

    // Reads an InputStream and converts it to a JSONObject.
    public Integer readBalance(InputStream stream, int len) throws IOException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);

        String msg = new String(buffer);

        try {
           Integer parsedBalance = Integer.parseInt(msg);

           return parsedBalance;
        } catch (Exception e) {

            return -1;
        }

    }
}


