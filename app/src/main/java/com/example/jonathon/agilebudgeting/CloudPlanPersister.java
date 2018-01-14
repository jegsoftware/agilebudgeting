package com.example.jonathon.agilebudgeting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;


/**
 * Created by Jonathon on 7/8/2017.
 */

public class CloudPlanPersister implements IPersistPlan, Serializable {
    @Override
    public void persist(Plan plan) {
        JSONObject savePlanObject = new JSONObject();
        try {
            savePlanObject.put("persistenceType", "savePlan");
            JSONObject dataObject = new JSONObject();

            PlanStatus planningStatus = plan.isPlanningOpen() ? PlanStatus.OPEN : PlanStatus.CLOSED;
            PlanStatus actualsStatus = plan.isActualsOpen() ? PlanStatus.OPEN : PlanStatus.CLOSED;

            dataObject.put("periodNum", plan.getPeriod().getPeriodNumber());
            dataObject.put("periodYear", plan.getPeriod().getPeriodYear());
            dataObject.put("planningStatus", planningStatus.name());
            dataObject.put("actualsStatus", actualsStatus.name());
            savePlanObject.put("data", dataObject);
            CloudCaller.sendJSON(savePlanObject);
        } catch (JSONException e) {

        }
    }

    @Override
    public Plan populate(Plan newPlan, PlanningPeriod period) {
        newPlan.setPeriod(period);
        JSONObject getPlanObject = new JSONObject();
        try {
            getPlanObject.put("persistenceType", "loadPlan");
            JSONObject dataObject = new JSONObject();
            dataObject.put("periodNum", period.getPeriodNumber());
            dataObject.put("periodYear", period.getPeriodYear());
            getPlanObject.put("data", dataObject);
            JSONObject retrievedPlan = CloudCaller.sendJSON(getPlanObject);

            if (retrievedPlan != null) {
                String planningStatusString = retrievedPlan.getString("planningStatus");
                String actualsStatusString = retrievedPlan.getString("actualsStatus");

                if (PlanStatus.CLOSED == PlanStatus.valueOf(planningStatusString)) {
                    newPlan.closePlanning();
                }
                if (PlanStatus.CLOSED == PlanStatus.valueOf(actualsStatusString)) {
                    newPlan.closeActuals();
                }
                populateItems(newPlan, retrievedPlan.getJSONArray("items"));
            }

        } catch (JSONException e) {

        }
        return newPlan;
    }

    private void populateItems(Plan newPlan, JSONArray items) throws JSONException {
        for (int i = 0; i < items.length(); i++) {
            JSONObject itemJSON = items.getJSONObject(i);
            String type = itemJSON.getString("type");
            String itemIdStr = itemJSON.getString("uuid");
            UUID itemId = UUID.fromString(itemIdStr);

            if ("Deposit".equals(type)) {
                Deposit deposit = Deposit.createDeposit(itemId, new CloudItemPersister());
                newPlan.addDeposit(deposit);
            } else if ("PlannedItem".equals(type)) {
                Item plannedItem = Item.createItem(itemId, new CloudItemPersister());
                newPlan.addPlannedItem(plannedItem);
            } else if ("ActualItem".equals(type)) {
                ActualItem actualItem = ActualItem.createActualItem(itemId, new CloudItemPersister());
                newPlan.addActualItem(actualItem);
            }
        }
    }

}
