package com.artsybasov.medapp.webservice;

import com.artsybasov.medapp.AppointmentItem;
import com.artsybasov.medapp.Doctor;
import com.artsybasov.medapp.User;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by MeatBoy on 8/9/2016.
 */
public class WebServiceEmulator {

    private static WebServiceEmulator mInstance;
    private ArrayList<User> mUsers;
    private ArrayList<Doctor> mDoctors;
    private ArrayList<AppointmentItem> mAppointments;
    private User mLoggedUser;

    private WebServiceEmulator() {
        mUsers = new ArrayList<>();
        mUsers.add(new User(0, "User1", "user1@gmail.com", "1111"));
        mUsers.add(new User(1, "User2", "user2@gmail.com", "2222"));
        mUsers.add(new User(2, "User3", "user3@gmail.com", "3333"));
        mUsers.add(new User(3, "User4", "user4@gmail.com", "4444"));

        mDoctors = new ArrayList<>();
        mDoctors.add(new Doctor(0, "Doctor1", "Терапевт"));
        mDoctors.add(new Doctor(1, "Doctor2", "Хирург"));
        mDoctors.add(new Doctor(2, "Doctor3", "Лор"));

        mAppointments = new ArrayList<>();

        mLoggedUser = mUsers.get(new Random().nextInt(mUsers.size()));
    }

    public static WebServiceEmulator getInstance() {
        if (mInstance == null) {
            mInstance = new WebServiceEmulator();
        }
        return mInstance;
    }

    public User authoriseUser(String login, String password) {
        for (User user : mUsers) {
            if (user.getLogin().equals(login) && user.getPassword().equals(password)) {
                return mLoggedUser = user;
            }
        }
        return null;
    }

    public void logOutUser() {
        mLoggedUser = null;
    }

    public ArrayList<User> getUsers() {
        return mUsers;
    }

    public ArrayList<Doctor> getDoctors() {
        return mDoctors;
    }

    public Doctor getDoctor(int id) {
        for (Doctor doctor : mDoctors) {
            if (doctor.getID() == id) {
                return doctor;
            }
        }
        return null;
    }

    public User gerUser(int id) {
        for (User user : mUsers) {
            if (user.getUserID() == id) {
                return user;
            }
        }
        return null;
    }

    public ArrayList<AppointmentItem> getAppointments() {
        return mAppointments;
    }

    public void addAppointment(AppointmentItem appointment) {
        mAppointments.add(appointment);
    }

    public void removeAppointment(AppointmentItem appointment) {
        mAppointments.remove(appointment);
    }

    public User getLoggedUser() {
        return mLoggedUser;
    }
}
