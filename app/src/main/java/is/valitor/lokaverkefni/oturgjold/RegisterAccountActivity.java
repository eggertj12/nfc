package is.valitor.lokaverkefni.oturgjold;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;

import is.valitor.lokaverkefni.oturgjold.models.User;
import is.valitor.lokaverkefni.oturgjold.service.AsyncTaskCompleteListener;
import is.valitor.lokaverkefni.oturgjold.service.AsyncTaskResult;
import is.valitor.lokaverkefni.oturgjold.service.RegisterAccountTask;


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

        if (!v.validateCardholderName(name)) {
            CharSequence message = getString(R.string.error_invalid_cardholder_name);
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
            toast.show();
            editAccountName.requestFocus();
            return;
        }
        EditText editAccountSSN = (EditText) findViewById(R.id.editAccountSSN);
        String ssn = editAccountSSN.getText().toString();

        // Proper validation might be in order
        if (ssn.length() != 10 || !(ssn.endsWith("9") || ssn.endsWith("0"))) {
            CharSequence message = getString(R.string.error_invalid_ssn);
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
            toast.show();
            editAccountSSN.requestFocus();
            return;
        }


        JSONObject jsonAccountObject = new JSONObject();

        try {
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

            // Update UI
            loadingThings.setVisibility(View.VISIBLE);
            Button registerAccountButton = (Button) findViewById(R.id.button_register_account);
            registerAccountButton.setClickable(false);
            registerAccountButton.setEnabled(false);

            // fetch data
            new RegisterAccountTask(this, new RegisterAccountListener())
                    .execute(getString(R.string.service_account_url), jsonAccountObject.toString());
        } else {
            // display error
            CharSequence message = "No network connection available.";
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);

            toast.show();
        }

    }

    private class RegisterAccountListener implements AsyncTaskCompleteListener<AsyncTaskResult<JSONObject>> {

        // onTaskComplete is called once the task has completed.
        @Override
        public void onTaskComplete(AsyncTaskResult<JSONObject> result)
        {
            if (result.getError() != null) {
                // display error
                CharSequence message;
                if (result.getError() instanceof IOException) {
                    message = "Network error, try again.";
                    Button registerAccountButton = (Button) findViewById(R.id.button_register_account);
                    registerAccountButton.setClickable(true);
                    registerAccountButton.setEnabled(true);

                } else {
                    message = "Unknown error, contact service.";
                }
                Toast toast = Toast.makeText(RegisterAccountActivity.this, message, Toast.LENGTH_LONG);
                toast.show();

                return;
            }

            JSONObject response = result.getResult();
            //textView.setText(result);
            int rCode = response.optInt("responseCode");
            loadingThings.setVisibility(View.INVISIBLE);
            if(rCode == 200) {
                try {
                    Gson gson = new Gson();
                    User user = gson.fromJson(response.toString(), User.class);
                    Repository.setUser(getApplication(), user);

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

    }
}



