package com.artsybasov.medapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.artsybasov.medapp.webservice.WebServiceEmulator;

import java.util.ArrayList;

/**
 * Created by MeatBoy on 8/10/2016.
 */
public class AppointmentsFragment extends com.blunderer.materialdesignlibrary.fragments.AFragment implements ListView.OnItemClickListener {

    private TextView mNoEntryView;
    private ListView mAppointmentsList;

    private ArrayList<AppointmentItem> mObjects;
    private AppointmentAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.appointments);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_appointment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNoEntryView = (TextView) view.findViewById(R.id.no_entry_view);
        mAppointmentsList = (ListView) view.findViewById(R.id.appointment_list_view);
        mObjects = WebServiceEmulator.getInstance().getLoggedUser().getAppointments();

        if (mObjects == null || mObjects.size() == 0) {
            setAppointmentsListVisibility(false);
        } else {
            setAppointmentsListVisibility(true);
            mAdapter = new AppointmentAdapter(getActivity(), mObjects);
            mAdapter.setFragment(this);
            mAdapter.setFragmentManager(getFragmentManager());
            mAppointmentsList.setAdapter(mAdapter);
        }
    }

    public void setAppointmentsListVisibility(boolean isVisible) {
        mNoEntryView.setVisibility((!isVisible) ? View.VISIBLE : View.INVISIBLE);
        mAppointmentsList.setVisibility((isVisible) ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
