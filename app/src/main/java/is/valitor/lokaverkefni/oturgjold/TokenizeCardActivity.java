package is.valitor.lokaverkefni.oturgjold;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
//import android.app.FragmentTransaction;
import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;


public class TokenizeCardActivity extends Activity {

    private TextView responseDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tokenize_card);
        responseDisplay = (TextView) findViewById(R.id.serviceResponse);

        Intent intent = getIntent();

        String cardNumber = intent.getStringExtra(RegisterCardActivity.MSG_CARDNUMBER);
        String cardHolder = intent.getStringExtra(RegisterCardActivity.MSG_CARDHOLDER);
        String cardType = intent.getStringExtra(RegisterCardActivity.MSG_CARDTYPE);
        String cardCvv = intent.getStringExtra(RegisterCardActivity.MSG_CARDCVV);
        String cardMonth = intent.getStringExtra(RegisterCardActivity.MSG_CARDMONTH);
        String cardYear = intent.getStringExtra(RegisterCardActivity.MSG_CARDYEAR);

        LinearLayout root = (LinearLayout) findViewById(R.id.tokenize_linear);

        TextView vHolder = new TextView(this);
        vHolder.setText("cardholder: " + cardHolder);
        root.addView(vHolder);

        TextView vType = new TextView(this);
        vType.setText("card type: " + cardType);
        root.addView(vType);

        TextView vNumber = new TextView(this);
        vNumber.setText("cardnumber: " + cardNumber);
        root.addView(vNumber);

        TextView vValid = new TextView(this);
        vValid.setText("valid: " + cardMonth + "/" + cardYear);
        root.addView(vValid);

        AccountStorage.SetAccount(this, cardNumber);

        // Communicate with the service:
        try {
            // Make JSON
            JSONObject outMsg = new JSONObject();
            outMsg.put("usr_id", 6666);
            outMsg.put("cardnumber", cardNumber);
            outMsg.put("cardholder", cardHolder);
            outMsg.put("validity", cardMonth + "/" + cardYear);
            outMsg.put("cvv", cardCvv);
            String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            outMsg.put("dev_id", android_id);



            // Ensure connection
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                // Communicate with service
                new TokenizeCardTask().execute("https://kortagleypir.herokuapp.com/card", outMsg.toString());
            } else {
                // display error
                CharSequence message = "No network connection available.";
                Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);

                toast.show();
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tokenize_card, menu);
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


    private class TokenizeCardTask extends AsyncTask<String, Void, JSONObject> {

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

            if(result != null) {
                int rCode = result.optInt("responseCode");

                if (rCode == 200) {
                    try {

                        responseDisplay.setText("Kort hefur verið skráð.");
                    } catch (Exception e) {

                    }
                } else {
                    System.out.println(rCode);
                }
            }

        }

        private JSONObject postUrl(String serviceURL, String json_accountInfo) throws IOException {
            InputStream is = null;

            // Remake json-string into json object. There has to be a smarter way to do this, but I cant pass a string and json object
            JSONObject msg = new JSONObject();
            JSONObject ret = new JSONObject();
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
                try {
                    ret.put("responseCode", response);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                String responseMessage = conn.getResponseMessage();
                System.out.println("The response message is: " + responseMessage);


                // Convert the InputStream into a string
                is = conn.getInputStream();
                //System.out.println(is.available());
                ret = readJSON(is, 5000);
                try {

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