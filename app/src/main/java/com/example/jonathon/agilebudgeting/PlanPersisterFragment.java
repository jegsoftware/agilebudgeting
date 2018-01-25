package com.example.jonathon.agilebudgeting;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by jonathon on 1/24/18.
 */

public class PlanPersisterFragment extends Fragment {
    public static final String TAG = "PlanPersisterFragment";
    private static final String CLOUD_DATA_KEY = "UseCLoudData";
    private static final String PERIOD_KEY = "PlanningPeriod";

    private IDataCallback<Plan> callback;
    private IPersistPlan persister;
    private PlanningPeriod planPeriod;

    public static PlanPersisterFragment getInstance(FragmentManager fragmentManager, boolean useCloudData) {
        PlanPersisterFragment fragment = new PlanPersisterFragment();
        Bundle args = new Bundle();
        args.putBoolean(CLOUD_DATA_KEY, useCloudData);
        fragment.setArguments(args);
        fragmentManager.beginTransaction().add(fragment, TAG).commit();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments().getBoolean(CLOUD_DATA_KEY)) {
            persister = new CloudPlanPersister();
        } else {
            persister = new DBPlanPersister();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (IDataCallback<Plan>) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    public void savePlan(Plan plan) {
        if (callback != null) {
            persister.persist(plan);
            callback.dataSaved(plan);
        }
    }

    public void loadPlan(PlanningPeriod period) {
        if (callback != null) {
            Plan loadedPlan = Plan.createPlan(period, persister);
            callback.dataLoaded(loadedPlan);
        }
    }

}
