package is.valitor.lokaverkefni.oturgjold.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import is.valitor.lokaverkefni.oturgjold.repository.Transaction;

/**
 * Created by kla on 27.4.2015.
 */
public class GetTransactionsTask extends AsyncTask<String, Void, List<Transaction> >{

    private static final String TAG = "GETTRANSACTIONS";

    private Context context;
    private AsyncTaskCompleteListener<List<Transaction>> listener;

    public GetTransactionsTask(Context ctx, AsyncTaskCompleteListener<List<Transaction>> listener)
    {
        this.context = ctx;
        this.listener = listener;
    }

    protected void onPreExecute()
    {
        super.onPreExecute();
    }

    @Override
    protected List<Transaction> doInBackground(String... params)
    {
        try {
            // params comes from the execute() call: params[0] is the url.
            Log.d(TAG, "Loading url "+params[0]);
            return postUrl(params[0]);

        } catch (Exception e) {
            return new ArrayList<Transaction>();
        }
    }

    @Override
    protected void onPostExecute(List<Transaction> result)
    {
        super.onPostExecute(result);
        listener.onTaskComplete(result);
    }

    private List<Transaction> postUrl(String serviceURL) throws IOException {
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
            String responseMessage = conn.getResponseMessage();

            // Convert the InputStream into a string
            is = conn.getInputStream();
            //System.out.println(is.available());
            return  readJSON(is, conn.getContentLength());

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        }catch(Exception ex){
            Log.e(TAG,"Error getting transactions",ex);
            return new ArrayList<Transaction>();
        } finally {
            if (is != null) {
                is.close();
            }
        }

    }

    // Reads an InputStream and converts it to a List of Transactions.
    public List<Transaction> readJSON(InputStream stream, int len) throws IOException {
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
        JsonReader jsonReader = new JsonReader(new StringReader(responseText));
        jsonReader.setLenient(true);

        final Type listType = new TypeToken<ArrayList<Transaction>>() {}.getType();
        return getGson().fromJson(jsonReader, listType);
    }

    private Gson getGson()
    {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class,new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                try {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }catch (Exception ex){return  null;}
            }
        });

        Gson gson = builder.create();

        return gson;
    }
}
