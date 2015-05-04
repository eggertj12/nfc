package is.valitor.lokaverkefni.oturgjold;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class CustomizeCardActivity extends Activity {

    private static final int REQUEST_REGISTER_CARD = 1;
    public static final String MSG_CARDHOLDER = "is.valitor.oturgjold.MSG_CARDHOLDER";
    public static final String MSG_CARDTYPE = "is.valitor.oturgjold.MSG_CARDTYPE";
    public static final String MSG_CARDNUMBER = "is.valitor.oturgjold.MSG_CARDNUMBER";
    public static final String MSG_CARDCVV = "is.valitor.oturgjold.MSG_CARDCVV";
    public static final String MSG_CARDMONTH = "is.valitor.oturgjold.MSG_CARDMONTH";
    public static final String MSG_CARDYEAR = "is.valitor.oturgjold.MSG_CARDYEAR";
    public static final String MSG_CARDPIN ="is.valitor.oturghold.MSG_CARDPIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_card);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_card_customization, menu);
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

    public String getPin() {

        EditText pin = (EditText) findViewById(R.id.customize_pin_inputfield);

        return pin.getText().toString();
    }

    public void finalizeCard(View view) {

        Intent oldIntent = getIntent();
        Intent intent = new Intent(this, FinalizeRegisterCardActivity.class);

        intent.putExtra(MSG_CARDNUMBER, oldIntent.getStringExtra(RegisterCardActivity.MSG_CARDNUMBER));
        intent.putExtra(MSG_CARDTYPE, oldIntent.getStringExtra(RegisterCardActivity.MSG_CARDTYPE));
        intent.putExtra(MSG_CARDHOLDER, oldIntent.getStringExtra(RegisterCardActivity.MSG_CARDHOLDER));
        intent.putExtra(MSG_CARDCVV, oldIntent.getStringExtra(RegisterCardActivity.MSG_CARDCVV));
        intent.putExtra(MSG_CARDMONTH, oldIntent.getStringExtra(RegisterCardActivity.MSG_CARDMONTH));
        intent.putExtra(MSG_CARDYEAR, oldIntent.getStringExtra(RegisterCardActivity.MSG_CARDYEAR));
        intent.putExtra(MSG_CARDPIN, getPin());

        startActivityForResult(intent, REQUEST_REGISTER_CARD);
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent intent) {
        super.onActivityResult(reqCode, resCode, intent);

        if (reqCode == REQUEST_REGISTER_CARD) {
            if(resCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    public void defaultFinish(View view){
        setResult(RESULT_OK);
        finish();
    }
}
