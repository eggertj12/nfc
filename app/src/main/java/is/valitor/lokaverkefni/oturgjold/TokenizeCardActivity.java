package is.valitor.lokaverkefni.oturgjold;

import android.app.Activity;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
//import android.app.FragmentTransaction;
import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;





public class TokenizeCardActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tokenize_card);

        Intent intent = getIntent();

        String cardNumber = intent.getStringExtra(RegisterCardActivity.MSG_CARDNUMBER);
        String cardHolder = intent.getStringExtra(RegisterCardActivity.MSG_CARDHOLDER);
        String cardType = intent.getStringExtra(RegisterCardActivity.MSG_CARDTYPE);
        String cardCvv = intent.getStringExtra(RegisterCardActivity.MSG_CARDCVV);
        String cardMonth = intent.getStringExtra(RegisterCardActivity.MSG_CARDMONTH);
        String cardYear = intent.getStringExtra(RegisterCardActivity.MSG_CARDYEAR);

        LinearLayout root = (LinearLayout) findViewById(R.id.tokenize_linear);

        TextView vHolder = new TextView(this);
        vHolder.setText("cardholder: " + cardHolder);
        root.addView (vHolder);

        TextView vType = new TextView(this);
        vType.setText("card type: " + cardType);
        root.addView (vType);

        TextView vNumber = new TextView(this);
        vNumber.setText("card number: " + cardNumber);
        root.addView (vNumber);

        TextView vValid = new TextView(this);
        vValid.setText("valid: " + cardMonth + "/" + cardYear);
        root.addView (vValid);

        AccountStorage.SetAccount(this, cardNumber);

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
}
