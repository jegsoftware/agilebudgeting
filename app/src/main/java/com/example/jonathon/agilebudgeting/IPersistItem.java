package com.example.jonathon.agilebudgeting;

/**
 * Created by Jonathon on 3/4/2017.
 */

public interface IPersistItem {
    long persist(Item item);
    Item retrieve(long itemId);
}
