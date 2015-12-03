package com.simonm.bigdaycountdown;

import android.graphics.Bitmap;
import android.util.Log;

import com.orm.SugarContext;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.io.File;
import java.util.Date;
import org.joda.time.DateTime;


public class TrackedEvent extends SugarRecord implements Comparable{

    protected void onCreate(){
    }

    public TrackedEvent(){

    }

    public TrackedEvent(boolean alert, String eventTitle, DateTime date, String imagePath) {
        this.imagePath = imagePath;
        this.alert = alert;
        this.eventTitle = eventTitle;
        this.date = date;
    }


    @Ignore
    protected DateTime date;

    protected String eventTitle;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    protected String imagePath;
    protected boolean alert;
    protected Date dateToSave;

    public void saveDate(){
        dateToSave = date.toDate();
        Log.i("datetosave", String.valueOf(dateToSave));
    }

    public void updateDate(){
        date = new DateTime(dateToSave);
    }



    public boolean isAlert() {
        return alert;
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
