package is.valitor.lokaverkefni.oturgjold;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.JsonReader;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import is.valitor.lokaverkefni.oturgjold.models.User;


public class RegisterAccountActivity extends Activity {

    private TextView editAccountName;
    private TextView editAccountSSN;
    private TextView serviceResponse;
    private ProgressBar loadingThings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);
        editAccountName = (TextView) findViewById(R.id.editAccountName);
        editAccountSSN = (TextView) findViewById(R.id.editAccountSSN);
        serviceResponse = (TextView) findViewById(R.id.responseText);
        loadingThings = (ProgressBar) findViewById(R.id.progressBar);

        loadingThings.setVisibility(View.INVISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_account_management, menu);
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

    // Confirm and return to main view
    public void confirmAccountRegister(View view) {
        finish();
    }

    public void registerAccount(View view) {

        // This has some limitations and bugs, apparently. Too bad I´m a newbie.
        // For now, call this the device id.
        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Lets have the support of the validator, if only to validate account name.
        Validator v = new Validator();

        // Account owner name
        EditText editAccountName = (EditText) findViewById(R.id.editAccountName);
        String name = editAccountName.getText().toString();

        String firstName = "John";
        String lastName = "Doe";

        if (!v.validateCardholderName(name)) {
            CharSequence message = getResources().getString(R.string.error_invalid_cardholder_name);
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
            toast.show();
            editAccountName.requestFocus();
            return;
        }
        // Consider doing this in an adult, sophisticated manner.
        String[] nameSplit = name.split(" ");
        if(nameSplit.length == 2) {
            firstName = nameSplit[0];
            lastName = nameSplit[1];
        }
        EditText editAccountSSN = (EditText) findViewById(R.id.editAccountSSN);
        String ssn = editAccountSSN.getText().toString();

        // Proper validation might be in order
        if (ssn.length() != 10 && ssn.endsWith("9")) {
            CharSequence message = "Improper kennitala motherfucker!";
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
            toast.show();
            editAccountSSN.requestFocus();
            return;
        }


        JSONObject jsonAccountObject = new JSONObject();

        try {
            //jsonAccountObject.put("firstName", firstName);
            //jsonAccountObject.put("lastName", lastName);
            jsonAccountObject.put("name", name);
            jsonAccountObject.put("ssn", ssn);
            jsonAccountObject.put("device_id", android_id);
        }
        catch(Exception e) {
            e.printStackTrace();
            return;
        }

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            loadingThings.setVisibility(View.VISIBLE);
            new RegisterAccountTask().execute("https://kortagleypir.herokuapp.com/user", jsonAccountObject.toString());
        } else {
            // display error
            CharSequence message = "No network connection available.";
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);

            toast.show();
        }

    }

    private class RegisterAccountTask extends AsyncTask<String, Void, JSONObject> {

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
            loadingThings.setVisibility(View.INVISIBLE);
            if(rCode == 200) {
                try {
                    Gson gson = new Gson();
                    User user = gson.fromJson(result.toString(), User.class);
                    Repository.setUser(getApplication(), user);

                    serviceResponse.setText("Skráning tókst.");
                    editAccountName.setText(user.getName());
                    editAccountSSN.setText(user.getSsn());

                    //go back to frontpage
                    // Set result to trigger action in mainActivity
                    setResult(MainActivity.RESULT_SUCCESS);
                    finish();
                }

                catch (Exception e) {

                }
            }
            else {
                editAccountName.setText("Misheppnuð skráning ahahahah!");
            }

        }

        private JSONObject postUrl(String serviceURL, String json_accountInfo) throws IOException {
            InputStream is = null;

            // Remake json-string into json object. There has to be a smarter way to do this, but I cant pass a string and json object
            JSONObject msg = new JSONObject();
            JSONObject ret = null;
            try {
                msg = new JSONObject(json_accountInfo);
            }
            catch (Exception e){
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
                }
                catch(Exception e) {
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
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            return ret;
        }
    }
}



