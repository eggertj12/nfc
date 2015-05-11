package is.valitor.lokaverkefni.oturgjold.service;

import android.accounts.NetworkErrorException;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.StringReader;
import java.security.InvalidParameterException;

import is.valitor.lokaverkefni.oturgjold.CustomizeCardActivity;
import is.valitor.lokaverkefni.oturgjold.R;
import is.valitor.lokaverkefni.oturgjold.repository.Card;
import is.valitor.lokaverkefni.oturgjold.repository.Repository;
import is.valitor.lokaverkefni.oturgjold.repository.Token;
import is.valitor.lokaverkefni.oturgjold.repository.User;

/**
 * Created by eggert on 27/03/15.
 */
public class RegisterCardTask extends RequestTask {

    private AsyncTaskCompleteListener<AsyncTaskResult<Card>> listener;

    public RegisterCardTask(AsyncTaskCompleteListener<AsyncTaskResult<Card>> listener) {
        super("POST", "application/json", "application/json");

        this.listener = listener;
    }

    @Override
    protected void onPostExecute(RequestResult result) {

        // Network error
        if (result.getException() instanceof IOException) {
            listener.onTaskComplete(new AsyncTaskResult<Card>(result.getException()));
            return;
        }

        if (result.getResultCode() != 200) {
            InvalidParameterException e = new InvalidParameterException(result.getResultContent());
            listener.onTaskComplete(new AsyncTaskResult<Card>(e));
        }

        Card card = null;
        try{
            //Retrieve the content from the response and add to repository
            Gson gson = new Gson();
            JsonReader jr = new JsonReader(new StringReader(result.getResultContent()));
            jr.setLenient(true);
            card = gson.fromJson(jr, Card.class);
        } catch (Exception e){
            e.printStackTrace();
            listener.onTaskComplete(new AsyncTaskResult<Card>(e));
            return;
        }

        listener.onTaskComplete(new AsyncTaskResult<Card>(card));
    }

}
