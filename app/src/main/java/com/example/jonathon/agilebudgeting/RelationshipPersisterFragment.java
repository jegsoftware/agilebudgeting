package com.example.jonathon.agilebudgeting;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by jonathon on 1/24/18.
 */

public class RelationshipPersisterFragment extends Fragment {
    public static final String TAG = "RelationshipPersisterFragment";
    public static final String ACTUAL_ITEM = "ActualItem";
    public static final String PLANNED_ITEMS = "PlannedItems";

    private IDataCallback<Item> callback;
    private Boolean useCloudData;
    private RelationshipSaveTask saveTask;
    private RelationshipLoadTask loadTask;

    public static RelationshipPersisterFragment getInstance(FragmentManager fragmentManager, boolean useCloud) {
        RelationshipPersisterFragment fragment = new RelationshipPersisterFragment();
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

    public void saveRelationships(Item actualItem, ArrayList<Item> plannedItems) {
        if (callback != null) {
            Bundle args = new Bundle();
            args.putSerializable(ACTUAL_ITEM, actualItem);
            args.putSerializable(PLANNED_ITEMS, plannedItems);
            saveTask = new RelationshipSaveTask();
            saveTask.execute(args);
        }
    }

    public void loadRelationship(UUID uuid) {
        if (callback != null) {
            loadTask = new RelationshipLoadTask();
            loadTask.execute(uuid);
        }
    }

    private class RelationshipSaveTask extends AsyncTask<Bundle, Void, Item> {

        @Override
        protected Item doInBackground(Bundle... bundles) {
            IPersistItem persister;
            if (useCloudData) {
                persister = new CloudItemPersister();
            } else {
                persister = new DBItemPersister();
            }
            Item actualItem = (Item) bundles[0].getSerializable(ACTUAL_ITEM);
            ArrayList<Item> plannedItems = (ArrayList<Item>) bundles[0].getSerializable(PLANNED_ITEMS);
            int itemCount = plannedItems.size();
            for (int i=0; i < itemCount; i++) {
                actualItem.addRelatedItem(plannedItems.get(i));
                plannedItems.get(i).addRelatedItem(actualItem);
                persister.persistRelationship(plannedItems.get(i), actualItem);
            }
            return actualItem;
        }

        protected void onPostExecute(Item result) {
            callback.dataSaved(result);
        }

    }

    private class RelationshipLoadTask extends AsyncTask<UUID, Void, Item> {

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
