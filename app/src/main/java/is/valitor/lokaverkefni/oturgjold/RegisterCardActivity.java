package is.valitor.lokaverkefni.oturgjold;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import is.valitor.lokaverkefni.oturgjold.repository.Repository;
import is.valitor.lokaverkefni.oturgjold.repository.User;
import is.valitor.lokaverkefni.oturgjold.utils.DigitGrouper;
import is.valitor.lokaverkefni.oturgjold.utils.Validator;

/**
 * View class for registering a card
 */
public class RegisterCardActivity extends Activity {
    // Id constants for intent
    public final static String MSG_CARDHOLDER = "is.valitor.oturgjold.MSG_CARDHOLDER";
    public final static String MSG_CARDTYPE = "is.valitor.oturgjold.MSG_CARDTYPE";
    public final static String MSG_CARDNUMBER = "is.valitor.oturgjold.MSG_CARDNUMBER";
    public final static String MSG_CARDCVV = "is.valitor.oturgjold.MSG_CARDCVV";
    public final static String MSG_CARDMONTH = "is.valitor.oturgjold.MSG_CARDMONTH";
    public final static String MSG_CARDYEAR = "is.valitor.oturgjold.MSG_CARDYEAR";

    private static final int REQUEST_REGISTER_CARD = 1;

    private String cardholder;
    private EditText cardNumber;
    private EditText editCvv;
    private Spinner spM;
    private Spinner spY;
    private Button nb;

    // To handle the rogue first selection event
    private boolean firstSpM;
    private boolean firstSpY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_card);

        // Load required data
        User user = Repository.getUser(getApplication());
        cardholder = user.getName();
        final TextView name = (TextView) findViewById(R.id.cardholderName);
        name.setText(cardholder);

        // Populate spinner for selection of month
        final Spinner spinnerMonth = (Spinner) findViewById(R.id.spinnerValidityMonth);
        spM = spinnerMonth;
        ArrayList<String> arrayMonths = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            arrayMonths.add(Integer.toString(i));
        }

        ArrayAdapter<String> adapterMonth = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayMonths);
        adapterMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapterMonth);

        // Populate spinner for selection of year
        final Spinner spinnerYear = (Spinner) findViewById(R.id.spinnerValidityYear);
        spY = spinnerYear;
        ArrayList<String> arrayYear = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i <= 8; i++) {
            arrayYear.add(Integer.toString(i + currentYear));
        }

        ArrayAdapter<String> adapterYear = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayYear);
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(adapterYear);

        // UI view handles
        cardNumber = (EditText) findViewById(R.id.editCardNumber);
        editCvv = (EditText) findViewById(R.id.editCardCvv);
        nb = (Button) findViewById(R.id.button_register_card_next);

        // Keep credit card number formatted to groups of four digits
        cardNumber.addTextChangedListener(new DigitGrouper(4));
    }

    @Override
    public void onStart() {
        super.onStart();
        // These booleans needed to ignore first selection, an android kink
        firstSpM = true;
        firstSpY = true;

        // Set listeners to ensure smooth flow through registration
        cardNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    spM.performClick();
                    editCvv.setCursorVisible(false);
                }
                return false;
            }
        });

        spM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (firstSpM) {
                    firstSpM = false;
                    return;
                }
                spY.performClick();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nothing
            }
        });

        spY.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (firstSpY) {
                    firstSpY = false;
                    return;
                }
                editCvv.requestFocus();
                editCvv.performClick();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        editCvv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCvv.setCursorVisible(true);
            }
        });

        editCvv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    nb.requestFocus();
                }
                return false;
            }
        });
    }


    /**
     * Called when the user clicks the Next button
     */
    public void createCardNext(View view) {
        // Start by validation
        Validator v = new Validator();

        cardNumber = (EditText) findViewById(R.id.editCardNumber);

        // Strip grouping separator
        String cn = cardNumber.getText().toString().replace(String.valueOf(DigitGrouper.space), "");

        String cardType = "";
        // String cardType = v.validateCardNumber(cn);
        // if (!(cardType == "visa" || cardType == "mastercard" )) {
        if (cn.length() != 16) {
            CharSequence message = getResources().getString(R.string.error_invalid_cardnumber);
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        // Cvv Number
        EditText editCardCvv = (EditText) findViewById(R.id.editCardCvv);
        String cardCvv = editCardCvv.getText().toString();
        if (!v.validateCvv(cardCvv)) {
            CharSequence message = getResources().getString(R.string.error_invalid_cvv);
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
            toast.show();
            editCardCvv.requestFocus();
            return;
        }

        // Validity year
        Spinner spinnerYear = (Spinner) findViewById(R.id.spinnerValidityYear);
        String cardYear = spinnerYear.getSelectedItem().toString();
        if (!v.validateYear(cardYear)) {
            CharSequence message = getResources().getString(R.string.error_invalid_validity_year);
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
            toast.show();
            spinnerYear.requestFocus();
            return;
        }

        // Validity month
        Spinner spinnerMonth = (Spinner) findViewById(R.id.spinnerValidityMonth);
        String cardMonth = spinnerMonth.getSelectedItem().toString();
        if (!v.validateMonth(cardMonth)) {
            CharSequence message = getResources().getString(R.string.error_invalid_validity_month);
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
            toast.show();
            spinnerMonth.requestFocus();
            return;
        }

        // Create intent for opening new activity
        Intent intent = new Intent(this, CustomizeCardActivity.class);

        // Populate intent with data
        intent.putExtra(MSG_CARDNUMBER, cn);
        intent.putExtra(MSG_CARDTYPE, cardType);
        intent.putExtra(MSG_CARDHOLDER, cardholder);
        intent.putExtra(MSG_CARDCVV, cardCvv);
        intent.putExtra(MSG_CARDMONTH, cardMonth);
        intent.putExtra(MSG_CARDYEAR, cardYear);

        startActivityForResult(intent, REQUEST_REGISTER_CARD);
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent intent) {
        super.onActivityResult(reqCode, resCode, intent);

        if (reqCode == REQUEST_REGISTER_CARD) {

            // If returning as a result of back do nothing
            if (resCode != Activity.RESULT_CANCELED) {
                // Otherwise pass the result code down the stack
                setResult(resCode);
                finish();
            }
        }
    }
}
