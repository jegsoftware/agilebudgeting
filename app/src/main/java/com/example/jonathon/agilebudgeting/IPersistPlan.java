package com.example.jonathon.agilebudgeting;

/**
 * Created by Jonathon on 3/4/2017.
 */

public interface IPersistPlan {
    void persist(Plan plan);
    Plan populate(Plan newPlan, PlanningPeriod period);
}
