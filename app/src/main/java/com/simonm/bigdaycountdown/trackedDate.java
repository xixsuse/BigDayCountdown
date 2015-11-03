package com.simonm.bigdaycountdown;

import java.util.Date;


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


    // Why does this not satisfy the Comparable?
    public int compareTo(TrackedDate another) {
        if (this.getDate() == null || another.getDate() == null)
            return 0;
        return this.getDate().compareTo(another.getDate());
    }

}
