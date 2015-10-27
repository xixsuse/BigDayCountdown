package com.simonm.bigdaycountdown;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.simonm.bigdaycountdown.Utils.AnimUtil;

import java.util.ArrayList;


// TODO: HIDE DRAWER BUTTON WHEN ON CREATE NEW DATE VIEW

// FOR THE TODO, I'D RECOMMEND ADD A BOOL AND THEN USING SOMETHING LIKE
//mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    // Variables
    protected int NumberOfDatesTracked;

    // All current tracked dates will be stored in this arrayList.
    protected ArrayList<trackedDate> trackedDatesList;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    protected CharSequence mTitle;
    private String[] menuTitles;

    private RelativeLayout main_view;
    private RelativeLayout get_started_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initViews();
        setupDrawer();

        if (savedInstanceState == null) {
            selectItem(0);
            selectItem(0);
        }

        // Checking wether any dates are tracked, if not, we display a get started screen.
        startScreenCheck(getNumberOfDatesTracked(), get_started_view, main_view);

    }

    private void initViews(){
        main_view = (RelativeLayout) findViewById(R.id.content_main_id);
        get_started_view = (RelativeLayout) findViewById(R.id.content_get_started_id);

        Button get_started_hint1 = (Button) findViewById(R.id.hint1);
        get_started_hint1.setOnClickListener(this);

        ImageView drawerButton = (ImageView) findViewById(R.id.menu_hint_button);
        drawerButton.setOnClickListener(this);

        FloatingActionButton fab_add_date = (FloatingActionButton) findViewById(R.id.fab_add_date);
        fab_add_date.setOnClickListener(this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }
    private void setupDrawer(){
        menuTitles = getResources().getStringArray(R.array.menu_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, menuTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());


        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                null,  /* Dont have a toolbar */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        )
        {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                AnimUtil.crossfade(main_view, findViewById(R.id.content_add_date_id), getResources().getInteger(android.R.integer.config_mediumAnimTime));
                break;
            case R.id.fab_add_date:
                AnimUtil.crossfade(findViewById(R.id.content_add_date_id), main_view, getResources().getInteger(android.R.integer.config_mediumAnimTime));
                break;
            case R.id.hint1:
                Log.i("tag", "got here!");

                // Retrieves and caches the systems default medium animations time.
                Integer mediumAnimationDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);

                // removes the onClickListener
                findViewById(R.id.hint1).setOnClickListener(null);

                // Fade out help screen
                // Fade in main screen
                AnimUtil.crossfade(get_started_view, main_view, mediumAnimationDuration);
                break;
            case R.id.menu_hint_button:
                Log.i("tag", "opens drawer");

                // open drawer
                mDrawerLayout.openDrawer(mDrawerList);
                break;

        }
    }



    public int getNumberOfDatesTracked() {

        return NumberOfDatesTracked;
    }

    public void setNumberOfDatesTracked(int numberOfDatesTracked) {
        NumberOfDatesTracked = numberOfDatesTracked;
    }

    // Check to see if any dates are tracked, if not -> We display a get Started Screen.
    public void startScreenCheck(int numberOfDatesTracked, View get_started_view, View main_view) {
        // Initialize the different views
        Log.i("tag", String.valueOf(numberOfDatesTracked));
        if (numberOfDatesTracked == 0) {
            Log.i("tag", "if test ran");
            main_view.setVisibility(View.VISIBLE);
            get_started_view.setVisibility(View.VISIBLE);
            Log.i("tag", String.valueOf(get_started_view.getVisibility()));
        } else {
            Log.i("tag", "this does not run");
            main_view.setVisibility(View.VISIBLE);
            get_started_view.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
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
        switch (item.getItemId()) {
        }
        return super.onOptionsItemSelected(item);
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(menuTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
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

}