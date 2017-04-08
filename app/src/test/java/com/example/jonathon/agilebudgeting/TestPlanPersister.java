package com.example.jonathon.agilebudgeting;

import java.util.ArrayList;

/**
 * Created by Jonathon on 4/8/2017.
 */

public class TestPlanPersister implements IPersistPlan {
    private PlanStatus planningStatus;
    private PlanStatus actualsStatus;
    private PlanningPeriod period;

    @Override
    public void persist(Plan plan) {
        period = plan.getPeriod();
        planningStatus = plan.isPlanningOpen() ? PlanStatus.OPEN : PlanStatus.CLOSED;
        actualsStatus = plan.isActualsOpen() ? PlanStatus.OPEN : PlanStatus.CLOSED;
    }

    @Override
    public Plan populate(Plan newPlan, PlanningPeriod period) {
        newPlan.setPeriod(period);
        if(PlanStatus.CLOSED == planningStatus) {
            newPlan.closePlanning();
        }
        if(PlanStatus.CLOSED == actualsStatus) {
            newPlan.closeActuals();
        }

        return newPlan;
    }
}
