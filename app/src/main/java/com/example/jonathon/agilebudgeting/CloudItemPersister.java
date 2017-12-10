package com.example.jonathon.agilebudgeting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Jonathon on 7/8/2017.
 */

public class CloudItemPersister implements IPersistItem, Serializable {
    @Override
    public void persist(Item item) {
        JSONObject saveItemObject = new JSONObject();
        try {
            saveItemObject.put("persistenceType", "saveItem");
            JSONObject dataObject = new JSONObject();


            dataObject.put("uuid", item.getItemId());
            dataObject.put("periodNum", item.getPlanPeriod().getPeriodNumber());
            dataObject.put("periodYear", item.getPlanPeriod().getPeriodYear());
            dataObject.put("date", item.getDate());
            dataObject.put("description", item.getDescription());
            dataObject.put("amount", item.getAmountString());
            dataObject.put("account", item.getAccount());
            dataObject.put("type", item.getType());

            saveItemObject.put("data", dataObject);
            CloudCaller.sendJSON(saveItemObject);
        } catch (JSONException e) {

        }

    }

    @Override
    public void persistRelationship(Item item1, Item item2) {
        JSONObject saveItemObject = new JSONObject();
        try {
            saveItemObject.put("persistenceType", "saveItemRelationship");
            JSONObject dataObject = new JSONObject();


            dataObject.put("uuid1", item1.getItemId());
            dataObject.put("uuid2", item2.getItemId());

            saveItemObject.put("data", dataObject);
            CloudCaller.sendJSON(saveItemObject);
        } catch (JSONException e) {

        }
    }

    @Override
    public Item retrieve(UUID itemId) {
        Item retrievedItem = null;
        try {
            JSONObject retrievedJSON = retrieveItemJSON(itemId);

            if (retrievedJSON != null) {
                retrievedItem = new Item();

                int periodNum = retrievedJSON.getInt("periodNum");
                int periodYear = retrievedJSON.getInt("periodYear");
                retrievedItem.planPeriod = new PlanningPeriod(periodNum,periodYear);

                retrievedItem.description = retrievedJSON.getString("description");
                retrievedItem.account = retrievedJSON.getString("account");
                retrievedItem.amount = retrievedJSON.getDouble("amount");
                retrievedItem.date = retrievedJSON.getString("date");
                retrievedItem.itemId = itemId;
                retrievedItem.persister = this;
            }

        } catch (JSONException e) {

        }
        return retrievedItem;
    }

    @Override
    public UUID[] retrieveRelatedItems(Item item) {
        UUID[] relatedItemIds = null;
        try {
            JSONObject retrievedJSON = retrieveItemJSON(item.getItemId());
            if (retrievedJSON != null) {
                JSONArray relatedItems = retrievedJSON.getJSONArray("relatedItems");
                relatedItemIds = new UUID[relatedItems.length()];
                for (int i = 0; i < relatedItems.length(); i++) {
                    relatedItemIds[i] = UUID.fromString(relatedItems.getString(i));
                }
            }
        } catch (JSONException e) {

        }
        return relatedItemIds;
    }

    private JSONObject retrieveItemJSON(UUID itemId) throws JSONException {
        JSONObject retrieveItemObject = new JSONObject();
        retrieveItemObject.put("persistenceType", "loadItem");
        JSONObject dataObject = new JSONObject();
        dataObject.put("uuid", itemId.toString());
        retrieveItemObject.put("data", dataObject);
        return CloudCaller.sendJSON(retrieveItemObject);
    }

}
