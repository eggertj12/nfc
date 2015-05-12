package is.valitor.lokaverkefni.oturgjold;

import android.app.Activity;
import android.provider.Settings;
//import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidParameterException;

import is.valitor.lokaverkefni.oturgjold.repository.Card;
import is.valitor.lokaverkefni.oturgjold.repository.Token;
import is.valitor.lokaverkefni.oturgjold.repository.User;
import is.valitor.lokaverkefni.oturgjold.service.AsyncTaskCompleteListener;
import is.valitor.lokaverkefni.oturgjold.service.AsyncTaskResult;
import is.valitor.lokaverkefni.oturgjold.service.GetTokenTask;
import is.valitor.lokaverkefni.oturgjold.service.RegisterCardTask;
import is.valitor.lokaverkefni.oturgjold.repository.Repository;
import is.valitor.lokaverkefni.oturgjold.service.RequestResult;
import is.valitor.lokaverkefni.oturgjold.utils.NetworkUtil;


public class FinalizeRegisterCardActivity extends Activity implements AsyncTaskCompleteListener<AsyncTaskResult<Card>>{

    private TextView responseDisplay;
    private TextView nextActionPrompt;
    private ProgressBar loadingThings;
    private Button defaultFinishButton;
    private Button continueRegisterCard;
    private TextView sendRequest;
    private static final int REQUEST_REGISTER_CARD = 2;

    private Card card = null;
    //private String cardName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalize_register_card);
        sendRequest = (TextView) findViewById(R.id.serviceRequest);
        responseDisplay = (TextView) findViewById(R.id.serviceResponse);
        nextActionPrompt = (TextView) findViewById(R.id.finalize_next_action);
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
        String nickName = intent.getStringExtra(CustomizeCardActivity.MSG_NICKNAME);

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
                new RegisterCardTask(this)
                        .execute(getString(R.string.service_card_url), outMsg.toString());
            }
            else {
                // display error
                Toast toast = Toast.makeText(this, R.string.error_no_network, Toast.LENGTH_LONG);
                toast.show();

                setResult(RESULT_CANCELED);
                finish();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void defaultFinish(View view){
       //setResult(MainActivity.RESULT_ADD_CARD);
       setResult(RESULT_OK);
        finish();
    }

    /** Called when the user clicks the reg_new_card button */
    public void registerAnotherCard(View view) {
         // Finish this activity signalling request to add another
        //setResult(MainActivity.RESULT_OK);
        setResult(MainActivity.RESULT_ADD_CARD);
        finish();
    }

    // onTaskComplete is called once the task has completed.
    @Override
    public void onTaskComplete(AsyncTaskResult<Card> result) {

        sendRequest.setVisibility(View.INVISIBLE);
        loadingThings.setVisibility(View.INVISIBLE);
        defaultFinishButton.setVisibility(View.VISIBLE);
        continueRegisterCard.setVisibility(View.VISIBLE);

        //Registration was succsessful
        if(result.getError() == null)
        {
            responseDisplay.setVisibility(View.VISIBLE);
            nextActionPrompt.setVisibility(View.VISIBLE);
            try{

                Card c = result.getResult();

                c.setCard_name(getIntent().getStringExtra(CustomizeCardActivity.MSG_NICKNAME));
                c.setCard_image(getIntent().getStringExtra(CustomizeCardActivity.MSG_CARDIMAGE));
                String cardNumber = getIntent().getStringExtra(CustomizeCardActivity.MSG_CARDNUMBER);
                c.setLast_four(cardNumber.substring(cardNumber.length() -4));
                Repository.addCard(this,c);

                //Getting ready to request for tokens for this new card
                User theUser = Repository.getUser(getApplicationContext());

                Token token = new Token();
                token.setUsr_id(theUser.getUsr_id());
                token.setDevice_id(theUser.getDevice_id());
                token.setCard_id(c.getCard_id());

                Gson gson = new Gson();
                String tokenJson = gson.toJson(token, Token.class);

                // The get token task will fetch 3 tokens
                new GetTokenTask(getApplicationContext()).execute(getString(R.string.service_token_url), tokenJson);

            } catch (Exception e){
                e.printStackTrace();
            }

        } else if (result.getError() instanceof IOException) {
            Toast.makeText(this, getString(R.string.error_general_network), Toast.LENGTH_LONG).show();
            // Return to previous activity on network error
            setResult(MainActivity.RESULT_NETWORK_ERROR);
            finish();
        } else  if (result.getError() instanceof InvalidParameterException) {
            Toast.makeText(this, result.getError().getMessage(), Toast.LENGTH_LONG).show();
            // Close current activity and return to previous on invalid input error
            finish();
        }


    }
}