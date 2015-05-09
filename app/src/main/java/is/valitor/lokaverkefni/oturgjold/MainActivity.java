package is.valitor.lokaverkefni.oturgjold;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

import is.valitor.lokaverkefni.oturgjold.repository.Card;
import is.valitor.lokaverkefni.oturgjold.repository.User;
import is.valitor.lokaverkefni.oturgjold.repository.Repository;
import is.valitor.lokaverkefni.oturgjold.service.AsyncTaskCompleteListener;
import is.valitor.lokaverkefni.oturgjold.service.GetBalanceTask;

import is.valitor.lokaverkefni.oturgjold.utils.NetworkUtil;


public class MainActivity extends FragmentActivity {
    public static final int RESULT_ADD_CARD = 0;

    private static final int REQUEST_REGISTER_USER = 1;
    private static final int REQUEST_REGISTER_CARD = 2;
    private static final int REQUEST_PAYMENT = 3;
    private static final int REQUEST_SHOW_TRANSACTIONS = 4;
    private static final int REQUEST_CHANGE_SELECTED_CARD = 5;

    SharedPreferences sharedPreferences;
    public static final String prefsFile = "oturgjoldPrefs";

    CardPagerAdapter pagerAdapter;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // To prevent flash of action bar for initial screen
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();

        setContentView(R.layout.activity_main);

        // In case we hit back, or system constrains us into recreating with HCE intent
        if(savedInstanceState != null) {
            Intent newIntent = new Intent();
            setIntent(newIntent);
        }

        // In case this is being called from HCE. getBooleanExtra is just funky this way
        if(getIntent().getStringExtra("MSG_REQUEST_PIN") != null) {
            // Extra layer of insulation:
            if(getIntent().getStringExtra("MSG_REQUEST_PIN").contentEquals("true")) {
                // Clear last entered pin shared preference
                System.out.println("MAIN ACTIVITY BEING CALLED AFTER HCE");
                SharedPreferences.Editor clearPin = PreferenceManager.getDefaultSharedPreferences(this).edit();
                clearPin.putString("lastPIN", "");
                clearPin.commit();
                // Go straight to pin
                Intent intent = new Intent(this, PaymentActivity.class);
                startActivityForResult(intent, REQUEST_PAYMENT);
                // Rest is handled in the listener downstairs
            }
        }

        enableRegistrationUI();

        NetworkUtil.enableNetworkMonitoring(getApplication());

        pagerAdapter = new CardPagerAdapter(getSupportFragmentManager(), getApplication());
        viewPager = (ViewPager) findViewById(R.id.cardPager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(pagerAdapter);
        if (Repository.getCardCount(getApplication()) > 0) {
            viewPager.setCurrentItem(Repository.getSelectedCardIndex(getApplication()));
        }

        Typeface icoMoon = Typeface.createFromAsset(getAssets(), "fonts/icomoon.ttf");
        ((TextView) findViewById(R.id.main_register_account)).setTypeface(icoMoon);
        ((TextView) findViewById(R.id.button_payment)).setTypeface(icoMoon);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Draws menu each time menu button is pressed
     * All items set invisible by default, only set to true
     * when certain requirements are met
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu (Menu menu)
    {
        boolean isRegistered = false;
        boolean hasCard = false;
        User user = Repository.getUser(getApplication());
        int cardCount = Repository.getCardCount(getApplication());
        if(user != null) isRegistered = true;
        if(cardCount != 0) hasCard = true;

        MenuItem menuItemRegCard = menu.findItem(R.id.register_card).setVisible(false);
        MenuItem menuItemGetBalance = menu.findItem(R.id.action_getBalance).setVisible(false);
        MenuItem menuItemCardTransactions = menu.findItem(R.id.action_getTransactions).setVisible(false);
        MenuItem menuItemCardChangeSelectedCard = menu.findItem(R.id.action_change_selected_card).setVisible(false);

        if(isRegistered) menuItemRegCard.setVisible(true);
        if(isRegistered && hasCard) menuItemGetBalance.setVisible(true);
        if(isRegistered && hasCard)menuItemCardTransactions.setVisible(true);
        if(isRegistered && hasCard)menuItemCardChangeSelectedCard.setVisible(true);

       return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        final Context ctx = getApplicationContext();

        //Register new card
        if (id == R.id.register_card) {
            registerCard(item.getActionView());
            return true;
        }

        else if(id == R.id.action_getBalance) {
            getCurrentCardBalance();
        }

        else if(id == R.id.action_change_selected_card)
        {
            changeSelectedCard(item.getActionView());
        }
        else if(id==R.id.action_getTransactions)
        {
            getTransactions(item.getActionView());
        }

        return super.onOptionsItemSelected(item);
    }

    /** Called when the user clicks the Register card button */
    public void registerCard(View view) {
        // Create intent for opening new activity
        Intent intent = new Intent(this, RegisterCardActivity.class);
        //startActivity(intent);
        startActivityForResult(intent, REQUEST_REGISTER_CARD);
    }

    /**
     * Called when user clicks changeSelectedCard button in settings
     * Stars a new activity to change the selected card
     * @param view
     */
    public void changeSelectedCard(View view)
    {
        Intent intent = new Intent(this,ChangeSelectedCardActivity.class);
        startActivityForResult(intent,REQUEST_CHANGE_SELECTED_CARD);
    }
    /**
     * Called when user clicks showtransaction button in settings
     * Stars a new activity to show the transactions for selected card
     * @param view
     */
    public void getTransactions(View view)
    {
        Intent intent = new Intent(this,ShowTransactionsActivity.class);

        startActivityForResult(intent, REQUEST_SHOW_TRANSACTIONS);
    }

    /** Called when the user clicks the register account button */
    public void registerAccount(View view) {
        Intent intent = new Intent(this, RegisterAccountActivity.class);

        // Start activity for result to be able to trigger UI change on success
        startActivityForResult(intent, REQUEST_REGISTER_USER);
    }

    /** Called when the user clicks the Register card button */
    public void payment(View view) {
        // Create intent for opening new activity
        Intent intent = new Intent(this, PaymentActivity.class);
        //startActivity(intent);
        startActivityForResult(intent, REQUEST_PAYMENT);
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent intent) {
        super.onActivityResult(reqCode, resCode, intent);

        if (reqCode == REQUEST_REGISTER_USER) {
            if(resCode == RESULT_OK) {
                Intent newIntent = new Intent(this, RegisterCardActivity.class);
                startActivityForResult(newIntent, REQUEST_REGISTER_CARD);
            }
        }
        if(reqCode == REQUEST_REGISTER_CARD) {
            enableRegistrationUI();
        }

        if (reqCode == REQUEST_REGISTER_CARD) {
            pagerAdapter.notifyDataSetChanged();
            viewPager.setCurrentItem(pagerAdapter.getCount() - 1);
            if(resCode == RESULT_ADD_CARD) {
                registerCard(null);
            }
        }

        if (reqCode == REQUEST_CHANGE_SELECTED_CARD) {
            new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (Repository.getCardCount(getApplication()) > 0) {
                            viewPager.setCurrentItem(Repository.getSelectedCardIndex(getApplication()));
                        }
                    }
                },
                300
            );
        }

        if(reqCode == REQUEST_PAYMENT) {
            if(resCode == RESULT_OK) {
                String pin = intent.getStringExtra("PIN");
                // This might not be most intelligent manner to pass data
                SharedPreferences.Editor clearPin = PreferenceManager.getDefaultSharedPreferences(this).edit();
                clearPin.putString("lastPIN", pin);
                clearPin.commit();
            }
            else if(resCode == RESULT_CANCELED) {

            }
        }
    }

    private void enableRegistrationUI() {
        //Force the menu to reload
        invalidateOptionsMenu();
        // Check if there is a user and disable buttons if not
        User user = Repository.getUser(getApplication());
        ArrayList<Card> cards = Repository.getCards(getApplication());


        Button registerUserButton = (Button) findViewById(R.id.main_register_account);
        Button paymentButton = (Button) findViewById(R.id.button_payment);

        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainLinearLayout);
        View registerLayout = findViewById(R.id.sublayout_register);
        View paymentLayout = findViewById(R.id.sublayout_payment);

        ViewPager vp = (ViewPager) findViewById(R.id.cardPager);
        ImageView startLogo = (ImageView) findViewById(R.id.startLogo);

//        paymentButton.setVisibility(View.INVISIBLE);
//        registerUserButton.setVisibility(View.VISIBLE);

        if (user == null) {
            getActionBar().hide();

            paymentLayout.setVisibility(View.INVISIBLE);
            registerLayout.setVisibility(View.VISIBLE);

            vp.setVisibility(View.INVISIBLE);
            startLogo.setVisibility(View.VISIBLE);
            mainLayout.setWeightSum(1);

        } else if(cards.size() == 0){
            getActionBar().show();

            // Should never happen, but just in case
            paymentLayout.setVisibility(View.INVISIBLE);
            registerLayout.setVisibility(View.INVISIBLE);

            vp.setVisibility(View.INVISIBLE);
            startLogo.setVisibility(View.VISIBLE);
            mainLayout.setWeightSum(1);
        }
        else {
            getActionBar().show();

            paymentLayout.setVisibility(View.VISIBLE);
            registerLayout.setVisibility(View.INVISIBLE);

            vp.setVisibility(View.VISIBLE);
            startLogo.setVisibility(View.INVISIBLE);
            mainLayout.setWeightSum(2);
        }
    }

    /*
    Fetch the balance of the currentCard
     */
    private void getCurrentCardBalance()
    {
        try {
           // Get current card fragment
            // This code relies on an unsupported trick to get the current fragment which is not supported by the API
            Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.cardPager + ":" + viewPager.getCurrentItem());
            // based on the current position you can then cast the page to the correct
            // class and call the method:
            if (page == null) {
                // No fragment found, have to abort
                return;
            }
            final TextView balance = (TextView) page.getView().findViewById(R.id.fragmentCardBalance);
            final AsyncTaskCompleteListener<Integer> listener = new AsyncTaskCompleteListener<Integer>() {
                @Override
                public void onTaskComplete(final Integer result) {
                    DecimalFormat fmt = new DecimalFormat("###,###.##");
                    balance.setText(fmt.format(result));
                }
            };
            // get currently selected card
            Card card = Repository.getSelectedCard(getApplication());
            int currentCard = card.getCard_id();
            balance.setText(getString(R.string.card_fragment_get_balance));
            new GetBalanceTask(listener).execute(getString(R.string.service_balance_url) + currentCard);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
