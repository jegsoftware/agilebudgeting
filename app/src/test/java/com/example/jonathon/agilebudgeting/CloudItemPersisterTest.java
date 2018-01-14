package com.example.jonathon.agilebudgeting;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by Jonathon on 7/8/2017.
 */
public class CloudItemPersisterTest {
    private CloudItemPersister persister;

    @Before
    public void setup() { persister = new CloudItemPersister(); }

    @Test
    public void persist() throws Exception {
        ActualItem testActual = ActualItem.createActualItem(
                                    new PlanningPeriod(1,1982),
                                    "01/01/1982",
                                    "Test actual item",
                                    42.00,
                                    "Checking",
                                    persister);
        testActual.persist();
        assertTrue(true);
    }

    @Test
    public void persistRelationship() throws Exception {
        Item testPlanned = Item.createItem(
                                    new PlanningPeriod(20, 2017),
                                    "Test planned item for two actuals",
                                    100.00,
                                    "Checking",
                                    persister);
        testPlanned.persist();
        ActualItem testActual1 = ActualItem.createActualItem(
                                    new PlanningPeriod(20, 2017),
                                    "10/20/2017",
                                    "First test actual item",
                                    50.00,
                                    "Checking",
                                    persister);
        testActual1.persist();
        ActualItem testActual2 = ActualItem.createActualItem(
                                    new PlanningPeriod(20, 2017),
                                    "10/20/2017",
                                    "Second test actual item",
                                    50.00,
                                    "Checking",
                                    persister);
        testActual2.persist();
        testPlanned.addActualItem(testActual1);
        testPlanned.addActualItem(testActual2);
    }

    @Test
    public void retrieve() throws Exception {
        Item testPlanned = Item.createItem(UUID.fromString("206ed56e-8829-4d41-b1dd-321664f30f31"), persister);
        assertEquals(testPlanned.getAccount(), "Checking");
        assertEquals(testPlanned.getAmount(), 100.00, 0.00);
        assertEquals("PlannedItem", testPlanned.getType());
        ActualItem testActual = ActualItem.createActualItem(UUID.fromString("afc6ed6f-4e6d-4ac2-bcd5-78340eb83764"), persister);
        assertTrue(testActual.hasMatch(testPlanned));
    }

    @Test
    public void retrieveRelatedItems() throws Exception {
        Item testPlanned = Item.createItem(UUID.fromString("206ed56e-8829-4d41-b1dd-321664f30f31"), persister);
        ActualItem testActual = ActualItem.createActualItem(UUID.fromString("afc6ed6f-4e6d-4ac2-bcd5-78340eb83764"), persister);
        assertTrue(testActual.hasMatch(testPlanned));
    }

}