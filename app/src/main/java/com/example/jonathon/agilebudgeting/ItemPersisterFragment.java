package com.example.jonathon.agilebudgeting;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.UUID;

/**
 * Created by jonathon on 1/24/18.
 */

public class ItemPersisterFragment extends Fragment {
    public static final String TAG = "PlanPersisterFragment";

    private IDataCallback<Item> callback;
    private Boolean useCloudData;
    private ItemSaveTask saveTask;
    private ItemLoadTask loadTask;

    public static ItemPersisterFragment getInstance(FragmentManager fragmentManager, boolean useCloud) {
        ItemPersisterFragment fragment = new ItemPersisterFragment();
        fragment.useCloudData = useCloud;
        fragmentManager.beginTransaction().add(fragment, TAG).commit();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (IDataCallback<Item>) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    public void saveItem(Item item) {
        if (callback != null) {
            saveTask = new ItemSaveTask();
            saveTask.execute(item);
        }
    }

    public void loadItem(UUID uuid) {
        if (callback != null) {
            loadTask = new ItemLoadTask();
            loadTask.execute(uuid);
        }
    }

    private class ItemSaveTask extends AsyncTask<Item, Void, Item> {

        @Override
        protected Item doInBackground(Item... items) {
            IPersistItem persister;
            if (useCloudData) {
                persister = new CloudItemPersister();
            } else {
                persister = new DBItemPersister();
            }
            persister.persist(items[0]);
            return items[0];
        }

        protected void onPostExecute(Item itemToSave) {
            callback.dataSaved(itemToSave);
        }
    }

    private class ItemLoadTask extends AsyncTask<UUID, Void, Item> {

        @Override
        protected Item doInBackground(UUID... uuids) {
            IPersistItem persister;
            if (useCloudData) {
                persister = new CloudItemPersister();
            } else {
                persister = new DBItemPersister();
            }
            return Item.createItem(uuids[0], persister);
        }

        protected void onPostExecute(Item loadedItem) {
            callback.dataLoaded(loadedItem);
        }

    }

}
