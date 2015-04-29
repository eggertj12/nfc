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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import is.valitor.lokaverkefni.oturgjold.R;
import is.valitor.lokaverkefni.oturgjold.repository.Repository;
import is.valitor.lokaverkefni.oturgjold.repository.Token;
import is.valitor.lokaverkefni.oturgjold.repository.Transactions;

/**
 * Created by kla on 27.4.2015.
 */
public class GetTransactionsTask extends AsyncTask<String, Void, List<Transactions> >{

    private static final String TAG = "GETTRANSACTIONS";

    private Context context;
    private AsyncTaskCompleteListener<List<Transactions>> listener;

    public GetTransactionsTask(Context ctx, AsyncTaskCompleteListener<List<Transactions>> listener)
    {
        this.context = ctx;
        this.listener = listener;
    }

    protected void onPreExecute()
    {
        super.onPreExecute();
    }

    @Override
    protected List<Transactions> doInBackground(String... params)
    {
        try {
            // params comes from the execute() call: params[0] is the url.
            Log.d(TAG, "Loading url "+params[0]);
            return postUrl(params[0]);

        } catch (Exception e) {
            return new ArrayList<Transactions>();
        }
    }

    @Override
    protected void onPostExecute(List<Transactions> result)
    {
        super.onPostExecute(result);
        listener.onTaskComplete(result);
    }

    private List<Transactions> postUrl(String serviceURL) throws IOException {
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
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            // Establish connection
            conn.connect();




            // The service will respond with a JSON string of its own.
            int response = conn.getResponseCode();


           // Log.d( "The response is: " + response);
            System.out.println("The response code is: " + response);
            String responseMessage = conn.getResponseMessage();
            System.out.println("The response message is: " + responseMessage);


            // Convert the InputStream into a string
            is = conn.getInputStream();
            //System.out.println(is.available());
            return  readJSON(is, conn.getContentLength());

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        }catch(Exception ex){
            Log.e(TAG,"Error getting transactions",ex);
            return new ArrayList<Transactions>();
        } finally {
            if (is != null) {
                is.close();
            }
        }

    }

    // Reads an InputStream and converts it to a List of Transactions.
    public List<Transactions> readJSON(InputStream stream, int len) throws IOException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        int pos=0;
        int readChars = reader.read(buffer, pos, len);
        pos+=readChars;
        while(readChars>0)
        {
            readChars = reader.read(buffer, pos, len-pos);
            pos+=readChars;
        }
        Log.d(TAG,""+pos+readChars);
        reader.close();

        buffer = Arrays.copyOf(buffer,pos+1);

        String responseText = new String(buffer);

        return new Gson().fromJson(responseText, List.class);
    }
}
