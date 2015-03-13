package is.valitor.lokaverkefni.oturgjold;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import is.valitor.lokaverkefni.oturgjold.models.User;


public class MainActivity extends Activity {
    public static final int RESULT_FAILURE = 0;
    public static final int RESULT_SUCCESS = 1;

    private static final int REQUEST_REGISTER_USER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enableRegistration();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Called when the user clicks the Register card button */
    public void registerCard(View view) {
        // Create intent for opening new activity
        Intent intent = new Intent(this, RegisterCardActivity.class);
        startActivity(intent);
    }

    public void accessService(View view) {
        Intent intent = new Intent(this, TestServiceActivity.class);
        startActivity(intent);
    }

    /** Called when the user clicks the register account button */
    public void registerAccount(View view) {
        Intent intent = new Intent(this, RegisterAccountActivity.class);

        // Start activity for result to be able to trigger UI change on success
        startActivityForResult(intent, REQUEST_REGISTER_USER);
    }

    public void getToken(View view)
    {
        Intent intent = new Intent(this, TokenReceive.class);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent intent) {
        super.onActivityResult(reqCode, resCode, intent);

        System.out.println("onActivityResult");
        if (reqCode == REQUEST_REGISTER_USER) {
            System.out.println("Req reg usr");
            enableRegistration();
        }
    }

    private void enableRegistration() {
        // Check if there is a user and disable buttons if not
        User user = Repository.getUser(getApplication());
        if (user == null) {
            Button registerCardButton = (Button) findViewById(R.id.button_register_card);
            registerCardButton.setClickable(false);
            registerCardButton.setEnabled(false);
        } else {
            Button registerCardButton = (Button) findViewById(R.id.button_register_card);
            registerCardButton.setClickable(true);
            registerCardButton.setEnabled(true);
        }

    }
}
