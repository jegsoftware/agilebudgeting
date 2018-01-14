package com.example.jonathon.agilebudgeting;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Jonathon on 4/8/2017.
 */
public class ItemTest {

    private TestItemPersister itemPersister;
    private Item testPlannedItem;
    private PlanningPeriod period;

    @Before
    public void setUp() {
        itemPersister = new TestItemPersister();
        period = new PlanningPeriod(1,2017);
        testPlannedItem = Item.createPlannedItem(period,"Test item", 42.00, "Checking", itemPersister);
    }

    @Test
    public void createPlannedItemFromScratch() throws Exception {
        assertEquals(1, testPlannedItem.getPlanPeriod().getPeriodNumber());
        assertEquals(2017, testPlannedItem.getPlanPeriod().getPeriodYear());
        assertEquals("Test item", testPlannedItem.getDescription());
        assertEquals(42.00, testPlannedItem.getAmount(), 0.00);
        assertEquals("42.00", testPlannedItem.getAmountString());
        assertEquals("Checking", testPlannedItem.getAccount());
        assertEquals("PlannedItem", testPlannedItem.getType());
    }

    @Test
    public void setDescription() throws Exception {
        testPlannedItem.setDescription("Another description");
        assertEquals("Another description", testPlannedItem.getDescription());
    }

    @Test
    public void setAmount() throws Exception {
        testPlannedItem.setAmount(100.5);
        assertEquals(100.5, testPlannedItem.getAmount(), 0.00);
        assertEquals("100.50", testPlannedItem.getAmountString());
    }

    @Test
    public void setAccount() throws Exception {
        testPlannedItem.setAccount("Credit Card");
        assertEquals("Credit Card", testPlannedItem.getAccount());
    }

    @Test
    public void setPlanPeriod() throws Exception {
        testPlannedItem.setPlanPeriod(new PlanningPeriod(3,2018));
        assertEquals(3, testPlannedItem.getPlanPeriod().getPeriodNumber());
        assertEquals(2018, testPlannedItem.getPlanPeriod().getPeriodYear());
    }

    @Test
    public void addActualItem() throws Exception {
        ActualItem relatedItem = ActualItem.createActualItem(period,"02/07/2017", "Test description", 22.00, "Checking", itemPersister);
        testPlannedItem.addActualItem(relatedItem);
    }

}