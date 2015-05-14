package is.valitor.lokaverkefni.oturgjold.service;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.StringReader;
import java.security.InvalidParameterException;

import is.valitor.lokaverkefni.oturgjold.repository.User;

/**
 * For registering user account on web service.
 * Created by eggert on 20/03/15.
 */
public class RegisterAccountTask extends RequestTask {

    private AsyncTaskCompleteListener<AsyncTaskResult<User>> listener;

    public RegisterAccountTask(AsyncTaskCompleteListener<AsyncTaskResult<User>> listener) {
        super("POST", "application/json", "application/json");

        this.listener = listener;
    }

    @Override
    protected void onPostExecute(RequestResult result) {

        // Network error
        if (result.getException() instanceof IOException) {
            listener.onTaskComplete(new AsyncTaskResult<User>(result.getException()));
            return;
        }

        // Invalid request or server error
        if (result.getResultCode() != 200) {
            InvalidParameterException e = new InvalidParameterException(result.getResultContent());
            listener.onTaskComplete(new AsyncTaskResult<User>(e));
        }

        User user;
        try{
            //Retrieve the content from the response and add to repository
            Gson gson = new Gson();
            JsonReader jr = new JsonReader(new StringReader(result.getResultContent()));
            jr.setLenient(true);
            user = gson.fromJson(jr, User.class);
        } catch (Exception e){
            e.printStackTrace();
            listener.onTaskComplete(new AsyncTaskResult<User>(e));
            return;
        }

        listener.onTaskComplete(new AsyncTaskResult<>(user));
    }

}
