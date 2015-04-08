package is.valitor.lokaverkefni.oturgjold;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import is.valitor.lokaverkefni.oturgjold.repository.Token;
import is.valitor.lokaverkefni.oturgjold.repository.User;
import is.valitor.lokaverkefni.oturgjold.repository.Repository;


public class MainActivity extends Activity {
    public static final int RESULT_FAILURE = 0;
    public static final int RESULT_SUCCESS = 1;

    private static final int REQUEST_REGISTER_USER = 1;
    private static final int REQUEST_REGISTER_CARD = 2;
    private static final int REQUEST_PAYMENT = 3;



    SharedPreferences sharedPreferences;
    public static final String prefsFile = "oturgjoldPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // In case this is being called from HCE. getBooleanExtra is just funky this way
        if(getIntent().getStringExtra("MSG_REQUEST_PIN") != null) {
            // Extra layer of insulation:
            if(getIntent().getStringExtra("MSG_REQUEST_PIN").contentEquals("true")) {
                // Clear last entered pin shared prefence
                System.out.println("MAIN ACTIVITY BEING CALLED AFTER HCE");
                SharedPreferences.Editor clearPin = PreferenceManager.getDefaultSharedPreferences(this).edit();
                clearPin.putString("lastPIN", "");
                clearPin.commit();
                // Go straight to pin
                Intent intent = new Intent(this, PaymentActivity.class);
                startActivityForResult(intent, REQUEST_PAYMENT);
                // Rest is handled in the listener downstairs

            }
        }

        // initialize shared preferences file, give default value default - improve when refactoring
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString("defaultCard", "main");
        editor.commit();

        enableRegistrationUI();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Register new card
        if (id == R.id.register_card) {
            registerCard(item.getActionView());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Called when the user clicks the Register card button */
    public void registerCard(View view) {
        // Create intent for opening new activity
        Intent intent = new Intent(this, RegisterCardActivity.class);
        //startActivity(intent);
        startActivityForResult(intent, REQUEST_REGISTER_CARD);
    }


    /** Called when the user clicks the register account button */
    public void registerAccount(View view) {
        Intent intent = new Intent(this, RegisterAccountActivity.class);

        // Start activity for result to be able to trigger UI change on success
        startActivityForResult(intent, REQUEST_REGISTER_USER);
    }


    /** Called when the user clicks the Register card button */
    public void payment(View view) {
        // Create intent for opening new activity
        Intent intent = new Intent(this, PaymentActivity.class);
        //startActivity(intent);
        startActivityForResult(intent, REQUEST_PAYMENT);
    }

    public void getDefaultName(View view) {
        Intent intent = new Intent(this, ManageCardActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent intent) {
        super.onActivityResult(reqCode, resCode, intent);

        if (reqCode == REQUEST_REGISTER_USER || reqCode == REQUEST_REGISTER_CARD) {
            enableRegistrationUI();
        }

        if(reqCode == REQUEST_PAYMENT) {
            if(resCode == RESULT_OK) {
                String pin = intent.getStringExtra("PIN");
                // This might not be most intelligent manner to pass data
                SharedPreferences.Editor clearPin = PreferenceManager.getDefaultSharedPreferences(this).edit();
                clearPin.putString("lastPIN", pin);
                clearPin.commit();
            }
            else if(resCode == RESULT_FAILURE) {

            }
        }
    }

    private void enableRegistrationUI() {
        // Check if there is a user and disable buttons if not
        User user = Repository.getUser(getApplication());
        Token token = Repository.getToken(getApplication());
        Button registerCardButton = (Button) findViewById(R.id.button_register_card);
        Button registerUserButton = (Button) findViewById(R.id.button_register_account);
        Button paymentButton = (Button) findViewById(R.id.button_payment);

        registerCardButton.setVisibility(View.INVISIBLE);
        paymentButton.setVisibility(View.INVISIBLE);
        registerUserButton.setVisibility(View.VISIBLE);

        if (user == null) {
            Log.d("jo", "user null");
            registerCardButton.setVisibility(View.INVISIBLE);
            paymentButton.setVisibility(View.INVISIBLE);

        } else if(user != null && token == null){
            registerCardButton.setVisibility(View.VISIBLE);
            paymentButton.setVisibility(View.INVISIBLE);
            registerUserButton.setVisibility(View.INVISIBLE);
        }
        else {
            Log.d("hello","user not null");
            paymentButton.setVisibility(View.VISIBLE);
            registerCardButton.setVisibility(View.INVISIBLE);
            registerUserButton.setVisibility(View.INVISIBLE);
        }
    }
}
