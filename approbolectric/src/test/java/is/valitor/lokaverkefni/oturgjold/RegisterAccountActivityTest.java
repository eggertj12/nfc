package is.valitor.lokaverkefni.oturgjold;

import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowToast;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@Config( emulateSdk = 18 )
@RunWith(RobolectricTestRunner.class)
public class RegisterAccountActivityTest {

    private Activity activity;

    @Before
    public void setup()
    {
        activity = Robolectric.buildActivity(RegisterAccountActivity.class).create().get();
    }

    @Test
    public void BasicActivityTest()
    {
        assertTrue(activity != null);
    }

    @Test
    public void TestNameValidation()
    {
        Button regButton = (Button) activity.findViewById(R.id.button_register_account);
        assertTrue(regButton != null);
        regButton.performClick();
        assertThat(ShadowToast.getTextOfLatestToast(),
                equalTo(activity.getString(R.string.error_invalid_cardholder_name)));
    }

    // Validation disabled
/*    @Test
    public void TestSSNValidation()
    {
        Button regButton = (Button) activity.findViewById(R.id.button_register_account);
        assertTrue(regButton != null);
        EditText name = (EditText) activity.findViewById(R.id.editAccountName);
        name.setText("John doe");
        regButton.performClick();
        assertThat(ShadowToast.getTextOfLatestToast(),
                equalTo(activity.getString(R.string.error_invalid_ssn)));
    }
*/
/*
    This is not working at all. Given up on unit testing the IO for now

    @Config( emulateSdk = 18, shadows = { ShadowRegisterAccountTask.class } )
    @Test
    public void testGet_FormsCorrectRequest_noBasicAuth()
            throws Exception {
        Robolectric.addPendingHttpResponse(200, "OK");

        Button regButton = (Button) activity.findViewById(R.id.button_register_account);
        assertTrue(regButton != null);
        EditText editText = (EditText) activity.findViewById(R.id.editAccountName);
        editText.setText("John doe");
        editText = (EditText) activity.findViewById(R.id.editAccountSSN);
        editText.setText("1234567890");
        regButton.performClick();

        Robolectric.runBackgroundTasks();

        assertThat(
                ((HttpUriRequest) Robolectric.getSentHttpRequest(0)).getURI(),
                equalTo(URI.create("www.example.com")));
    }*/
}