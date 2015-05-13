package is.valitor.lokaverkefni.oturgjold;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import is.valitor.lokaverkefni.oturgjold.repository.Repository;

/**
 * The second view in registering a card
 * User selectable options for card
 */
public class CustomizeCardActivity extends Activity {

    private static final int REQUEST_REGISTER_CARD = 1;
    public static final String MSG_CARDHOLDER = "is.valitor.oturgjold.MSG_CARDHOLDER";
    public static final String MSG_CARDTYPE = "is.valitor.oturgjold.MSG_CARDTYPE";
    public static final String MSG_CARDNUMBER = "is.valitor.oturgjold.MSG_CARDNUMBER";
    public static final String MSG_CARDCVV = "is.valitor.oturgjold.MSG_CARDCVV";
    public static final String MSG_CARDMONTH = "is.valitor.oturgjold.MSG_CARDMONTH";
    public static final String MSG_CARDYEAR = "is.valitor.oturgjold.MSG_CARDYEAR";
    public static final String MSG_CARDPIN = "is.valitor.oturgjold.MSG_CARDPIN";
    public static final String MSG_CARDIMAGE = "is.valitor.oturgjold.MSG_CARDIMAGE";
    public static final String MSG_NICKNAME = "is.valitor.oturgjold.MSG_CARDNICKNAME";

    private static final int thumbs[] = {
            R.id.ibAbsBrown,
            R.id.ibAbsBlue,
            R.id.ibAbsRed,
            R.id.ibRainCyan,
            R.id.ibRainBlue,
            R.id.ibRainRed
    };

    private String cardImage = "abs_brown_creditcard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_card);

        // Shade all but the first card image buttons
        ImageButton button;
        for (int i = 1; i <= 5; i++) {
            button = (ImageButton) findViewById(thumbs[i]);
            button.setColorFilter(R.color.grey05trans);
        }
        findViewById(R.id.customize_pin_inputfield).requestFocus();
    }

    /**
     * Read the user selected pin input
     *
     * @return string PIN
     */
    private String getCustomizePin() {
        EditText pin = (EditText) findViewById(R.id.customize_pin_inputfield);

        return pin.getText().toString();
    }

    /**
     * Read the card nick inputbox
     *
     * @return user selected nickname or default sequential nick if none given
     */
    private String getCustomizeNick() {
        EditText getNick = (EditText) findViewById(R.id.customize_nick_input);
        String nick = getNick.getText().toString();
        if (nick == null || nick.trim().equals("")) {
            int size = Repository.getCardCount(this);
            nick = "Kort " + String.valueOf(size + 1);
        }

        return nick;
    }

    /**
     * Handler for clicks on card look imagebuttons
     * Shades all the other buttons
     *
     * This really is a dirty workaround for something that a radiobutton should solve
     * @param v the view that the event originates from
     */
    public void onSelectCardImage(View v) {
        // This should be handled by a radioGroup but it has serious layout deficiencies
        // F.ex. no possibility for wrapping buttons in a group

        ImageButton button;

        for (int id : thumbs) {
            button = (ImageButton) findViewById(id);
            button.setColorFilter(R.color.grey05trans);
        }

        button = (ImageButton) v;
        button.clearColorFilter();

        switch (v.getId()) {
            case R.id.ibRainBlue:
                cardImage = "rain_blue_creditcard";
                break;
            case R.id.ibRainRed:
                cardImage = "rain_red_creditcard";
                break;
            case R.id.ibRainCyan:
                cardImage = "rain_cyan_creditcard";
                break;
            case R.id.ibAbsBlue:
                cardImage = "abs_blue_creditcard";
                break;
            case R.id.ibAbsRed:
                cardImage = "abs_red_creditcard";
                break;
            case R.id.ibAbsBrown:
                cardImage = "abs_brown_creditcard";
                break;
        }
    }

    /**
     * Verify valid pin input and trigger transaction to register card
     *
     * @param view
     */
    public void finalizeCard(View view) {

        // Need the intent sent to start this activty to get previous data
        Intent oldIntent = getIntent();

        // And create a new one to start
        Intent intent = new Intent(this, FinalizeRegisterCardActivity.class);

        // Validate app PIN
        if (getCustomizePin().length() != 4) {
            CharSequence message = getResources().getString(R.string.error_customize_pin_length);
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
            toast.show();
            findViewById(R.id.customize_pin_inputfield).requestFocus();
            return;
        }

        // Send data to finalize
        intent.putExtra(MSG_CARDNUMBER, oldIntent.getStringExtra(RegisterCardActivity.MSG_CARDNUMBER));
        intent.putExtra(MSG_CARDTYPE, oldIntent.getStringExtra(RegisterCardActivity.MSG_CARDTYPE));
        intent.putExtra(MSG_CARDHOLDER, oldIntent.getStringExtra(RegisterCardActivity.MSG_CARDHOLDER));
        intent.putExtra(MSG_CARDCVV, oldIntent.getStringExtra(RegisterCardActivity.MSG_CARDCVV));
        intent.putExtra(MSG_CARDMONTH, oldIntent.getStringExtra(RegisterCardActivity.MSG_CARDMONTH));
        intent.putExtra(MSG_CARDYEAR, oldIntent.getStringExtra(RegisterCardActivity.MSG_CARDYEAR));
        intent.putExtra(MSG_CARDIMAGE, cardImage);
        intent.putExtra(MSG_NICKNAME, getCustomizeNick());
        intent.putExtra(MSG_CARDPIN, getCustomizePin());

        startActivityForResult(intent, REQUEST_REGISTER_CARD);
    }

    /**
     * Handle returning from next activity on stack (FinalizeCard)
     *
     * @param reqCode request identifier
     * @param resCode activity result code
     * @param intent
     */
    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent intent) {
        super.onActivityResult(reqCode, resCode, intent);

        if (reqCode == REQUEST_REGISTER_CARD) {
            // If returning as a result of network error do nothing
            // That way the user has opportunity to try again
            if (resCode == MainActivity.RESULT_NETWORK_ERROR) {
                return;
            }
            // Otherwise close and pass the result code down the stack
            setResult(resCode);
            finish();
        }
    }
}
