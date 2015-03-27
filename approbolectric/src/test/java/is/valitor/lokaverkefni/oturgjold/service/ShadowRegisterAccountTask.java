package is.valitor.lokaverkefni.oturgjold.service;

import android.content.Context;

import org.json.JSONObject;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowAsyncTask;

import java.io.IOException;

import is.valitor.lokaverkefni.oturgjold.Validator;

/**
 * Created by eggert on 24/03/15.
 */

@Implements(RegisterAccountTask.class)
public class ShadowRegisterAccountTask extends ShadowAsyncTask {

    private Context context;
    private AsyncTaskCompleteListener<AsyncTaskResult<JSONObject>> listener;

    public void __constructor__(Context ctx, AsyncTaskCompleteListener<AsyncTaskResult<JSONObject>> listener)
    {
        this.context = ctx;
        this.listener = listener;
    }

    protected void onPostExecute(AsyncTaskResult<JSONObject> myPojo)
    {
        AsyncTaskResult<JSONObject> res = new AsyncTaskResult<JSONObject>(new IOException("Test error"));
        listener.onTaskComplete(res);
    }
}
