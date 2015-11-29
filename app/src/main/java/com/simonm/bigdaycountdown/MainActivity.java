package com.simonm.bigdaycountdown;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import org.joda.time.DateTime;
import org.joda.time.Days;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.simonm.bigdaycountdown.Utils.AnimUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import pl.aprilapps.easyphotopicker.EasyImage;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener{

    // Temporary variables to store values before TrackedEvent object is created and put in the List.
    private boolean onAddDateScreen = false;
    protected Bitmap tempBackground;
    private int tempRotation;
    protected boolean alert;
    protected int tempYear = 0;
    protected int tempMonth = 0;
    protected int tempDay = 0;
    protected DateTime tempDate;
    String newEventTitle = "";
    Bitmap eventBG;

    // To be used by currently chosen event
    protected File eventBackground;
    protected Date eventDate;

    protected TrackedEvent currentEvent;


    // Variables

    protected int NumberOfDatesTracked;



    // All current tracked dates will be stored in this arrayList.

    protected ArrayList<TrackedEvent> myTrackedEventsList;
    protected List<String> eventNames = new ArrayList<>();

    private DrawerLayout mDrawerLayout;

    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView eDrawerList;
    private ActionBarDrawerToggle eDrawerToggle;


    protected CharSequence mTitle;
    private String[] menuTitles;

    private CharSequence eTitle;
    private String[] eventTitles;


    private RelativeLayout main_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initViews();
        setupLeftDrawer();
        setupRightDrawer();
        initVars();
        checkEventsEmpty();

        if (savedInstanceState == null) {
            selectItem(0);
            selectItem(0);
        }

        // Checking whether any dates are tracked, if not, we display a get started screen.
        startScreenCheck(getNumberOfDatesTracked(), main_view);

    }



    // For the background selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new EasyImage.Callbacks() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source) {
                //Some error handling
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source) {
                //Handle the image
                // Temporary store the image to later set it in the TrackedEvent object.
                tempBackground = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                ((ImageView) findViewById(R.id.backgroundPreview)).setImageBitmap(tempBackground);
            }
        });
    }

    // GETTERS & SETTERS
    public ArrayList<TrackedEvent> getMyTrackedEventsList() {
        return myTrackedEventsList;
    }


    // Initializes variables
    protected void initVars(){
        if (getMyTrackedEventsList() == null){
            myTrackedEventsList = new ArrayList<>();
        }
        // Else we already have a list containing tracked events
    }

    // Displays the date picker when the date field is clicked:
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "myFragmentManager");
    }

    // DateTime months and days are 0 indexed, DatePicker months are 0 indexed, not days ???
    public void onDateSet(DatePicker view, int year, int month, int day){
        TextView myEventDate = (TextView) findViewById(R.id.new_date_id);
        tempYear = year;
        tempMonth = month +1;
        tempDay = day + 1;
        String strYear = String.valueOf(year);
        String strMonth = String.valueOf(tempMonth);
        String strDay = String.valueOf(day);
        myEventDate.setText(strYear + '-' + strMonth + '-' + strDay);
        myEventDate.setTextColor(Color.rgb(96, 96, 96));
    }


    private void initViews(){
        main_view = (RelativeLayout) findViewById(R.id.content_main_id);


        ImageView eDrawerButton = (ImageView) findViewById(R.id.event_hint_button);
        eDrawerButton.setOnClickListener(this);

        FloatingActionButton fab_add_date = (FloatingActionButton) findViewById(R.id.fab_add_date);
        fab_add_date.setOnClickListener(this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        RelativeLayout galleryButton = (RelativeLayout) findViewById(R.id.gallery_button);
        galleryButton.setOnClickListener(this);

        ImageView deleteButton = (ImageView) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(this);

        ImageView rotateRButton = (ImageView) findViewById(R.id.rotateRButton);
        rotateRButton.setOnClickListener(this);

        ImageView rotateLButton = (ImageView) findViewById(R.id.rotateLButton);
        rotateLButton.setOnClickListener(this);


    }


    private void setupLeftDrawer(){
        menuTitles = getResources().getStringArray(R.array.menu_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        // START Implies left side
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, menuTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener("menu"));


        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                null,  /* Don't have a toolbar */
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

    private void setupRightDrawer(){
        // TODO: Change this to hold the events instead
        if (eventNames.size() == 0) {
            eventTitles = getResources().getStringArray(R.array.events_array);
        } else {
            eventTitles = new String[eventNames.size()];
            eventTitles = eventNames.toArray(eventTitles);
        }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        eDrawerList = (ListView) findViewById(R.id.right_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        // END Implies right hand side
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.END);
        // set up the drawer's list view with items and click listener
        eDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, eventTitles));
        eDrawerList.setOnItemClickListener(new DrawerItemClickListener("events"));


        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        eDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                null,  /* Don't have a toolbar */
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
        mDrawerLayout.setDrawerListener(eDrawerToggle);
    }

    private void updateEventDrawer() {
        if (eventNames.size() == 0) {
            eventTitles = getResources().getStringArray(R.array.events_array);
        } else {
            eventTitles = new String[eventNames.size()];
            eventNames.clear();
            for (TrackedEvent element : myTrackedEventsList){
                eventNames.add(element.getEventTitle());
            }
            eventTitles = eventNames.toArray(eventTitles);
        }
        eDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, eventTitles));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                resetVariables();
                onAddDateScreen = true;
                AnimUtil.crossfade(main_view, findViewById(R.id.content_add_date_id), getResources().getInteger(android.R.integer.config_mediumAnimTime));
                break;
            case R.id.fab_add_date:
                createAndStoreNewEvent();
                break;

            case R.id.event_hint_button:
                Log.i("tag", "opens drawer");

                // open drawer
                mDrawerLayout.openDrawer(eDrawerList);
                break;

            case R.id.gallery_button:
                onPickFromGaleryClicked();
                break;

            case R.id.deleteButton:
                deleteEvent();
                break;

            case R.id.rotateRButton:
                rotateBackground(90);
                break;

            case R.id.rotateLButton:
                rotateBackground(-90);
                break;

        }
    }
    //Rotates background 90 degrees.
    private void rotateBackground(int rotation) {
        if (tempRotation == 360 || tempRotation == -360){
            tempRotation = 0;
        }
        if (tempBackground == null){
            tempBackground = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        }
        tempBackground = rotateImage(tempBackground, rotation);
        tempRotation = tempRotation + rotation;

        ((ImageView) findViewById(R.id.backgroundPreview)).setImageBitmap(tempBackground);

    }

    public static Bitmap rotateImage(Bitmap src, float degree) {
        // create new matrix
        Matrix matrix = new Matrix();

        // setup rotation degree
        matrix.postRotate(degree);
        Bitmap bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        return bmp;
    }

    protected void createAndStoreNewEvent(){
        EditText tempEventTitleEditText = (EditText) findViewById(R.id.new_title_id);
        newEventTitle = tempEventTitleEditText.getText().toString();
        if (tempBackground == null){
            tempBackground = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        }
        // DateTime days and months are 0 indexed. Handled when I set tempMonth and tempDay
        if (validateDate(tempYear, tempMonth, tempDay)) {
            tempDate = new DateTime(tempYear, tempMonth, tempDay - 1, 0, 0);

            Log.i("day", String.valueOf(tempDay));
            if (validateInput(newEventTitle, tempBackground, tempDate)) {

                TrackedEvent newEvent = new TrackedEvent(alert, newEventTitle, tempDate, tempBackground);
                currentEvent = newEvent;
                Log.i("tag", newEvent.getDate().toString());
                myTrackedEventsList.add(newEvent);
                Collections.sort(myTrackedEventsList);

                eventNames.add(newEventTitle);
                updateEventDrawer();
                updateUI();
                eventNames.clear();
                for (TrackedEvent element : myTrackedEventsList) {
                    eventNames.add(element.getEventTitle());
                }
                AnimUtil.crossfade(findViewById(R.id.content_add_date_id), main_view, getResources().getInteger(android.R.integer.config_mediumAnimTime));
                ((EditText) findViewById(R.id.new_title_id)).setHintTextColor(Color.rgb(96, 96, 96));
                ((TextView) findViewById(R.id.new_date_id)).setTextColor(Color.rgb(96, 96, 96));
                tempBackground = null;

            }
        }

    }


    private boolean validateDate(int tempYear, int tempMonth, int tempDay) {
        if (tempDay == 0 || tempMonth == 0 || tempYear == 0){
            ((TextView) findViewById(R.id.new_date_id)).setTextColor(Color.RED);
            return false;
        }
        return true;
    }

    private void resetVariables() {
        tempBackground = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        ((ImageView) findViewById(R.id.backgroundPreview)).setImageBitmap(tempBackground);
        tempBackground = null;
        tempRotation = 0;
        tempDate = null;
        ((TextView) findViewById(R.id.new_date_id)).setText(R.string.date);
        ((EditText) findViewById(R.id.new_title_id)).setText("");
        ((EditText) findViewById(R.id.new_title_id)).setHint(R.string.title_for_event);
    }

    protected boolean validateInput(String title, Bitmap backGround, DateTime date){
        //TODO:
        Log.i("valueOfTitle", String.valueOf(title == null));
        Log.i("valueOfTitle", String.valueOf(title.equals("")));
        if (title.equals("")){
            ((EditText) findViewById(R.id.new_title_id)).setHintTextColor(Color.RED);
            return false;
        }


        return  true;
        // Background image
        // Event title
        // Date

    }

    protected void onTakePhotoClicked() {
        EasyImage.openCamera(this);
    }

    protected void onPickFromGaleryClicked() {
        EasyImage.openGalleryPicker(this);
    }



    public int getNumberOfDatesTracked() {

        return NumberOfDatesTracked;
    }

    public void setNumberOfDatesTracked(int numberOfDatesTracked) {
        NumberOfDatesTracked = numberOfDatesTracked;
    }

    // Check to see if any dates are tracked, if not -> We display a get Started Screen.
    public void startScreenCheck(int numberOfDatesTracked, View main_view) {
        // Initialize the different views
        Log.i("tag", String.valueOf(numberOfDatesTracked));
        if (numberOfDatesTracked == 0) {
            Log.i("tag", "if test ran");
            main_view.setVisibility(View.VISIBLE);
        } else {
            Log.i("tag", "this does not run");
            main_view.setVisibility(View.VISIBLE);
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

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        String name;
        public DrawerItemClickListener(String name){
            this.name = name;
        }
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.i("tag", String.valueOf(position));
            if (this.name.equals("events")){
                selectEventItem(position);

            } else if (this.name.equals("menu")){
                selectItem(position);
            }
        }
    }

    private void selectItem(int position) {

        // Update, then close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void selectEventItem(int position) {
        if (eventNames.size() != 0) {
            currentEvent = myTrackedEventsList.get(position);
            // update selected item and title, then close the drawer
            eDrawerList.setItemChecked(position, true);
            mDrawerLayout.closeDrawer(eDrawerList);
            updateUI();
            Log.i("Tag", "Selected event with index" + String.valueOf(position));
        } else {
            mDrawerLayout.closeDrawer(eDrawerList);
        }
    }

    protected  void updateUI(){

        findViewById(R.id.getStartedText).setVisibility(View.INVISIBLE);
        ((TextView) findViewById(R.id.eventName)).setText(currentEvent.getEventTitle());
        DateTime currentDate = new DateTime();
        DateTime currentDateTime0 = new DateTime(currentDate.getYear(), currentDate.getMonthOfYear(), currentDate.getDayOfMonth(), 0, 0);
        int remainingDays = getDayDifference(currentDateTime0, currentEvent.getDate());

        ((ImageView) findViewById(R.id.background)).setImageBitmap(currentEvent.getBackGround());


        Log.i("remainingDays", String.valueOf(getDayDifference(currentDateTime0, currentEvent.getDate())));
        Log.i("remainingDays", String.valueOf(remainingDays));
        // Set remaining days
        if (remainingDays < 0){
            ((TextView) findViewById(R.id.remainingOrHasPassed)).setText(R.string.hasPassed);
        } else {
            ((TextView) findViewById(R.id.remainingOrHasPassed)).setText(R.string.remaining);
        }
        ((TextView) findViewById(R.id.day)).setText(String.valueOf(Math.abs(remainingDays)));
        ((TextView) findViewById(R.id.dayText)).setText(R.string.days);
    }


    private int getDayDifference(DateTime start, DateTime end) {
        Log.i("FirtDay", String.valueOf(start));
        Log.i("LastDay", String.valueOf(end));
        Log.i("daysBetween", String.valueOf(Days.daysBetween(new DateTime(start), new DateTime(end)).getDays()));
        return Days.daysBetween(start, end).getDays();
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
        // Pass any configuration change to the drawer toggle
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    // This will probably not be used as I am going to only use days in the beginning atleast
    private List<String> getDateDifferenceInList(Date from, Date to) {
        List<String> tmp = new ArrayList<>();
        Calendar fromDate=Calendar.getInstance();
        Calendar toDate=Calendar.getInstance();
        fromDate.setTime(from);
        toDate.setTime(to);
        int increment = 0;
        int year,month,day;
        System.out.println(fromDate.getActualMaximum(Calendar.DAY_OF_MONTH));
        if (fromDate.get(Calendar.DAY_OF_MONTH) > toDate.get(Calendar.DAY_OF_MONTH)) {
            increment =fromDate.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        System.out.println("increment"+increment);
        // DAY CALCULATION
        if (increment != 0) {
            day = (toDate.get(Calendar.DAY_OF_MONTH) + increment) - fromDate.get(Calendar.DAY_OF_MONTH);
            increment = 1;
        } else {
            day = toDate.get(Calendar.DAY_OF_MONTH) - fromDate.get(Calendar.DAY_OF_MONTH);
        }

        // MONTH CALCULATION
        if ((fromDate.get(Calendar.MONTH) + increment) > toDate.get(Calendar.MONTH)) {
            month = (toDate.get(Calendar.MONTH) + 12) - (fromDate.get(Calendar.MONTH) + increment);
            increment = 1;
        } else {
            month = (toDate.get(Calendar.MONTH)) - (fromDate.get(Calendar.MONTH) + increment);
            increment = 0;
        }

        // YEAR CALCULATION
        // TODO: I really don't know about this. toDate just seems to get a +1900 somewhere...
        year = toDate.get(Calendar.YEAR)-1900 - (fromDate.get(Calendar.YEAR) + increment);
        Log.i("year", String.valueOf(year));
        Log.i("to", String.valueOf(toDate.get(Calendar.YEAR)));
        Log.i("from", String.valueOf(fromDate.get(Calendar.YEAR)));


        tmp.add(String.valueOf(day));
        tmp.add(String.valueOf(month));
        tmp.add(String.valueOf(year));

        return tmp;
    }

    protected void deleteEvent(){
        if (currentEvent != null) {
            eventNames.remove(currentEvent.getEventTitle());
            myTrackedEventsList.remove(currentEvent);
            currentEvent = null;
            Collections.sort(myTrackedEventsList);
            if (myTrackedEventsList.size() > 0){
                currentEvent = myTrackedEventsList.get(0);
                updateUI();
            }
            updateEventDrawer();
        }

        if (myTrackedEventsList.size() == 0){
            resetUI();
            updateEventDrawer();
            noEventsUI();
            findViewById(R.id.getStartedText).setVisibility(View.VISIBLE);

            ((ImageView) findViewById(R.id.background)).setImageResource(R.mipmap.background);
        }


    }

    private void resetUI() {
        ((EditText) findViewById(R.id.new_title_id)).setHintTextColor(Color.rgb(96, 96, 96));
        ((TextView) findViewById(R.id.new_date_id)).setTextColor(Color.rgb(96, 96, 96));
        newEventTitle = "";
        ((TextView) findViewById(R.id.eventName)).setText(R.string.title_for_event);
        ((TextView) findViewById(R.id.day)).setText("00");
    }

    private void noEventsUI(){
        ((TextView) findViewById(R.id.eventName)).setText("");
        ((TextView) findViewById(R.id.day)).setText("");
        ((TextView) findViewById(R.id.remainingOrHasPassed)).setText("");
        ((TextView) findViewById(R.id.dayText)).setText("");


    }

    private void checkEventsEmpty() {
        if (myTrackedEventsList.size() == 0){
            noEventsUI();
        } else {
            ((TextView) findViewById(R.id.dayText)).setText(R.string.days);
        }
    }

    public void onBackPressed() {
        if (onAddDateScreen){
            resetVariables();
            onAddDateScreen = false;
            AnimUtil.crossfade(findViewById(R.id.content_add_date_id), main_view, getResources().getInteger(android.R.integer.config_mediumAnimTime));
        } else {
            super.onBackPressed();
        }
    }

    private void save() throws IOException{

    }
}
