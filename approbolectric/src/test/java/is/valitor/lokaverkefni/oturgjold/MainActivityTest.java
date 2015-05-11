package is.valitor.lokaverkefni.oturgjold;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.FragmentTestUtil;

import javax.xml.soap.Text;

import is.valitor.lokaverkefni.oturgjold.repository.Card;
import is.valitor.lokaverkefni.oturgjold.repository.Repository;
import is.valitor.lokaverkefni.oturgjold.repository.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class MainActivityTest {

    Activity activity;
    View reg_acc;
    View reg_card;
    View pay;
    View vp;

    @Test
    public void InitialActivityTest()
    {
        activity = Robolectric.buildActivity(MainActivity.class).create().get();
        reg_acc = activity.findViewById(R.id.sublayout_register);
        reg_card = activity.findViewById(R.id.sublayout_add_card);
        pay = activity.findViewById(R.id.payment_button_layout);
        vp = activity.findViewById(R.id.card_pager_layout);

        // Check visibility of UI elements, only register user button should be visible
        assertTrue(reg_acc.getVisibility() == View.VISIBLE);
        assertTrue(reg_card.getVisibility() == View.INVISIBLE);
        assertTrue(pay.getVisibility() == View.INVISIBLE);
    }

    @Test
    public void NoCardTest()
    {
        Context ctx = Robolectric.application;
//        Context ctx = Robolectric.getShadowApplication().getApplicationContext();

        User user = new User();
        Repository.setUser(ctx, user);

        activity = Robolectric.buildActivity(MainActivity.class).create().get();
        reg_acc = activity.findViewById(R.id.sublayout_register);
        reg_card = activity.findViewById(R.id.sublayout_add_card);
        pay = activity.findViewById(R.id.payment_button_layout);
        vp = activity.findViewById(R.id.card_pager_layout);

        // Check visibility of UI elements, now register card should be visible
        assertTrue(reg_acc.getVisibility() == View.INVISIBLE);
        assertTrue(reg_card.getVisibility() == View.VISIBLE);
        assertTrue(pay.getVisibility() == View.INVISIBLE);
        assertTrue(vp.getVisibility() == View.INVISIBLE);
    }

    @Test
    public void HasCardTest()
    {
        Context ctx = Robolectric.application;
//        Context ctx = Robolectric.getShadowApplication().getApplicationContext();

        User user = new User();
        Repository.setUser(ctx, user);
        Card card = new Card();
        Repository.addCard(ctx, card);

        activity = Robolectric.buildActivity(MainActivity.class).create().get();
        reg_acc = activity.findViewById(R.id.sublayout_register);
        reg_card = activity.findViewById(R.id.sublayout_add_card);
        pay = activity.findViewById(R.id.payment_button_layout);
        vp = activity.findViewById(R.id.card_pager_layout);

        // Check visibility of UI elements, now register card should be visible
        assertTrue(reg_acc.getVisibility() == View.INVISIBLE);
        assertTrue(reg_card.getVisibility() == View.INVISIBLE);
        assertTrue(pay.getVisibility() == View.VISIBLE);
        assertTrue(pay.getVisibility() == View.VISIBLE);
    }



    @Test
    public void BasicCardFragmentTest()
    {
        activity = Robolectric.buildActivity(MainActivity.class).create().get();

        ViewPager pager = (ViewPager) activity.findViewById(R.id.cardPager);
        assertTrue(pager != null);

        // Add one card
        Context ctx = activity.getApplication();
        Card card = new Card();
        card.setCard_id(1);
        card.setLast_four("4444");
        Repository.addCard(ctx, card);

        // Pager should have 1 fragment now
        FragmentPagerAdapter ad = (FragmentPagerAdapter) pager.getAdapter();
        assertTrue(ad.getCount() == 1);

        // Get the fragment and verify last 4
        Fragment fragment = ad.getItem(pager.getCurrentItem());
        FragmentTestUtil.startFragment(fragment);
        TextView cn = (TextView) fragment.getView().findViewById(R.id.fragmentCardNumber);
        assertEquals(true, cn instanceof TextView);
        assertEquals("XXXX XXXX XXXX 4444", ((TextView) cn).getText());
    }

}