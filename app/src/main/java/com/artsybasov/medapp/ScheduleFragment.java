package com.artsybasov.medapp;

/**
 * Created by MeatBoy on 8/8/2016.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.artsybasov.medapp.webservice.WebServiceEmulator;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.Spinner;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment
        extends com.blunderer.materialdesignlibrary.fragments.AFragment implements Spinner.OnItemSelectedListener, View.OnClickListener, ListView.OnItemClickListener {

    private Spinner mSpinner;
    private ImageButton mPreviousDayButton;
    private ImageButton mNextDayButton;
    private TextView mCurrentDayView;
    private ListView mScheduleList;
    private TextView mNoScheduleView;

    private DateTime mLauchDay = DateTime.now();
    private DateTime mCurrentDay = mLauchDay;
    private User mUser;
    private WebServiceEmulator mInstance;
    private int mSelectedDoctor;
    private int mSelectedDoctorID;

    private SchedulerAdapter mAdapter;
    private ArrayList<Integer> mDoctorIDList;
    private ArrayList<ScheduleItem> mObjects;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.schedule);
        mInstance = WebServiceEmulator.getInstance();
        mUser = mInstance.getLoggedUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSpinner = (Spinner) view.findViewById(R.id.spinner);
        mSpinner.setOnItemSelectedListener(this);

        mDoctorIDList = new ArrayList<>();
        List<String> categories = new ArrayList<>();
        for (Doctor doctor : mInstance.getDoctors()) {
            categories.add(doctor.getName() + " - " + doctor.getPosition());
            mDoctorIDList.add(doctor.getID());
        }
        mSelectedDoctorID = mDoctorIDList.get(0);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity().getBaseContext(), R.layout.spinner_simple, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(dataAdapter);

        mNoScheduleView = (TextView) view.findViewById(R.id.no_schedule_view);

        mPreviousDayButton = (ImageButton) view.findViewById(R.id.previous_day);
        mPreviousDayButton.setOnClickListener(this);
        if (mCurrentDay.getDayOfYear() == mLauchDay.getDayOfYear()) {
            mPreviousDayButton.setVisibility(View.INVISIBLE);
        } else {
            mPreviousDayButton.setVisibility(View.VISIBLE);
        }

        mNextDayButton = (ImageButton) view.findViewById(R.id.next_day);
        mNextDayButton.setOnClickListener(this);
        DateTime tempDate = new DateTime(mLauchDay.getYear(),
                mLauchDay.getMonthOfYear(),
                mLauchDay.getDayOfMonth(),
                mCurrentDay.getHourOfDay(),
                mCurrentDay.getMinuteOfHour());
        if (Days.daysBetween(tempDate, mCurrentDay).getDays() < 7) {
            mNextDayButton.setVisibility(View.VISIBLE);
        }else {
            mNextDayButton.setVisibility(View.INVISIBLE);
        }

        mCurrentDayView = (TextView) view.findViewById(R.id.current_day);
        mCurrentDayView.setOnClickListener(this);

        int temp = (mCurrentDay.getMonthOfYear() != 1) ? mCurrentDay.getMonthOfYear() - 1 : 11;
        mCurrentDayView.setText(mCurrentDay.dayOfMonth().get() + " " + SchedulerAdapter.MONTH_NAMES[temp]);

        mScheduleList = (ListView) view.findViewById(R.id.schedule_list_view);

        mObjects = generateDataset(mInstance.getDoctor(mSelectedDoctorID).getAppointments(), getPreviousOrCurrentTime(mCurrentDay, mLauchDay));
        mAdapter = new SchedulerAdapter(getActivity(), mObjects);
        mScheduleList.setAdapter(mAdapter);
        mScheduleList.setOnItemClickListener(this);

        if (mObjects.size() == 0) {
            mNoScheduleView.setVisibility(View.VISIBLE);
            mScheduleList.setVisibility(View.INVISIBLE);
        } else {
            mScheduleList.setVisibility(View.VISIBLE);
            mNoScheduleView.setVisibility(View.INVISIBLE);

        }
    }

    private ArrayList<ScheduleItem> generateDataset(ArrayList<AppointmentItem> appointments, DateTime selectedDate) {
        ArrayList<ScheduleItem> schedule = new ArrayList<>();

        DateTime currentTime = new DateTime(selectedDate.year().get(),
                selectedDate.monthOfYear().get(),
                selectedDate.getDayOfMonth(),
                10,
                30,
                00);
        if (selectedDate.getHourOfDay() < 16) {
            do {
                currentTime = currentTime.plusMinutes(30);
                if (currentTime.isAfter(selectedDate) || currentTime.equals(selectedDate)) {
                    boolean flag = false;
                    for (AppointmentItem appointment : appointments) {
                        if (appointment.getDate().equals(currentTime)) {
                            schedule.add(new ScheduleItem(currentTime, appointment.getUserID()));
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        schedule.add(new ScheduleItem(currentTime, -1));
                    }
                }
            } while (currentTime.getHourOfDay() < 16);
        }
        return schedule;
    }

    private DateTime getPreviousOrCurrentTime(DateTime current, DateTime mLauchDay) {
        if (mCurrentDay.getDayOfYear() == mLauchDay.getDayOfYear()) {
            mCurrentDay = mCurrentDay.minuteOfHour().setCopy(mLauchDay.getMinuteOfHour());
            mCurrentDay = mCurrentDay.hourOfDay().setCopy(mLauchDay.getHourOfDay());
            if (mCurrentDay.getMinuteOfHour() >= 30) {
                mCurrentDay = mCurrentDay.minuteOfHour().setCopy(59);
            } else {
                mCurrentDay = mCurrentDay.minuteOfHour().setCopy(30);
            }
            mCurrentDay = mCurrentDay.plusHours(1);
        } else {
            mCurrentDay = mCurrentDay.minuteOfHour().setCopy(59);
            mCurrentDay = mCurrentDay.hourOfDay().setCopy(10);
        }
        mCurrentDay = mCurrentDay.secondOfMinute().setCopy(0);
        return mCurrentDay;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.previous_day:
                if (mCurrentDay.getDayOfYear() != mLauchDay.getDayOfYear()) {
                    mNextDayButton.setVisibility(View.VISIBLE);
                    mCurrentDay = mCurrentDay.minusDays(1);
                    if (mCurrentDay.getDayOfYear() == mLauchDay.getDayOfYear()) {
                        mCurrentDay = mCurrentDay.minuteOfHour().setCopy(mLauchDay.getMinuteOfHour());
                        mCurrentDay = mCurrentDay.hourOfDay().setCopy(mLauchDay.getHourOfDay());
                        if (mCurrentDay.getMinuteOfHour() >= 30) {
                            mCurrentDay = mCurrentDay.minuteOfHour().setCopy(59);
                        } else {
                            mCurrentDay = mCurrentDay.minuteOfHour().setCopy(30);
                        }
                        mCurrentDay = mCurrentDay.plusHours(1);
                        mCurrentDay = mCurrentDay.secondOfMinute().setCopy(0);
                        mPreviousDayButton.setVisibility(View.INVISIBLE);
                    } else {
                        mPreviousDayButton.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.next_day:
                DateTime tempDate = new DateTime(mLauchDay.getYear(),
                        mLauchDay.getMonthOfYear(),
                        mLauchDay.getDayOfMonth(),
                        mCurrentDay.getHourOfDay(),
                        mCurrentDay.getMinuteOfHour());
                if (Days.daysBetween(tempDate, mCurrentDay).getDays() < 7) {
                    mCurrentDay = mCurrentDay.plusDays(1);
                    mCurrentDay = mCurrentDay.minuteOfHour().setCopy(59);
                    mCurrentDay = mCurrentDay.hourOfDay().setCopy(10);
                    mCurrentDay = mCurrentDay.secondOfMinute().setCopy(0);
                    mPreviousDayButton.setVisibility(View.VISIBLE);
                }
                if (Days.daysBetween(tempDate, mCurrentDay).getDays() < 7) {
                    mNextDayButton.setVisibility(View.VISIBLE);
                } else {
                    mNextDayButton.setVisibility(View.INVISIBLE);
                }
                break;
        }
        int temp = (mCurrentDay.getMonthOfYear() != 1) ? mCurrentDay.getMonthOfYear() - 1 : 11;
        mCurrentDayView.setText(mCurrentDay.dayOfMonth().get() + " " + SchedulerAdapter.MONTH_NAMES[temp]);
        mObjects = generateDataset(mInstance.getDoctor(mDoctorIDList.get(mSelectedDoctor)).getAppointments(), mCurrentDay);
        if (mObjects.size() == 0) {
            mScheduleList.setVisibility(View.INVISIBLE);
            mNoScheduleView.setVisibility(View.VISIBLE);
        } else {
            mScheduleList.setVisibility(View.VISIBLE);
            mNoScheduleView.setVisibility(View.INVISIBLE);
        }
        mAdapter.updateData(mObjects);
    }

    @Override
    public void onItemSelected(Spinner parent, View view, int position, long id) {
        mSelectedDoctor = position;
        mSelectedDoctorID = mDoctorIDList.get(mSelectedDoctor);
        if (mAdapter != null) {
            mObjects = generateDataset(mInstance.getDoctor(mSelectedDoctorID).getAppointments(), mCurrentDay);
            mAdapter.updateData(mObjects);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        if (mAdapter.isEnabled(position)) {
            Dialog.Builder builder = new SimpleDialog.Builder(com.rey.material.R.style.Material_App_Dialog_Simple_Light) {
                @Override
                public void onPositiveActionClicked(DialogFragment fragment) {
                    super.onPositiveActionClicked(fragment);
                    mObjects.get(position).setReservedBy(mUser.getUserID());
                    mInstance.addAppointment(new AppointmentItem(mUser.getUserID(), mInstance.getDoctor(mSelectedDoctorID).getID(), mObjects.get(position).getTime()));
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onNegativeActionClicked(DialogFragment fragment) {
                    super.onNegativeActionClicked(fragment);
                }
            };

            Doctor doctor = mInstance.getDoctor(mDoctorIDList.get(mSelectedDoctor));
            DateTime time = mObjects.get(position).getTime();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Записаться на прием к врачу ")
                    .append(doctor.getName())
                    .append(" (")
                    .append(doctor.getPosition())
                    .append(") на ")
                    .append(time.dayOfMonth().get())
                    .append(" ")
                    .append(SchedulerAdapter.MONTH_NAMES[time.getMonthOfYear() - 1])
                    .append(" в ")
                    .append(time.getHourOfDay())
                    .append(":");
            if (time.minuteOfHour().get() == 0) {
                stringBuilder.append("00");
            } else {
                stringBuilder.append(time.getMinuteOfHour());
            }
            stringBuilder.append("?");

            ((SimpleDialog.Builder) builder).message(stringBuilder)
                    .title("Запись на прием")
                    .positiveAction("Да")
                    .negativeAction("Нет");
            DialogFragment fragment = DialogFragment.newInstance(builder);
            fragment.show(getFragmentManager(), null);
        }
    }

}