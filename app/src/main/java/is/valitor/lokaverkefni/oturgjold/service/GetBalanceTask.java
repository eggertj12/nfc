package is.valitor.lokaverkefni.oturgjold.service;


import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * For retrieving card balance information from web service.
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

        Integer balance;
        try {
            balance = Integer.parseInt(result.getResultContent());
        } catch (Exception e){
            e.printStackTrace();
            listener.onTaskComplete(new AsyncTaskResult<Integer>(e));
            return;
        }

        listener.onTaskComplete(new AsyncTaskResult<>(balance));
    }

}


