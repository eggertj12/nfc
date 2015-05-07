package is.valitor.lokaverkefni.oturgjold;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
//import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;

import is.valitor.lokaverkefni.oturgjold.repository.Card;
import is.valitor.lokaverkefni.oturgjold.repository.Token;
import is.valitor.lokaverkefni.oturgjold.repository.User;
import is.valitor.lokaverkefni.oturgjold.service.AsyncTaskCompleteListener;
import is.valitor.lokaverkefni.oturgjold.service.AsyncTaskResult;
import is.valitor.lokaverkefni.oturgjold.service.GetTokenTask;
import is.valitor.lokaverkefni.oturgjold.service.RegisterCardTask;
import is.valitor.lokaverkefni.oturgjold.repository.Repository;
import is.valitor.lokaverkefni.oturgjold.utils.NetworkUtil;


public class FinalizeRegisterCardActivity extends Activity {

    private TextView responseDisplay;
    private ProgressBar loadingThings;
    private Button defaultFinishButton;
    private Button continueRegisterCard;
    private static final int REQUEST_REGISTER_CARD = 2;

    private Card card = null;
    //private String cardName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalize_register_card);
        responseDisplay = (TextView) findViewById(R.id.serviceResponse);
        loadingThings = (ProgressBar) findViewById(R.id.tokCardProgressBar);

        Intent intent = getIntent();
        // Lets get get the card data, temporary storage only ofc
        String cardNumber = intent.getStringExtra(CustomizeCardActivity.MSG_CARDNUMBER);
        String cardHolder = intent.getStringExtra(CustomizeCardActivity.MSG_CARDHOLDER);
        String cardType = intent.getStringExtra(CustomizeCardActivity.MSG_CARDTYPE);
        String cardCvv = intent.getStringExtra(CustomizeCardActivity.MSG_CARDCVV);
        String cardMonth = intent.getStringExtra(CustomizeCardActivity.MSG_CARDMONTH);
        String cardYear = intent.getStringExtra(CustomizeCardActivity.MSG_CARDYEAR);
        String cardPin = intent.getStringExtra(CustomizeCardActivity.MSG_CARDPIN);

        LinearLayout root = (LinearLayout) findViewById(R.id.finalize_register_linear);

        System.out.println(cardNumber);

        // Having registered a card, you are now happy to return to main view
        defaultFinishButton = (Button) findViewById(R.id.button_finish_default_card);
        defaultFinishButton.setVisibility(View.INVISIBLE);

        //Register new card (another card)
        continueRegisterCard = (Button)findViewById(R.id.reg_new_card);
        continueRegisterCard.setVisibility(View.INVISIBLE);

        // Communicate with the service:
        try {
            // Make JSON
            JSONObject outMsg = new JSONObject();
            outMsg.put("usr_id", Repository.getUser(this).getUsr_id());
            outMsg.put("cardnumber", cardNumber);
            outMsg.put("cardholder", cardHolder);
            outMsg.put("validity", cardMonth + "/" + cardYear);
            outMsg.put("cvv", cardCvv);
            outMsg.put("pin", cardPin);
            String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            outMsg.put("device_id", android_id);

            // Ensure connection
            if (NetworkUtil.isConnected(getApplication())) {
                // Communicate with service
                new RegisterCardTask(this, new RegisterCardListener())
                        .execute(getString(R.string.service_card_url), outMsg.toString());
            }
            else {
                // display error
                CharSequence message = "Nettenging ekki virk.";
                Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
                toast.show();

                setResult(RESULT_CANCELED);
                finish();
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

    public void defaultFinish(View view){
        setResult(RESULT_OK);
        finish();
    }

    /** Called when the user clicks the reg_new_card button */
    public void registerCard(View view) {
        // Create intent for opening new activity
         Intent intent = new Intent(this, RegisterCardActivity.class);
         startActivityForResult(intent, REQUEST_REGISTER_CARD);
    }

    private class RegisterCardListener implements AsyncTaskCompleteListener<AsyncTaskResult<JSONObject>> {

        // onTaskComplete is called once the task has completed.
        @Override
        public void onTaskComplete(AsyncTaskResult<JSONObject> result)
        {
            if (result.getError() != null) {
                // display error
                Exception error = result.getError();
                CharSequence message;
                if (error instanceof IOException) {
                    error.printStackTrace();
                    message = getString(R.string.error_general_network);

                } else {
                    message = getString(R.string.error_general);
                }
                Toast toast = Toast.makeText(FinalizeRegisterCardActivity.this, message, Toast.LENGTH_LONG);
                toast.show();

                return;
            }

            JSONObject response = result.getResult();

            int rCode = response.optInt("responseCode");
            loadingThings.setVisibility(View.INVISIBLE);
            defaultFinishButton.setVisibility(View.VISIBLE);
            continueRegisterCard.setVisibility(View.VISIBLE);

            if(rCode == 200) {
                try {
                    responseDisplay.setText("Kort hefur verið skráð.");

                    Gson gson = new Gson();
                    card = gson.fromJson(response.toString(), Card.class);
                    String cardNumber = response.getString("cardnumber");
                    card.setLast_four(cardNumber.substring(cardNumber.length() - 4));
                    card.setTokenized_card_number(cardNumber);
                    card.setTokenized_cvv(response.getString("cvv"));
                    card.setTokenized_validation(response.getString("validity"));
                    card.setCard_name(getIntent().getStringExtra("Nickname"));
                    card.setCard_image(getIntent().getStringExtra(CustomizeCardActivity.MSG_CARDIMAGE));

                    Repository.addCard(getApplicationContext(), card);

                    User theUser = Repository.getUser(getApplicationContext());

                    Token token = new Token();
                    token.setUsr_id(theUser.getUsr_id());
                    token.setDevice_id(theUser.getDevice_id());
                    token.setCard_id(card.getCard_id());

                    String tokenJson = gson.toJson(token, Token.class);
                    new GetTokenTask(getApplicationContext()).execute(getString(R.string.service_token_url), tokenJson);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                System.out.println(rCode);
            }

        }

    }

}