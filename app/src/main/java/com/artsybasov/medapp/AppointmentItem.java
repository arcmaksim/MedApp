package com.artsybasov.medapp;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by MeatBoy on 8/9/2016.
 */
public class AppointmentItem {

    private int mUserID;
    private int mDoctorID;
    private DateTime mDate;

    public AppointmentItem(int userID, int doctorID, DateTime date) {
        mUserID = userID;
        mDoctorID = doctorID;
        mDate = date;
    }

    public int getUserID() {
        return mUserID;
    }

    public int getDoctorID() {
        return mDoctorID;
    }

    public DateTime getDate() {
        return mDate;
    }

    public void setUserID(int mUserID) {
        this.mUserID = mUserID;
    }
}
