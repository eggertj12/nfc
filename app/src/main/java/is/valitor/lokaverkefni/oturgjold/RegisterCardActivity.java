package is.valitor.lokaverkefni.oturgjold;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;


public class RegisterCardActivity extends Activity {
    public final static String MSG_CARDHOLDER = "is.valitor.oturgjold.MSG_CARDHOLDER";
    public final static String MSG_CARDTYPE = "is.valitor.oturgjold.MSG_CARDTYPE";
    public final static String MSG_CARDNUMBER = "is.valitor.oturgjold.MSG_CARDNUMBER";
    public final static String MSG_CARDCVV = "is.valitor.oturgjold.MSG_CARDCVV";
    public final static String MSG_CARDMONTH = "is.valitor.oturgjold.MSG_CARDMONTH";
    public final static String MSG_CARDYEAR = "is.valitor.oturgjold.MSG_CARDYEAR";
    public final static String MSG_CARDPIN = "is.valitor.otugjold.MSG_CARDPIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_card);

        // Populate spinner for selection of month
        Spinner spinnerMonth = (Spinner) findViewById(R.id.spinnerValidityMonth);

        ArrayList<String> arrayMonths = new ArrayList<>();
        for (int i = 1; i <= 12; i++)
        {
            arrayMonths.add(Integer.toString(i));
        }

        ArrayAdapter<String> adapterMonth = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayMonths);
        adapterMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerMonth.setAdapter(adapterMonth);

        // Populate spinner for selection of year
        Spinner spinnerYear = (Spinner) findViewById(R.id.spinnerValidityYear);

        ArrayList<String> arrayYear = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i <= 8; i++)
        {
            arrayYear.add(Integer.toString(i + currentYear));
        }

        ArrayAdapter<String> adapterYear = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayYear);
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerYear.setAdapter(adapterYear);
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

        // Cardholder name
        EditText editCardholder = (EditText) findViewById(R.id.editCardholderName);
        String cardholder = editCardholder.getText().toString();
        //if (!v.validateCardholderName(cardholder)) {
        if(false) {
            CharSequence message = getResources().getString(R.string.error_invalid_cardholder_name);
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
            toast.show();
            editCardholder.requestFocus();
            return;
        }

        // Card Number
        EditText editCardNumber = (EditText) findViewById(R.id.editCardNumber);
        String cardNumber = v.cleanupCardNumber(editCardNumber.getText().toString());
        String cardType = v.validateCardNumber(cardNumber);
        //if (!(cardType == "visa" ||cardType == "mastercard" )) {
        if(false){
            CharSequence message = getResources().getString(R.string.error_invalid_cardnumber);
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
            toast.show();
            editCardNumber.requestFocus();
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

        // PIN Number
        EditText editPin = (EditText) findViewById(R.id.editPIN);
        String cardPin = editPin.getText().toString();
       /* if (!v.validatePin(cardPin)) {
            CharSequence message = getResources().getString(R.string.error_invalid_pin);
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
            toast.show();
            editCardPin.requestFocus();
            return;
        }*/
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
        Intent intent = new Intent(this, FinalizeRegisterCardActivity.class);

        // Populate intent with data
        intent.putExtra(MSG_CARDNUMBER, cardNumber);
        intent.putExtra(MSG_CARDTYPE, cardType);
        intent.putExtra(MSG_CARDHOLDER, cardholder);
        intent.putExtra(MSG_CARDCVV, cardCvv);
        intent.putExtra(MSG_CARDMONTH, cardMonth);
        intent.putExtra(MSG_CARDYEAR, cardYear);
        intent.putExtra(MSG_CARDPIN,cardPin);

        startActivity(intent);
        finish();
    }

    /** Validator methods for the input */
}
