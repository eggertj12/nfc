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
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import is.valitor.lokaverkefni.oturgjold.repository.Transaction;
import is.valitor.lokaverkefni.oturgjold.repository.User;

/**
 * Created by kla on 27.4.2015.
 */
public class GetTransactionsTask extends RequestTask {

    private AsyncTaskCompleteListener<AsyncTaskResult<List<Transaction>>> listener;

    public GetTransactionsTask(AsyncTaskCompleteListener<AsyncTaskResult<List<Transaction>>> listener) {
        super("GET", "application/json", "application/json");

        this.listener = listener;
    }

    @Override
    protected void onPostExecute(RequestResult result) {

        // Network error
        if (result.getException() instanceof IOException) {
            listener.onTaskComplete(new AsyncTaskResult<List<Transaction>>(result.getException()));
            return;
        }

        if (result.getResultCode() != 200) {
            InvalidParameterException e = new InvalidParameterException(result.getResultContent());
            listener.onTaskComplete(new AsyncTaskResult<List<Transaction>>(e));
        }

        List<Transaction> transactions;
        try{
            //Retrieve the content from the response and add to repository
            Gson gson = new Gson();
            JsonReader jsonReader = new JsonReader(new StringReader(result.getResultContent()));
            jsonReader.setLenient(true);
            final Type listType = new TypeToken<ArrayList<Transaction>>() {}.getType();
            transactions = getGson().fromJson(jsonReader, listType);
        } catch (Exception e){
            e.printStackTrace();
            listener.onTaskComplete(new AsyncTaskResult<List<Transaction>>(e));
            return;
        }

        listener.onTaskComplete(new AsyncTaskResult<List<Transaction>>(transactions));
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
