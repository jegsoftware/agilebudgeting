package com.example.jonathon.agilebudgeting;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Jonathon on 4/8/2017.
 */
public class ItemTest {

    private TestItemPersister itemPersister;
    private Item testItem;
    private PlanningPeriod period;

    @Before
    public void setUp() {
        itemPersister = new TestItemPersister();
        period = new PlanningPeriod(1,2017);
        testItem = Item.createItem(period,"Test item", 42.00, "Checking", itemPersister);
    }

    @Test
    public void createItemFromScratch() throws Exception {
        assertEquals(1, testItem.getPlanPeriod().getPeriodNumber());
        assertEquals(2017, testItem.getPlanPeriod().getPeriodYear());
        assertEquals("Test item", testItem.getDescription());
        assertEquals(42.00, testItem.getAmount(), 0.00);
        assertEquals("42.00", testItem.getAmountString());
        assertEquals("Checking", testItem.getAccount());
        assertEquals("PlannedItem", testItem.getType());
    }

    @Test
    public void setDescription() throws Exception {
        testItem.setDescription("Another description");
        assertEquals("Another description", testItem.getDescription());
    }

    @Test
    public void setAmount() throws Exception {
        testItem.setAmount(100.5);
        assertEquals(100.5, testItem.getAmount(), 0.00);
        assertEquals("100.50", testItem.getAmountString());
    }

    @Test
    public void setAccount() throws Exception {
        testItem.setAccount("Credit Card");
        assertEquals("Credit Card", testItem.getAccount());
    }

    @Test
    public void setPlanPeriod() throws Exception {
        testItem.setPlanPeriod(new PlanningPeriod(3,2018));
        assertEquals(3, testItem.getPlanPeriod().getPeriodNumber());
        assertEquals(2018, testItem.getPlanPeriod().getPeriodYear());
    }

    @Test
    public void addActualItem() throws Exception {
        ActualItem relatedItem = ActualItem.createActualItem(period,"02/07/2017", "Test description", 22.00, "Checking", itemPersister);
        testItem.addActualItem(relatedItem);
    }

}