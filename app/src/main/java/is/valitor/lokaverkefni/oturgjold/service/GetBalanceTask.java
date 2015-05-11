package is.valitor.lokaverkefni.oturgjold.service;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidParameterException;
import java.text.ParseException;

import is.valitor.lokaverkefni.oturgjold.repository.Card;


/**
 * Created by kla on 20.4.2015.
 */
public class GetBalanceTask extends RequestTask {

    private AsyncTaskCompleteListener<AsyncTaskResult<Integer>> listener;

    public GetBalanceTask(AsyncTaskCompleteListener<AsyncTaskResult<Integer>> listener) {
        super("GET", "text/plain", "text/plain");

        this.listener = listener;
    }

    @Override
    protected void onPostExecute(RequestResult result) {

        // Network error
        if (result.getException() instanceof IOException) {
            listener.onTaskComplete(new AsyncTaskResult<Integer>(result.getException()));
            return;
        }

        if (result.getResultCode() != 200) {
            InvalidParameterException e = new InvalidParameterException(result.getResultContent());
            listener.onTaskComplete(new AsyncTaskResult<Integer>(e));
        }

        Integer balance = 0;
        try {
            balance = Integer.parseInt(result.getResultContent());
            System.out.println("Balance is: " + balance);
        } catch (Exception e){
            e.printStackTrace();
            listener.onTaskComplete(new AsyncTaskResult<Integer>(e));
            return;
        }

        listener.onTaskComplete(new AsyncTaskResult<>(balance));
    }

}


