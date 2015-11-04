package com.simonm.bigdaycountdown;

import java.util.Date;
import java.util.Objects;


public class TrackedDate implements Comparable{

    public TrackedDate(boolean alert, String eventTitle, Date date) {
        this.alert = alert;
        this.eventTitle = eventTitle;
        this.date = date;
    }

    protected String eventTitle;
    protected Date date;
    protected boolean alert;
    // Background..

    public Date getDate() {
        return date;
    }


    //TODO: Test this comparison.
    @Override
    public int compareTo(Object another) {
        TrackedDate td = (TrackedDate)another;
        if (this.getDate() == null || td.getDate() == null)
            return 0;
        return this.getDate().compareTo(td.getDate());
    }
}
