package is.valitor.lokaverkefni.oturgjold;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import is.valitor.lokaverkefni.oturgjold.repository.Repository;

/**
 * For conforming cards for display in fragment on MainActivity
 * Created by eggert on 18/04/15.
 */
public class CardPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

    private final Context appContext;
    private final Activity activity;

    /**
     * Constructor
     *
     * @param fm fragment manager that handles the fragments
     * @param ctx application context of the parent activity
     * @param act the parent activity, used for updating title
     */
    public CardPagerAdapter(FragmentManager fm, Context ctx, Activity act) {
        super(fm);
        this.appContext = ctx;
        this.activity = act;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new CardFragment();
        Bundle args = new Bundle();

        // Just put in the index. Fragment will handle loading data from repo
        args.putInt(CardFragment.CARDFRAGMENT_CARDINDEX, i);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getCount() {
        return Repository.getCardCount(this.appContext);
    }

    @Override
    public void onPageSelected(int position) {
        Repository.setSelectedCardByIndex(this.appContext, position);

        // Update card name in ActionBar
        String cardName = Repository.getSelectedCard(appContext).getCard_name();
        if (cardName.length() > 0) {
            activity.setTitle(cardName);
        } else {
            activity.setTitle(appContext.getString(R.string.app_name));
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}
