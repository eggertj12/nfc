package is.valitor.lokaverkefni.oturgjold;

import android.app.Application;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.action.ViewActions;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.inputmethod.EditorInfo;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasFocus;
import static android.support.test.espresso.matcher.ViewMatchers.hasImeAction;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
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

    // These tests should be run on a clean install, Espresso does not support branching on views

    public void testRegisterNewUser() {
        onView(withId(R.id.button_register_account)).perform(click());
        onView(withId(R.id.editAccountName)).perform(typeText("Hullabaloo Jon F"));
        onView(withId(R.id.editAccountName)).check(matches(hasImeAction(EditorInfo.IME_ACTION_NEXT)));
        onView(withId(R.id.editAccountName)).perform(pressImeActionButton());
        onView(withId(R.id.editAccountSSN)).perform(typeText("0615156969"));
        onView(withId(R.id.editAccountSSN)).perform(pressImeActionButton());
        onView(withId(R.id.button_register_account)).perform(click());
    }

    public void testRegisterFirstCard() {
        onView(withId(R.id.button_register_card)).perform(click());
        onView(withId(R.id.editCardNumber)).perform(typeText("4539453945397896"));
        onView(withId(R.id.editCardNumber)).perform(pressImeActionButton());
        onView(withText("5")).perform(click());
        onView(withText("2020")).perform(click());
        onView(withId(R.id.editCardCvv)).perform(typeText("5555"));
        onView(withId(R.id.editCardCvv)).perform(pressImeActionButton());
        onView(withId(R.id.button_customize_card_next));
    }

    public void testTextEdits() {
    }



}