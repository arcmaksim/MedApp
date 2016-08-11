package com.artsybasov.medapp;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.artsybasov.medapp.webservice.WebServiceEmulator;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.ImageButton;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Created by MeatBoy on 8/9/2016.
 */
public class AppointmentAdapter extends ArrayAdapter<AppointmentItem> {

    private ArrayList<AppointmentItem> mObjects;
    private Activity mContext;
    private AppointmentsFragment mAppointmentFragment;
    private FragmentManager mFragmentManager;

    private static class ViewHolder {
        public TextView mDoctorView;
        public TextView mDateView;
        public ImageButton mCancelButton;
        public View mView;
    }

    public AppointmentAdapter(Activity context, ArrayList<AppointmentItem> objects) {
        super(context, R.layout.listview_row, objects);
        mObjects = objects;
        mContext = context;
        sortContent();
    }

    private void sortContent() {
        for (int i = mObjects.size() - 1; i > 0; i--) {
            DateTime date = mObjects.get(i).getDate();
            for (int j = i - 1; j > -1; j--) {
                if (mObjects.get(j).getDate().isAfter(date)) {
                    AppointmentItem temp = mObjects.get(j);
                    mObjects.set(j, mObjects.get(i));
                    mObjects.set(i, temp);
                }
            }
        }
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {

        final ViewHolder holder;
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            rowView = inflater.inflate(R.layout.listview_appointment, null, true);
            holder = new ViewHolder();
            holder.mView = rowView.findViewById(R.id.appointment_view);
            holder.mDoctorView = (TextView) rowView.findViewById(R.id.doctor_view);
            holder.mDateView = (TextView) rowView.findViewById(R.id.date_view);
            holder.mCancelButton = (ImageButton) rowView.findViewById(R.id.cancel_button);
            holder.mCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Dialog.Builder builder = new SimpleDialog.Builder(com.rey.material.R.style.Material_App_Dialog_Simple_Light) {
                        @Override
                        public void onPositiveActionClicked(DialogFragment fragment) {
                            super.onPositiveActionClicked(fragment);
                            WebServiceEmulator.getInstance().removeAppointment(mObjects.get(position));
                            mObjects.remove(position);
                            Animation mAnim = AnimationUtils.loadAnimation(mContext, R.anim.delete_anim);
                            mAnim.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    holder.mView.clearAnimation();
                                    notifyDataSetChanged();
                                    if (mObjects.size() == 0) {
                                        mAppointmentFragment.setAppointmentsListVisibility(false);
                                    }
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {
                                }
                            });
                            holder.mView.setAnimation(mAnim);
                            holder.mView.startAnimation(mAnim);
                        }

                        @Override
                        public void onNegativeActionClicked(DialogFragment fragment) {
                            super.onNegativeActionClicked(fragment);
                        }
                    };

                    ((SimpleDialog.Builder) builder).message("Отменить запись?")
                            .title("Отмена записи")
                            .positiveAction("Да")
                            .negativeAction("Нет");
                    DialogFragment fragment = DialogFragment.newInstance(builder);
                    fragment.show(mFragmentManager, null);
                }
            });
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        Doctor doctor = WebServiceEmulator.getInstance().getDoctor(mObjects.get(position).getDoctorID());
        StringBuilder builder = new StringBuilder();
        builder.append(doctor.getName())
                .append(" - ")
                .append(doctor.getPosition());
        holder.mDoctorView.setText(builder);
        builder.setLength(0);

        DateTime date = mObjects.get(position).getDate();
        builder.append(date.dayOfMonth().get())
                .append(" ")
                .append(SchedulerAdapter.MONTH_NAMES[date.monthOfYear().get() - 1])
                .append(" - ")
                .append(date.getHourOfDay())
                .append(":");
        if (date.minuteOfHour().get() == 0) {
            builder.append("00");
        } else {
            builder.append(date.minuteOfHour().get());
        }
        holder.mDateView.setText(builder);
        return rowView;
    }

    public void setFragment(AppointmentsFragment fragment) {
        mAppointmentFragment = fragment;
    }

    public void setFragmentManager(FragmentManager mFragmentManager) {
        this.mFragmentManager = mFragmentManager;
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

}
