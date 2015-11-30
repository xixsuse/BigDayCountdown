package com.simonm.bigdaycountdown;

import android.graphics.Bitmap;
import android.util.Log;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.io.File;
import java.util.Date;
import org.joda.time.DateTime;


public class TrackedEvent extends SugarRecord implements Comparable{

    public TrackedEvent(){

    }

    public TrackedEvent(boolean alert, String eventTitle, DateTime date, Bitmap backGround) {
        this.alert = alert;
        this.eventTitle = eventTitle;
        this.date = date;
        this.backGround = backGround;
    }


    @Ignore
    protected Bitmap backGround;
    @Ignore
    protected DateTime date;

    protected String eventTitle;
    protected boolean alert;
    protected Date dateToSave;
    protected byte[] imageToSave;

    public void saveDate(){
        dateToSave = date.toDate();
        Log.i("datetosave", String.valueOf(dateToSave));
    }

    public void updateDate(){
        date = new DateTime(dateToSave);
    }

    public void saveImage(){
        imageToSave = Utilities.getBytes(backGround);
    }

    public void updateImage(){
        backGround = Utilities.getImage(imageToSave);
    }



    public boolean isAlert() {
        return alert;
    }

    public Bitmap getBackGround() {
        return backGround;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public DateTime getDate() {
        return date;
    }


    //TODO: Test this comparison.
    @Override
    public int compareTo(Object another) {
        TrackedEvent td = (TrackedEvent)another;
        if (this.getDate() == null || td.getDate() == null)
            return 0;
        return this.getDate().compareTo(td.getDate());
    }
}
