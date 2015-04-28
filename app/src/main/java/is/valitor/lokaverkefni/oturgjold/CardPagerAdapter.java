package is.valitor.lokaverkefni.oturgjold;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.ArrayMap;

import java.util.ArrayList;

import is.valitor.lokaverkefni.oturgjold.repository.Repository;

/**
 * Created by eggert on 18/04/15.
 */
public class CardPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

    private Context appContext;
    private ArrayMap<Integer, CardFragment> fragments = new ArrayMap();

    public CardPagerAdapter(FragmentManager fm, Context ctx)
    {
        super(fm);
        this.appContext = ctx;
    }

    @Override
    public Fragment getItem(int i)
    {
        Fragment fragment = new CardFragment();
        Bundle args = new Bundle();

        // Just put in the index. Fragment will handle loading data from repo
        args.putInt(CardFragment.CARDFRAGMENT_CARDINDEX, i);
        fragment.setArguments(args);

        // Store fragment for future reference.
        fragments.put(i, (CardFragment) fragment);

        return fragment;
    }

    @Override
    public int getCount()
    {
        return Repository.getCardCount(this.appContext);
    }

    @Override
    public void onPageSelected(int position)
    {
        if (fragments.containsKey(position)) {
            // Don't do this, its obtrusive
            //fragments.get(position).getCurrentBalance();
        }
        Repository.setSelectedCardByIndex(this.appContext, position);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {
    }

    @Override
    public void onPageScrollStateChanged(int state)
    {

    }
}
