package is.valitor.lokaverkefni.oturgjold;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class PaymentActivity extends Activity {

    private String pin = "";
    private RadioGroup[] disp;

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
        Button button12 = (Button) findViewById(R.id.button_pin_forward);

        disp = new RadioGroup[4];
        disp[0] = (RadioGroup) findViewById(R.id.radioButton_1);
        disp[1] = (RadioGroup) findViewById(R.id.radioButton_2);
        disp[2] = (RadioGroup) findViewById(R.id.radioButton_3);
        disp[3] = (RadioGroup) findViewById(R.id.radiobutton_4);


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
        button12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("wrote into extra intent");
                Intent intent = getIntent();
                intent.putExtra("PIN", pin);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
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

    private void clickToPin(int x) {
        int size = pin.length();
        if(size < 4 && size >= 0) {
            pin = pin.concat(Integer.toString(x));
            // OMG YOU WOULD THINK A BSC IN COMPSCI WOULD MAKE ME A BETTER PROGRAMMER
            switch(size) {
                case 0:
                    disp[size].check(R.id.pin_radio_1);
                    break;
                case 1:
                    disp[size].check(R.id.pin_radio_2);
                    break;
                case 2:
                    disp[size].check(R.id.pin_radio_3);
                    break;
                case 3:
                    disp[size].check(R.id.pin_radio_4);
                    findViewById(R.id.button_pin_forward).setVisibility(View.VISIBLE);
                    break;
                default:
                    // sigh
                    break;
            }

        }
    }

    private void clickToBack() {
        int size = pin.length();
        if(size <= 4 && size >= 1) {
            pin = pin.substring(0, pin.length() - 1);
            disp[pin.length()].clearCheck();
            findViewById(R.id.button_pin_forward).setVisibility(View.INVISIBLE);
        }
    }
}
