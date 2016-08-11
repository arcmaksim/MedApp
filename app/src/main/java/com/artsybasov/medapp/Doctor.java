package com.artsybasov.medapp;

import com.artsybasov.medapp.webservice.WebServiceEmulator;

import java.util.ArrayList;

/**
 * Created by MeatBoy on 8/9/2016.
 */
public class Doctor {

    private int mID;
    private String mName;
    private String mPosition;

    public Doctor(int ID, String name, String position) {
        mID = ID;
        mName = name;
        mPosition = position;
    }

    public int getID() {
        return mID;
    }

    public String getName() {
        return mName;
    }

    public String getPosition() {
        return mPosition;
    }

    public ArrayList<AppointmentItem> getAppointments() {
        ArrayList<AppointmentItem> appointments = new ArrayList<>();
        WebServiceEmulator instance = WebServiceEmulator.getInstance();
        for (AppointmentItem appointment : instance.getAppointments()) {
            if (appointment.getDoctorID() == mID) {
                appointments.add(appointment);
            }
        }
        return appointments;
    }
}
