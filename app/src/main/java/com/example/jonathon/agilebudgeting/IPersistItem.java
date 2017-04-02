package com.example.jonathon.agilebudgeting;

import java.util.List;

/**
 * Created by Jonathon on 3/4/2017.
 */

public interface IPersistItem {
    long persist(Item item);
    void persistRelationship(Item item1, Item item2);
    Item retrieve(long itemId);
    long[] retrieveRelatedItems(Item item);
}
