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

import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidParameterException;

import is.valitor.lokaverkefni.oturgjold.repository.Repository;
import is.valitor.lokaverkefni.oturgjold.repository.User;
import is.valitor.lokaverkefni.oturgjold.service.AsyncTaskCompleteListener;
import is.valitor.lokaverkefni.oturgjold.service.AsyncTaskResult;
import is.valitor.lokaverkefni.oturgjold.service.RegisterAccountTask;
import is.valitor.lokaverkefni.oturgjold.service.RequestResult;
import is.valitor.lokaverkefni.oturgjold.utils.DigitGrouper;
import is.valitor.lokaverkefni.oturgjold.utils.NetworkUtil;
import is.valitor.lokaverkefni.oturgjold.utils.Validator;

/**
 * View for new account / user registration
 */
public class RegisterAccountActivity extends Activity implements AsyncTaskCompleteListener<AsyncTaskResult<User>> {

    private TextView editAccountName;
    private ProgressBar loadingThings;

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

    public void registerAccount(View view) {

        // Get the unique device ID. This might have some limitation but is the best available option
        String android_id = Settings.Secure
                .getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

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

        // User SSN
        EditText editAccountSSN = (EditText) findViewById(R.id.editAccountSSN);
        String ssn = editAccountSSN.getText().toString().replace(String.valueOf(DigitGrouper.space), "");

        if (!v.validateSsn(ssn)) {
            CharSequence message = getString(R.string.error_invalid_ssn);
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
            toast.show();
            editAccountSSN.requestFocus();
            return;
        }

        // Build service request object
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
            new RegisterAccountTask(this)
                    .execute(getString(R.string.service_account_url), jsonAccountObject.toString());
        }
    }


    /**
     * onTaskComplete is called once the task has completed.
     *
     * @param result The resulting object from the AsyncTask.
     */

    @Override
    public void onTaskComplete(AsyncTaskResult<User> result) {

        //Registration was succsessful
        if(result.getError() == null)
        {
            try {
                User user = result.getResult();
                Repository.setUser(getApplication(), user);

                // go back to frontpage
                // Set result to trigger action in mainActivity
                setResult(RESULT_OK);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        } else if (result.getError() instanceof IOException) {
            Toast.makeText(this, getString(R.string.error_general_network), Toast.LENGTH_LONG).show();
        } else  if (result.getError() instanceof InvalidParameterException) {
            Toast.makeText(this, result.getError().getMessage(), Toast.LENGTH_LONG).show();
        }

        // Stop spinner and update UI
        loadingThings.setVisibility(View.INVISIBLE);
        Button registerAccountButton = (Button) findViewById(R.id.button_register_account);
        registerAccountButton.setClickable(true);
        registerAccountButton.setEnabled(true);
        editAccountName.requestFocus();
    }
}






