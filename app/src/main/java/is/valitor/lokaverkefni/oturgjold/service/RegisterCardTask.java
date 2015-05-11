package is.valitor.lokaverkefni.oturgjold.service;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by eggert on 27/03/15.
 */
public class RegisterCardTask extends RegisterTask {


    public RegisterCardTask(Context ctx, AsyncTaskCompleteListener<RegisterResult> listener) {
        super(ctx, listener);
    }
}
