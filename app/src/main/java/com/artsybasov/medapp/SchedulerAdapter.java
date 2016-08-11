package com.artsybasov.medapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.artsybasov.medapp.webservice.WebServiceEmulator;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Created by MeatBoy on 8/8/2016.
 */
public class SchedulerAdapter extends ArrayAdapter<ScheduleItem> {

    private ArrayList<ScheduleItem> mObjects;
    private Activity mContext;

    public static final String[] MONTH_NAMES = new String[] {
            "января", "февраля", "марта", "апреля", "мая", "июня",
            "июля", "августа", "сентября", "октября", "ноября",
            "декабря"
    };

    public SchedulerAdapter(Activity context, ArrayList<ScheduleItem> objects) {
        super(context, R.layout.listview_row, objects);
        mObjects = objects;
        mContext = context;
        this.setNotifyOnChange(true);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            rowView = inflater.inflate(R.layout.listview_row, null, true);
            holder = new ViewHolder();
            holder.mTimeView = (TextView) rowView.findViewById(R.id.time);
            holder.mFlagView = (TextView) rowView.findViewById(R.id.flag);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        if (holder.isEnabled != isEnabled(position)) {
            holder.isEnabled = isEnabled(position);
            if (holder.isEnabled) {
                rowView.setBackgroundResource(R.color.white_transparent);
            } else {
                rowView.setBackgroundResource(R.color.grey_400);
            }
        }

        StringBuilder builder = new StringBuilder();
        DateTime temp = mObjects.get(position).getTime();
        builder.append(temp.getHourOfDay())
                .append(":");
        if (temp.getMinuteOfHour() == 0) {
            builder.append("00");
        } else {
            builder.append(temp.getMinuteOfHour());
        }
        holder.mTimeView.setText(builder);

        String info = "";
        if (mObjects.get(position).getReservedBy() != -1) {
            info = (mObjects.get(position).getReservedBy() == WebServiceEmulator.getInstance().getLoggedUser().getUserID()) ? "Моя запись" : "Занято";
        } else {
            info = "Свободно";
        }
        holder.mFlagView.setText(info);

        return rowView;
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public ScheduleItem getItem(int position) {
        return mObjects.get(position);
    }

    @Override
    public boolean isEnabled(int position) {
        return mObjects.get(position).getReservedBy() == -1;
    }

    public void updateData(ArrayList<ScheduleItem> objects) {
        mObjects = objects;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        public TextView mTimeView;
        public TextView mFlagView;
        public boolean isEnabled = true;
    }

}
