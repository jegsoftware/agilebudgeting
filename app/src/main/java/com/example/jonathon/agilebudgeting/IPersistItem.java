package com.example.jonathon.agilebudgeting;

import java.util.List;
import java.util.UUID;

/**
 * Created by Jonathon on 3/4/2017.
 */

public interface IPersistItem {
    void persist(Item item);
    void persistRelationship(Item item1, Item item2);
    Item retrieve(UUID itemId);
    UUID[] retrieveRelatedItems(Item item);
}
