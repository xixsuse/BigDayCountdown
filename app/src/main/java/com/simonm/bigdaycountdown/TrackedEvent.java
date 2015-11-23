package com.simonm.bigdaycountdown;

import java.io.File;
import java.util.Date;
import org.joda.time.DateTime;


public class TrackedEvent implements Comparable{

    public TrackedEvent(boolean alert, String eventTitle, DateTime date, File backGround) {
        this.alert = alert;
        this.eventTitle = eventTitle;
        this.date = date;
        this.backGround = backGround;
    }


    protected File backGround;
    protected String eventTitle;
    protected DateTime date;
    protected boolean alert;
    // Background..


    public boolean isAlert() {
        return alert;
    }

    public File getBackGround() {
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
