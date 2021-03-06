/*
 * Copyright (c) 2014 Łukasz Byjoś
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.devnoobs.bmr;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.devnoobs.bmr.Fragments.FragmentBMR;
import com.devnoobs.bmr.Fragments.FragmentTabele;
import com.devnoobs.bmr.Fragments.FragmentUstawienia;
import com.devnoobs.bmr.Fragments.FragmentWykres;
import com.devnoobs.bmr.Interfejsy.IReklamy;
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;



@SuppressLint("NewApi")

public class MainActivity extends FragmentActivity implements ActionBar.TabListener, AdListener,IReklamy {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	//private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	private AdView adView;
	private Tab tab;
	private FragmentUstawienia fu;
	//Tracker tracker ;
	
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] nazwy_menu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
	       super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);

	        mTitle = mDrawerTitle = getTitle();
	        nazwy_menu = getResources().getStringArray(R.array.tabela_menu);
	        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
	        mDrawerList = (ListView) findViewById(R.id.left_drawer);

	        // set a custom shadow that overlays the main content when the drawer opens
	        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
	        // set up the drawer's list view with items and click listener
	        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
	                R.layout.drawer_list_item, nazwy_menu));
	        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

	        // enable ActionBar app icon to behave as action to toggle nav drawer
	        getActionBar().setDisplayHomeAsUpEnabled(true);
	        getActionBar().setHomeButtonEnabled(true);

	        // ActionBarDrawerToggle ties together the the proper interactions
	        // between the sliding drawer and the action bar app icon
	        mDrawerToggle = new ActionBarDrawerToggle(
	                this,                  /* host Activity */
	                mDrawerLayout,         /* DrawerLayout object */
	                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
	                R.string.rozwin_drawer,  /* "open drawer" description for accessibility */
	                R.string.zwin_drawer  /* "close drawer" description for accessibility */
	                ) {
	            public void onDrawerClosed(View view) {
	                getActionBar().setTitle(mTitle);
	                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
	            }

	            public void onDrawerOpened(View drawerView) {
	                getActionBar().setTitle(mDrawerTitle);
	                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
	            }
	        };
	        mDrawerLayout.setDrawerListener(mDrawerToggle);

	        if (savedInstanceState == null) {
	            selectItem(0);
	        }
		  //  adView = (AdView) findViewById(R.id.adView);
		 //   adView.loadAd(new AdRequest());
		
		
		
	/*	
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//inicjalizacja tutaj aby dodac listenera
		fu = new FragmentUstawienia();
		fu.addListenerReklamy(this);
		
		//tracker = GoogleAnalytics.getInstance(this).getTracker("UA-18780283-6"); 
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		//actionBar.hide();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		//mViewPager = (ViewPager) findViewById(R.id.pager);
		//mViewPager.setAdapter(mSectionsPagerAdapter);
		//actionBar.setTitle(getString(R.string.app_name));
	
	/*	
		if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.KITKAT)
		{
			mViewPager.setOnSystemUiVisibilityChangeListener(this);
			mViewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE);
			mViewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
			mViewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		}
		
	*/	
		
		/*
		 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
		        Window w = getWindow(); // in Activity's onCreate() for instance
		        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		        //w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		    }
		

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
		}
		//ustawienie ikonek w pasku

		
		Tab ab = getActionBar().getTabAt(0);
		ab.setIcon(R.drawable.ic_kalkulator);	
		ab = getActionBar().getTabAt(1);
		ab.setIcon(R.drawable.ic_tabela);
		ab = getActionBar().getTabAt(2);
		ab.setIcon(R.drawable.ic_wykres);		
		ab = getActionBar().getTabAt(3);
		ab.setIcon(R.drawable.ic_ustawienia);	

		
		 // Create the adView
	    adView = (AdView) findViewById(R.id.adView);
	    adView.loadAd(new AdRequest());
	   // adView.setAdListener(this);

		 */
        
        
        
	}//oncreate

	

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
      //  boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
		return true;
 

        
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
    	
    	if(position==0)
    	{
    		Fragment bmr = (Fragment) new FragmentBMR();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, bmr).commit();
    	}
    	else if(position==1)
    	{
    		Fragment tabele = (Fragment) new FragmentTabele();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, tabele).commit();
    	}
    	else if(position==2)
    	{
    		Fragment wykresy = (Fragment) new FragmentWykres();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, wykresy).commit();
    	}
    	else if(position==3)
    	{
    		Fragment ustawienia = (Fragment) new FragmentUstawienia();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, ustawienia).commit();	
    	}

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(nazwy_menu[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
       
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
	
	
	
	
	
	  @Override
	  public void onStart() {
	    super.onStart();	   
	    EasyTracker.getInstance(this).activityStart(this);  // Add this method.

	  }

	  @Override
	  public void onStop() {
	    super.onStop();	   
	    EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	  }
	
	
	@Override
	public void onTabSelected(Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(Tab tab,
			FragmentTransaction fragmentTransaction) {
	}
	
	

/*
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		/**
		 * W zale�no�ci od pozycji pokazuje dany fragment
		 
		@Override
		public Fragment getItem(int position) {
			
 
			switch(position)
			{
			case 0:
				//tracker.send(MapBuilder.createAppView().set(Fields.SCREEN_NAME, "Kalkulator").build());
				return FragmentBMR.init(position);
			case 1:
				//tracker.send(MapBuilder.createAppView().set(Fields.SCREEN_NAME, "Wyniki").build());
				return FragmentTabele.init(position);
			case 2: 
				//tracker.send(MapBuilder.createAppView().set(Fields.SCREEN_NAME, "Wykresy").build());
				return FragmentWykres.init(position);
			case 3:
				//tracker.send(MapBuilder.createAppView().set(Fields.SCREEN_NAME, "Ustawienia").build());
				return fu.init(position);
			}
			return null;

		}
 
		@Override
		public int getCount() {
			// Show 4 total pages.
			return 4;
		}

	}//class section adapter

	*/
	
	@Override
	public void onDismissScreen(Ad arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
		//adView.setVisibility(View.GONE); 
		 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
		      // super.setTheme(android.R.style.Theme_Holo_Light_NoActionBar_TranslucentDecor);
			// Window w = getWindow(); 

		    //    w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		    }
		
	}


	@Override
	public void onLeaveApplication(Ad arg0) {
		
	}


	@Override
	public void onPresentScreen(Ad arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onReceiveAd(Ad arg0) {
		
	}


	@Override
	public void zmienReklamy(boolean stan) {
		
		//Toast toast = Toast.makeText(getApplicationContext(), "test",5 );
		//toast.show();
		
		if(stan==true)
		{
			adView.setVisibility(View.VISIBLE); 
		    adView.loadAd(new AdRequest());
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) 
			{	
			        Window w = getWindow(); // in Activity's onCreate() for instance
			        w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			       // w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			        //w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			    }
		}
		else
		{
			adView.setVisibility(View.GONE); 
			 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) 
			 {
			      super.setTheme(android.R.style.Theme_Holo_Light_NoActionBar_TranslucentDecor);
				 Window w = getWindow(); 
			       w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			    }
		    
		}
		
	}


	

}//klasa glowna
