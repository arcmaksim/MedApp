package com.artsybasov.medapp;

import com.artsybasov.medapp.webservice.WebServiceEmulator;

import java.util.ArrayList;

/**
 * Created by MeatBoy on 8/9/2016.
 */
public class User {

    private int mID;
    private String mName;
    private String mLogin;
    private String mPassword;

    public User(int userID, String name, String login, String password) {
        mID = userID;
        mName = name;
        mLogin = login;
        mPassword = password;
    }

    public int getUserID() {
        return mID;
    }

    public String getName() {
        return mName;
    }

    public ArrayList<AppointmentItem> getAppointments() {
        ArrayList<AppointmentItem> appointments = new ArrayList<>();

        for (AppointmentItem appointment : WebServiceEmulator.getInstance().getAppointments()) {
            if (appointment.getUserID() == mID) {
                appointments.add(appointment);
            }
        }

        return appointments;
    }

    public String getLogin() {
        return mLogin;
    }

    public String getPassword() {
        return mPassword;
    }
}
