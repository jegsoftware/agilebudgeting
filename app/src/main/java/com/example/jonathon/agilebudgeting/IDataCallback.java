package com.example.jonathon.agilebudgeting;

/**
 * Created by jonathon on 1/24/18.
 */

public interface IDataCallback<T> {
    void dataLoaded(T result);
    void dataSaved(T result);
}
