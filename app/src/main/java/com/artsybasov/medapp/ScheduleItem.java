package com.artsybasov.medapp;

import org.joda.time.DateTime;

/**
 * Created by MeatBoy on 8/8/2016.
 */
public class ScheduleItem {

    private DateTime mDate;
    private int mReservedBy;

    public ScheduleItem(DateTime time, int reservedBy) {
        mDate = time;
        mReservedBy = reservedBy;
    }

    public DateTime getTime() {
        return mDate;
    }

    public void setTime(DateTime date) {
        this.mDate = date;
    }

    public int getReservedBy() {
        return mReservedBy;
    }

    public void setReservedBy(int mReservedBy) {
        this.mReservedBy = mReservedBy;
    }
}
