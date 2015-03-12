package is.valitor.lokaverkefni.oturgjold;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

import is.valitor.lokaverkefni.oturgjold.models.Token;


public class TokenReceive extends Activity {
    private TextView editusr_id;
    private TextView editdevice_id;
    private TextView edittokenone;
    private TextView edittokentwo;
    private TextView edittokenthree;
    private TextView serviceResponse;
    private static final String TAG = "CardService";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_receive);
        Log.i(TAG, "Sending account number: " );
        editusr_id = (TextView) findViewById(R.id.editAccountName);
        editdevice_id = (TextView) findViewById(R.id.editAccountSSN);
        serviceResponse = (TextView) findViewById(R.id.responseText);

        Log.i(TAG, "Sending account number: " );
        JSONObject jsonAccountObject = new JSONObject();

        try {
            jsonAccountObject.put("usr_id", 99);
            jsonAccountObject.put("device_id", 65468);
            // Need three empty strings to be cool
            jsonAccountObject.put("tokenone", "party");
            jsonAccountObject.put("tokentwo", "on");
            jsonAccountObject.put("tokenthree", "bros");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            new FetchTokenTask().execute("https://kortagleypir.herokuapp.com/token", jsonAccountObject.toString());
        } else {
            // display error
            CharSequence message = "No network connection available.";
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);

            toast.show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_token_receive, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getToken(View view) {




    }

    private class FetchTokenTask extends AsyncTask<String, Void, JSONObject> {

        private Exception exception;

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                // params comes from the execute() call: params[0] is the url.
                return postUrl(params[0], params[1]);
            } catch (IOException e) {
                return null;
            } catch (Exception e) {
                this.exception = e;
            }
            return null;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(JSONObject result) {
            //textView.setText(result);
            int rCode = result.optInt("responseCode");

            if (rCode == 200) {
                try {
                    System.out.println(result.toString());
                    Gson gson = new Gson();
                    Token token  = gson.fromJson(result.toString(), Token.class);
                    Repository.setToken(getApplication(), token);


                    serviceResponse.setText("Skráning tókst.");
                    editusr_id.setText(token.getUsr_id());
                    editdevice_id.setText(token.getDevice_id());
                    edittokenone.setText(token.getTokenone());
                    edittokentwo.setText(token.getTokentwo());
                    edittokenthree.setText(token.getTokenthree());

                } catch (Exception e) {

                }
            } else {
                //editAccountName.setText("Misheppnuð skráning ahahahah!");
            }

        }

        private JSONObject postUrl(String serviceURL, String json_accountInfo) throws IOException {
            InputStream is = null;

            // Remake json-string into json object. There has to be a smarter way to do this, but I cant pass a string and json object
            JSONObject msg = new JSONObject();
            JSONObject ret = null;
            try {
                msg = new JSONObject(json_accountInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                URL url = new URL(serviceURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                // Establish connection
                conn.connect();
                // Get ready to write data
                OutputStream os = conn.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");

                osw.write(msg.toString());
                System.out.println(msg.toString());
                osw.flush();
                osw.close();
                // The service will respond with a JSON string of its own.
                int response = conn.getResponseCode();
                //Log.d( "The response is: " + response);
                System.out.println("The response code is: " + response);
                String responseMessage = conn.getResponseMessage();
                System.out.println("The response message is: " + responseMessage);


                // Convert the InputStream into a string
                is = conn.getInputStream();
                //System.out.println(is.available());
                ret = readJSON(is, 5000);
                try {
                    ret.put("responseCode", response);
                    ret.put("sentMessage", msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
            return ret;
        }

        // Reads an InputStream and converts it to a JSONObject.
        public JSONObject readJSON(InputStream stream, int len) throws IOException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);

            String msg = new String(buffer);
            JSONObject ret = new JSONObject();
            try {
                ret = new JSONObject(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ret;
        }
    }
}