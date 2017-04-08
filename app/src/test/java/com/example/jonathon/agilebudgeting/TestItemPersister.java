package com.example.jonathon.agilebudgeting;

import java.util.UUID;

/**
 * Created by Jonathon on 4/8/2017.
 */

public class TestItemPersister implements IPersistItem {
    @Override
    public void persist(Item item) {

    }

    @Override
    public void persistRelationship(Item item1, Item item2) {

    }

    @Override
    public Item retrieve(UUID itemId) {
        return null;
    }

    @Override
    public UUID[] retrieveRelatedItems(Item item) {
        return new UUID[0];
    }
}
