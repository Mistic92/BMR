/*
 * Copyright (c) 2015 Łukasz Byjoś
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.devnoobs.bmr;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.devnoobs.bmr.Fragments.FragmentBMR;
import com.devnoobs.bmr.Fragments.FragmentTabele;
import com.devnoobs.bmr.Fragments.FragmentUstawienia;
import com.devnoobs.bmr.Fragments.FragmentWykres;
import com.devnoobs.bmr.Fragments.FragmentWyniki;
import com.devnoobs.bmr.Interfejsy.IReklamy;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;


@SuppressLint("NewApi")

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener,
        IReklamy
{

    /**
     The {@link android.support.v4.view.PagerAdapter} that will provide
     fragments for each of the sections. We use a
     {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     will keep every loaded fragment in memory. If this becomes too memory
     intensive, it may be best to switch to a
     {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    //private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    //private AdView adView;
    private ActionBar.Tab tab;
    private FragmentUstawienia fu;
    //Tracker tracker ;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] nazwy_menu;
    private static final String PROPERTY_ID = "UA-18780283-7";
    //toolbar

    ImageView menuIcon;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        //final View view = findViewById(R.id.drawer_layout);
        setContentView(R.layout.activity_main);
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbarGlowny);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_18dp);

        if (toolbar != null)
        {
            setSupportActionBar(toolbar);
        }
        //  toolbar.setLogo(R.drawable.ic_menu_white_18dp);


        Tracker t = (getTracker(TrackerName.APP_TRACKER));
        t.setScreenName("Home");
        t.send(new HitBuilders.AppViewBuilder().build());

        mTitle = mDrawerTitle = getTitle();
        nazwy_menu = getResources().getStringArray(R.array.tabela_menu);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, nazwy_menu));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());


        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                // toolbar,  /* nav drawer image to replace 'Up' caret */
                R.string.rozwin_drawer,  /* "open drawer" description for accessibility */
                R.string.zwin_drawer  /* "close drawer" description for accessibility */
        )
        {
            public void onDrawerClosed(View view)
            {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView)
            {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null)
        {
            selectItem(0);
        }

    }//oncreate


    public enum TrackerName
    {
        APP_TRACKER, // Tracker used only in this app.
        // ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }

    public HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

//todo skrocic to bo tylko apptracker uzywam

    /**
     @param trackerId

     @return
     */
    public synchronized Tracker getTracker(TrackerName trackerId)
    {
        if (!mTrackers.containsKey(trackerId))
        {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = analytics.newTracker(PROPERTY_ID);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        // If the nav drawer is open, hide action items related to the content view
        //  boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        // Handle action buttons
        return true;


    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction
            fragmentTransaction)
    {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction
            fragmentTransaction)
    {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction
            fragmentTransaction)
    {

    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            selectItem(position);
        }
    }

    private void selectItem(int position)
    {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction tran = fragmentManager.beginTransaction();
//        tran.setCustomAnimations(FragmentTransaction.TRANSIT_ENTER_MASK, FragmentTransaction.TRANSIT_EXIT_MASK);
//         tran.setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
        if (position == 0)
        {
            Fragment bmr = (Fragment) new FragmentBMR();

            tran.replace(R.id.content_frame, bmr);
            tran.commit();
            // fragmentManager.beginTransaction().replace(R.id.content_frame, bmr).commit();

        }
        else if (position==1)
        {
            Fragment wyniki = (Fragment) new FragmentWyniki();
            tran.replace(R.id.content_frame,wyniki);
            tran.commit();
        }
// else if (position == 1)
//        {
//            Fragment tabele = (Fragment) new FragmentTabele();
////            FragmentManager fragmentManager = getFragmentManager();
////            fragmentManager.beginTransaction() .replace(R.id.content_frame, tabele).commit();
//            tran.replace(R.id.content_frame, tabele);
//            tran.commit();
//
//        } else if (position == 2)
//        {
//            Fragment wykresy = (Fragment) new FragmentWykres();
////            FragmentManager fragmentManager = getFragmentManager();
////            fragmentManager.beginTransaction().replace(R.id.content_frame, wykresy).commit();
//            tran.replace(R.id.content_frame, wykresy);
//            tran.commit();
//
//        }
//

        else if (position == 2)
        {
            Fragment ustawienia = (Fragment) new FragmentUstawienia();
//            FragmentManager fragmentManager = getFragmentManager();
//            fragmentManager.beginTransaction().replace(R.id.content_frame, ustawienia).commit();
            tran.replace(R.id.content_frame, ustawienia);
            tran.commit();

        }

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(nazwy_menu[position]);
        mDrawerLayout.closeDrawer(mDrawerList);

    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     When using the ActionBarDrawerToggle, you must call it during
     onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


//    @Override
//    public void onTabSelected(Tab tab,
//                              FragmentTransaction fragmentTransaction) {
//        // When the given tab is selected, switch to the corresponding page in
//        // the ViewPager.
//
//        mViewPager.setCurrentItem(tab.getPosition());
//    }
//
//    @Override
//    public void onTabUnselected(Tab tab,
//                                FragmentTransaction fragmentTransaction) {
//    }
//
//    @Override
//    public void onTabReselected(Tab tab,
//                                FragmentTransaction fragmentTransaction) {
//    }


    //    @Override
//    public void onDismissScreen(Ad arg0) {
//        // TODO Auto-generated method stub
//
//    }
//
//
//    @Override
//    public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
//        //adView.setVisibility(View.GONE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            // super.setTheme(android.R.style.Theme_Holo_Light_NoActionBar_TranslucentDecor);
//            // Window w = getWindow();
//
//            //    w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
//            // WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }
//
//    }
//
//
//    @Override
//    public void onLeaveApplication(Ad arg0) {
//
//    }
//
//
//    @Override
//    public void onPresentScreen(Ad arg0) {
//        // TODO Auto-generated method stub
//
//    }
//
//
//    @Override
//    public void onReceiveAd(Ad arg0) {
//
//    }
//
//

    @Override
    protected void onStart()
    {
        super.onStart();
        GoogleAnalytics.getInstance(MainActivity.this).reportActivityStart(this);
        //Get an Analytics tracker to report app starts & uncaught exceptions etc.

    }


    @Override
    protected void onStop()
    {
        super.onStop();
        //Stop the analytics tracking
        GoogleAnalytics.getInstance(MainActivity.this).reportActivityStop(this);
    }

    @Override
    public void zmienReklamy(boolean stan)
    {

        //Toast toast = Toast.makeText(getApplicationContext(), "test",5 );
        //toast.show();
//
//        if (stan == true) {
//            adView.setVisibility(View.VISIBLE);
//            adView.loadAd(new AdRequest());
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                Window w = getWindow(); // in Activity's onCreate() for instance
//                w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//                // w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
//                // WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//                //w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//                // WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            }
//        } else {
//            adView.setVisibility(View.GONE);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                super.setTheme(android.R.style.Theme_Holo_Light_NoActionBar_TranslucentDecor);
//                Window w = getWindow();
//                w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
//                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            }
//
//        }

    }


}//klasa glowna
