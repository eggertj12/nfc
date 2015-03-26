package is.valitor.lokaverkefni.oturgjold;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;


public class Payment extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_payment, menu);
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

    //not doing anything at the moment
    public void createString()
    {
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
    }
}
