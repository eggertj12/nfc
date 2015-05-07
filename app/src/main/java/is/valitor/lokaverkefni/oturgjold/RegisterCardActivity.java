package is.valitor.lokaverkefni.oturgjold;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import is.valitor.lokaverkefni.oturgjold.repository.Repository;
import is.valitor.lokaverkefni.oturgjold.repository.User;
import is.valitor.lokaverkefni.oturgjold.utils.FourDigitGrouper;
import is.valitor.lokaverkefni.oturgjold.utils.FourDigitUngrouper;
import is.valitor.lokaverkefni.oturgjold.utils.Validator;


public class RegisterCardActivity extends Activity {
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

    // To handle the rogue first selection event
    private boolean firstSpM;
    private boolean firstSpY;
    private boolean firstCardN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_card);

        User user = Repository.getUser(getApplication());
        cardholder = user.getName().toString();
        final TextView name = (TextView)findViewById(R.id.cardholderName);
        name.setText(cardholder);
        // Populate spinner for selection of month
        final Spinner spinnerMonth = (Spinner) findViewById(R.id.spinnerValidityMonth);
        spM = spinnerMonth;

        ArrayList<String> arrayMonths = new ArrayList<>();
        for (int i = 1; i <= 12; i++)
        {
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
        for (int i = 0; i <= 8; i++)
        {
            arrayYear.add(Integer.toString(i + currentYear));
        }

        ArrayAdapter<String> adapterYear = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayYear);
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerYear.setAdapter(adapterYear);


        cardNumber = (EditText) findViewById(R.id.editCardNumber);
        editCvv = (EditText) findViewById(R.id.editCardCvv);

        // Keep credit card number format to groups of four digits
        cardNumber.addTextChangedListener(new FourDigitGrouper());
    }

    @Override
    public void onStart() {
        super.onStart();
        // These booleans needed to ignore first selection
        firstCardN = true;
        firstSpM = true;
        firstSpY = true;

        // Set listeners to ensure smooth flow through registration
        cardNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_NEXT) {
                    cardNumber.clearFocus();
                    spM.requestFocus();
                    spM.performClick();
                }
                return false;
            }
        });

        spM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(firstSpM == true) {
                    firstSpM = false;
                    return;
                }

                spM.clearFocus();
                spY.requestFocus();
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
                if(firstSpY == true) {
                    firstSpY = false;
                    return;
                }

                spY.clearFocus();
                editCvv.requestFocus();
                editCvv.performClick();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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

    /** Called when the user clicks the Next button */
    public void createCardNext(View view) {
        // Start by validation
        Validator v = new Validator();
        // get currently selected card


        cardNumber = (EditText) findViewById(R.id.editCardNumber);
        String cn = FourDigitUngrouper.RemoveSpaces(cardNumber.getText().toString());
        System.out.println(cardNumber);

        String cardType = "";
        //if (!(cardType == "visa" ||cardType == "mastercard" )) {
        if(false){
            CharSequence message = getResources().getString(R.string.error_invalid_cardnumber);
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        // Cvv Number
        EditText editCardCvv = (EditText) findViewById(R.id.editCardCvv);
        String cardCvv = editCardCvv.getText().toString();
        //if (!v.validateCvv(cardCvv)) {
        if(false) {
            CharSequence message = getResources().getString(R.string.error_invalid_cvv);
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
            toast.show();
            editCardCvv.requestFocus();
            return;
        }

        // Validity year
        Spinner spinnerYear = (Spinner) findViewById(R.id.spinnerValidityYear);
        String cardYear = spinnerYear.getSelectedItem().toString();
        //if (!v.validateYear(cardYear)) {
        if(false) {
            CharSequence message = getResources().getString(R.string.error_invalid_validity_year);
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
            toast.show();
            spinnerYear.requestFocus();
            return;
        }

        // Validity month
        Spinner spinnerMonth = (Spinner) findViewById(R.id.spinnerValidityMonth);
        String cardMonth = spinnerMonth.getSelectedItem().toString();
        //if (!v.validateMonth(cardMonth)) {
        if(false) {
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
            if(resCode == RESULT_OK) {
                finish();
            }
        }
    }

}
