package org.jboss.aerogear.pushtest.activity;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import org.jboss.aerogear.android.unifiedpush.MessageHandler;
import org.jboss.aerogear.android.unifiedpush.Registrations;
import org.jboss.aerogear.pushtest.BaseFragment;
import org.jboss.aerogear.pushtest.R;
import org.jboss.aerogear.pushtest.fragment.PerformanceOutputFragment;
import org.jboss.aerogear.pushtest.fragment.SimpleOutputFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements MessageHandler {

    private MainActivityPagerAdapter pagerAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) { }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) { }
        };


        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        pagerAdapter = new MainActivityPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                getActionBar().setSelectedNavigationItem(position);
            }
        });

        pagerAdapter.addBaseFragment(new SimpleOutputFragment());
        pagerAdapter.addBaseFragment(new PerformanceOutputFragment());

        for(BaseFragment fragment : pagerAdapter.getItems()) {
            actionBar.addTab(
                    actionBar.newTab()
                        .setText(fragment.getTitle())
                        .setTabListener(tabListener));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Registrations.registerMainThreadHandler(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Registrations.unregisterMainThreadHandler(this);
    }

    @Override
    public void onMessage(Context context, Bundle message) {
        BaseFragment activeFragment = pagerAdapter.getItem(viewPager.getCurrentItem());

        activeFragment.onPushMessageReceived(context, message);
    }

    @Override
    public void onDeleteMessage(Context context, Bundle message) {
        BaseFragment current = getCurrentBaseFragment();

        current.onDeletePushMessage(context, message);
    }

    @Override
    public void onError() {
        BaseFragment current = getCurrentBaseFragment();

        current.onPushMessageError();
    }

    private BaseFragment getCurrentBaseFragment() {
        return pagerAdapter.getItem(viewPager.getCurrentItem());
    }

    public class MainActivityPagerAdapter extends FragmentPagerAdapter {
        private List<BaseFragment> fragments = new ArrayList<BaseFragment>();

        public MainActivityPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public BaseFragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getItem(position).getTitle();
        }

        public void addBaseFragment(BaseFragment fragment) {
            fragments.add(fragment);
            notifyDataSetChanged();
        }

        public List<BaseFragment> getItems() {
            return fragments;
        }
    }
}
