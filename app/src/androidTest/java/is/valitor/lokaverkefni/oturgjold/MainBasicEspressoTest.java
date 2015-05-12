package is.valitor.lokaverkefni.oturgjold;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.inputmethod.EditorInfo;

import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

import is.valitor.lokaverkefni.oturgjold.repository.Card;
import is.valitor.lokaverkefni.oturgjold.repository.Repository;


import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.onData;


import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasFocus;
import static android.support.test.espresso.matcher.ViewMatchers.hasImeAction;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.action.ViewActions.*;
import static org.hamcrest.Matchers.startsWith;

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

    private void clearData() {
        Repository.removeTokens(getActivity());
        Repository.removeAllCards(getActivity());
        Repository.removeUserInfo(getActivity());
    }

    // Because the soft keyboard is flaky on old devices
    private void pause() {
        try {
            Thread.sleep(500);
        }
        catch (Exception e) {
            // Whatever
        }
    }

    // Tests methods are run in alphabetical order

    public void testA_RegisterNewUser() {
        //clearData();
        onView(withId(R.id.main_register_account)).perform(click());
        onView(withId(R.id.editAccountName)).perform(typeText("Hullabaloo Jon F"));
        onView(withId(R.id.editAccountName)).check(matches(hasImeAction(EditorInfo.IME_ACTION_NEXT)));
        onView(withId(R.id.editAccountName)).perform(pressImeActionButton());
        onView(withId(R.id.editAccountSSN)).perform(typeText("0615156969"));
        onView(withId(R.id.editAccountSSN)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.button_register_account)).check(ViewAssertions.matches(isCompletelyDisplayed()));
        pause();
        onView(withId(R.id.button_register_account)).perform(click());
    }

    public void testB_RegisterCard() {
        onView(withId(R.id.main_register_card)).perform(click());
        onView(withId(R.id.editCardNumber)).perform(typeText("4539453945394539"));
        pause();

        /*
        Spinner items would need a specific handle to be clicked on, testing library drawback
        cant find appropriate hamcrest matcher
        onView(swipeDow)
                onView(withId(R.id.spinnerValidityYear)).perform(click());
        onData(allOf(is(instanceOf(String.class)), withText("2016")))
                .perform(click());
        onView(withId(R.id.spinnerValidityMonth)).perform(swipeDown(), click());
        onView(withText(5)).perform(click());
        pause();
        onView(withId(R.id.spinnerValidityYear)).perform(click());
        pause();
        */
        onView(withId(R.id.editCardCvv)).perform(typeText("6969"), ViewActions.closeSoftKeyboard());
        pause();
        onView(withId(R.id.button_register_card_next)).perform(click());
        // At customize card
        onView(withId(R.id.customize_pin_inputfield)).perform(typeText("6969"), pressImeActionButton());
        onView(allOf(hasFocus(), withId(R.id.customize_nick_input))).perform(typeText("Happy!"), pressImeActionButton());
        onView(withId(R.id.ibAbsBrown)).perform(click());
        onView(withId(R.id.customize_next)).perform(click());
        // At finalize register card
        onView(withText(R.string.label_no_caps)).perform(click());
    }

    public void testC_RegisterAnotherCard() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Skrá kort")).perform(click());
        registerCard();
        customizeCard();
        onView(withId(R.id.reg_new_card)).perform(click());
    }

    public void testD_RegisterCards() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Skrá kort")).perform(click());
        registerCard();
        customizeCard();
        onView(withId(R.id.button_finish_default_card)).perform(click());
        registerCard();
        customizeCard();
        onView(withId(R.id.reg_new_card)).perform(click());
    }

    public void testE_ShowTransactionsEmpty() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Yfirlit")).perform(click());
        onView(withText("Engar færslur")).check(ViewAssertions.matches(isDisplayed()));
    }

    public void testF_ChangeSelectedCard() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Skipta um valið kort")).perform(click());
        onView(withId(R.id.change_selected_card)).check(matches(isDisplayed()));
    }

    private void registerCard() {
        onView(withId(R.id.editCardNumber)).perform(typeText("4539453945394539"));
        pause();
        onView(withId(R.id.editCardCvv)).perform(typeText("6969"), ViewActions.closeSoftKeyboard());
        pause();
        onView(withId(R.id.button_register_card_next)).perform(click());
    }

    private void customizeCard() {
        onView(withId(R.id.customize_pin_inputfield)).perform(typeText("6969"), pressImeActionButton());
        onView(allOf(hasFocus(), withId(R.id.customize_nick_input))).perform(typeText("Happy Espresso!"), pressImeActionButton());
        onView(withId(R.id.ibAbsBrown)).perform(click());
        onView(withId(R.id.customize_next)).perform(click());
    }

}