package com.example.jonathon.agilebudgeting;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Jonathon on 7/8/2017.
 */
public class CloudPlanPersisterTest {

    private CloudPlanPersister persister;

    @Before
    public void setup() { persister = new CloudPlanPersister(); }
    @Test
    public void persist() throws Exception {
        Plan testPlan = Plan.createPlan(new PlanningPeriod(6,1973), persister);
        persister.persist(testPlan);
        assertTrue(true);
    }

    @Test
    public void populate() throws Exception {
        Plan testPlan = Plan.createPlan(new PlanningPeriod(1,2017), persister);
        assertTrue(testPlan.isPlanningClosed());
        Plan testNonExistentPlan = Plan.createPlan(new PlanningPeriod(42,4242), persister);
        assertTrue(testNonExistentPlan.isPlanningOpen());
    }

}