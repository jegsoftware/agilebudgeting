package com.example.jonathon.agilebudgeting;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by jonathon on 1/24/18.
 */

public class PlanPersisterFragment extends Fragment {
    public static final String TAG = "PlanPersisterFragment";

    private IDataCallback<Plan> callback;
    private Boolean useCloudData;
    private PlanSaveTask saveTask;
    private PlanLoadTask loadTask;

    public static PlanPersisterFragment getInstance(FragmentManager fragmentManager, boolean useCloud) {
        PlanPersisterFragment fragment = new PlanPersisterFragment();
        fragment.useCloudData = useCloud;
        fragmentManager.beginTransaction().add(fragment, TAG).commit();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
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
            saveTask = new PlanSaveTask();
            saveTask.execute(plan);
        }
    }

    public void loadPlan(PlanningPeriod period) {
        if (callback != null) {
            loadTask = new PlanLoadTask();
            loadTask.execute(period);
        }
    }

    private class PlanSaveTask extends AsyncTask<Plan, Void, Plan> {

        @Override
        protected Plan doInBackground(Plan... plans) {
            IPersistPlan persister;
            if (useCloudData) {
                persister = new CloudPlanPersister();
            } else {
                persister = new DBPlanPersister();
            }
            persister.persist(plans[0]);
            return plans[0];
        }

        protected void onPostExecute(Plan planToSave) {
            callback.dataSaved(planToSave);
        }

    }

    private class PlanLoadTask extends AsyncTask<PlanningPeriod, Void, Plan> {

        @Override
        protected Plan doInBackground(PlanningPeriod... planningPeriods) {
            IPersistPlan persister;
            if (useCloudData) {
                persister = new CloudPlanPersister();
            } else {
                persister = new DBPlanPersister();
            }
            return Plan.createPlan(planningPeriods[0], persister);
        }

        protected void onPostExecute(Plan loadedPlan) {
            callback.dataLoaded(loadedPlan);
        }
    }

}
