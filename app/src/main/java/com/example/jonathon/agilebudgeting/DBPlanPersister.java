package com.example.jonathon.agilebudgeting;

/**
 * Created by Jonathon on 3/4/2017.
 */

public class DBPlanPersister implements IPersistPlan {
    @Override
    public void persist(Plan plan) {

    }

    @Override
    public Plan retrieve(PlanningPeriod period) {
        return Plan.createPlan(period);
    }
}
