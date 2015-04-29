package is.valitor.lokaverkefni.oturgjold;

import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@Config( emulateSdk = 18 )
@RunWith(RobolectricTestRunner.class)
public class PaymentActivityTest {

    private Activity activity;

    @Before
    public void setup()
    {
        activity = Robolectric.buildActivity(PaymentActivity.class).create().get();
    }

    @Test
    public void BasicActivityTest()
    {
        assertTrue(activity != null);
    }

    @Test
    public void TestInputButton()
    {
        Button btn_1 = (Button) activity.findViewById(R.id.button_pin_1);
        TextView stars = (TextView) activity.findViewById(R.id.pin_stars);
        assertTrue(btn_1 != null);
        assertTrue(stars != null);
        btn_1.performClick();
        assertEquals(3, stars.getText().length());
    }

    @Test
    public void TestBackspaceButton()
    {
        Button btn_1 = (Button) activity.findViewById(R.id.button_pin_1);
        Button btn_bs = (Button) activity.findViewById(R.id.button_pin_back);
        TextView stars = (TextView) activity.findViewById(R.id.pin_stars);
        assertTrue(btn_1 != null);
        assertTrue(stars != null);
        btn_1.performClick();
        btn_bs.performClick();
        assertEquals(0, stars.getText().length());
    }
}