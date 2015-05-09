package is.valitor.lokaverkefni.oturgjold;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class PaymentActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private String pin = "";
    private TextView stars[] = new TextView[4];
    private Context context = this;

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Button button1 = (Button) findViewById(R.id.button_pin_1);
        Button button2 = (Button) findViewById(R.id.button_pin_2);
        Button button3 = (Button) findViewById(R.id.button_pin_3);
        Button button4 = (Button) findViewById(R.id.button_pin_4);
        Button button5 = (Button) findViewById(R.id.button_pin_5);
        Button button6 = (Button) findViewById(R.id.button_pin_6);
        Button button7 = (Button) findViewById(R.id.button_pin_7);
        Button button8 = (Button) findViewById(R.id.button_pin_8);
        Button button9 = (Button) findViewById(R.id.button_pin_9);
        Button button10 = (Button) findViewById(R.id.button_pin_0);
        Button button11 = (Button) findViewById(R.id.button_pin_back);
        Button cancel = (Button) findViewById(R.id.button_pin_cancel);

        stars[0] = (TextView) findViewById(R.id.pin_stars_1);
        stars[1] = (TextView) findViewById(R.id.pin_stars_2);
        stars[2] = (TextView) findViewById(R.id.pin_stars_3);
        stars[3] = (TextView) findViewById(R.id.pin_stars_4);

        Typeface icoMoon = Typeface.createFromAsset(getAssets(), "fonts/icomoon.ttf");
        button11.setTypeface(icoMoon);


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
        public void onClick(View v) {
                clickToPin(1);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickToPin(2);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickToPin(3);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickToPin(4);
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickToPin(5);
            }
        });
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickToPin(6);
            }
        });
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickToPin(7);
            }
        });
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickToPin(8);
            }
        });
        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickToPin(9);
            }
        });
        button10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickToPin(0);
            }
        });
        button11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickToBack();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        if(savedInstanceState != null) {
            pin = savedInstanceState.getString("enteredPin");
            for(int i = 0; i < savedInstanceState.getInt("pinLength"); i++) {
                clickToBack();
            }
            for(int i = 0; i < savedInstanceState.getInt("pinLength"); i++) {
                clickToPin(Character.getNumericValue(savedInstanceState.getString("enteredPin").charAt(i)));
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("enteredPin", pin);
        savedInstanceState.putInt("pinLength", pin.length());
    }



    private void showStars() {
        String entered = "";

        for (int i = 0; i < pin.length(); i++) {
            //entered = entered + " \u25c9 ";         // 25cf is also an option
            stars[i].setTextColor(getResources().getColor(R.color.bright_orange));
        }

        for (int i = pin.length(); i < 4; i++) {
            stars[i].setTextColor(getResources().getColor(R.color.dark_blue));
        }

    }

    private void clickToPin(int x) {
        int size = pin.length();
        if(size < 4 && size >= 0) {
            pin = pin.concat(Integer.toString(x));

            showStars();

            if(pin.length() == 4) {
                TextView tv = (TextView) findViewById(R.id.textView_pin_message);
                tv.setTextColor(getResources().getColor(R.color.bright_orange));
                tv.setText(getString(R.string.payment_tap_to_pay));

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                editor.putString("lastPIN", pin);
                editor.commit();

                Vibrator vib = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
                vib.vibrate(200);
            }

        }
    }

    private void clickToBack() {

        int size = pin.length();
        if(size <= 4 && size >= 1) {
            pin = pin.substring(0, pin.length() - 1);

            showStars();

            TextView tv = (TextView) findViewById(R.id.textView_pin_message);
            tv.setTextColor(getResources().getColor(R.color.white));
            tv.setText(getString(R.string.payment_enter_pin));

            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            editor.putString("lastPIN", "");
            editor.commit();

        }
    }

    /**
     * Need to clear pin when this activity closes.
     */
    private void close() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("lastPIN", "");
        editor.commit();

        finish();
    }

    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {

        // The card service will change this key to used when sending the payment
        if (key.equals("lastPIN") && preferences.getString(key, "").equals("used")) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            editor.putString("lastPIN", "");
            editor.commit();
            close();
        }
    }
}
