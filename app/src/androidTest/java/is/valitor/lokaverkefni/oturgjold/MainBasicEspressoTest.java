package is.valitor.lokaverkefni.oturgjold;

import android.app.Application;
import android.support.test.espresso.action.ViewActions;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.action.ViewActions.*;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@LargeTest
public class MainBasicEspressoTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public MainBasicEspressoTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    public void testClickClicks() {
    }

    public void testTextEdits() {
        onView(withId(R.id.button_register_card)).perform(click());

        onView(withId(R.id.editCardNumber)).perform(typeText("4539481729817506"));
        onView(withId(R.id.editCardCvv)).perform(typeText("666"));
        onView(withId(R.id.editPIN)).perform(typeText("666"));
        onView(withId(R.id.button_register_card_next)).perform(ViewActions.click());

    }



}