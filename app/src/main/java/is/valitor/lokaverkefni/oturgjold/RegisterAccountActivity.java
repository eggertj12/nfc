package is.valitor.lokaverkefni.oturgjold;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.StringReader;
import com.google.gson.stream.JsonReader;

import is.valitor.lokaverkefni.oturgjold.repository.Repository;
import is.valitor.lokaverkefni.oturgjold.repository.User;
import is.valitor.lokaverkefni.oturgjold.service.AsyncTaskCompleteListener;
import is.valitor.lokaverkefni.oturgjold.service.RegisterAccountTask;
import is.valitor.lokaverkefni.oturgjold.utils.DigitGrouper;
import is.valitor.lokaverkefni.oturgjold.utils.NetworkUtil;
import is.valitor.lokaverkefni.oturgjold.service.RegisterResult;
import is.valitor.lokaverkefni.oturgjold.utils.Validator;


public class RegisterAccountActivity extends Activity implements AsyncTaskCompleteListener<RegisterResult>{

    private TextView editAccountName;
    private ProgressBar loadingThings;
    private RegisterResult requestResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);
        editAccountName = (TextView) findViewById(R.id.editAccountName);
        loadingThings = (ProgressBar) findViewById(R.id.progressBar);
        loadingThings.setVisibility(View.INVISIBLE);

        // Group first 6 numbers of SSN
        TextView editSsn = (TextView) findViewById(R.id.editAccountSSN);
        editSsn.addTextChangedListener(new DigitGrouper(6));
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
        String ssn = editAccountSSN.getText().toString().replace(String.valueOf(DigitGrouper.space), "");

        // Proper validation might be in order
        if (!((ssn.length() != 10 && !(ssn.endsWith("9"))) || !ssn.endsWith("0"))) {
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
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (NetworkUtil.isConnected(getApplication())) {

            // Update UI
            loadingThings.setVisibility(View.VISIBLE);
            Button registerAccountButton = (Button) findViewById(R.id.button_register_account);
            registerAccountButton.setClickable(false);
            registerAccountButton.setEnabled(false);

            // fetch data
            new RegisterAccountTask(getApplicationContext(),this)
                    .execute(getString(R.string.service_account_url), jsonAccountObject.toString());
        }
    }


        // onTaskComplete is called once the task has completed.

    @Override
    public void onTaskComplete(RegisterResult result) {

        if(result.getResultCode() == 200)
        {
            //Toast.makeText(this, result.getResultMessage(),Toast.LENGTH_LONG).show();
                try {
                    Gson gson = new Gson();
                    JsonReader rd = new JsonReader(new StringReader(result.getResultContent()));
                    rd.setLenient(true);
                    User user = gson.fromJson(rd, User.class);
                    Repository.setUser(getApplication(), user);

                    //go back to frontpage
                    // Set result to trigger action in mainActivity
                    setResult(RESULT_OK);
                    finish();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
            }
        else {
            Toast.makeText(this,result.getResultContent(),Toast.LENGTH_LONG).show();
            loadingThings.setVisibility(View.INVISIBLE);
            Button registerAccountButton = (Button) findViewById(R.id.button_register_account);
            registerAccountButton.setClickable(true);
            registerAccountButton.setEnabled(true);
            editAccountName.requestFocus();
        }
     }
}






