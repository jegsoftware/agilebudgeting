package com.example.jonathon.agilebudgeting;

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
            sendJSON(savePlanObject);
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
            JSONObject retrievedPlan = sendJSON(getPlanObject);

            if (retrievedPlan != null) {
                String planningStatusString = retrievedPlan.getString("planningStatus");
                String actualsStatusString = retrievedPlan.getString("actualsStatus");

                if (PlanStatus.CLOSED == PlanStatus.valueOf(planningStatusString)) {
                    newPlan.closePlanning();
                }
                if (PlanStatus.CLOSED == PlanStatus.valueOf(actualsStatusString)) {
                    newPlan.closeActuals();
                }
            }

        } catch (JSONException e) {

        }
        return newPlan;
    }

    private JSONObject sendJSON(JSONObject objectToSend) {
        HttpURLConnection connection = null;
        String sendData = objectToSend.toString();
        JSONObject retObject = null;
        try {
            URL persistenceURL = new URL("https://us-central1-arcane-antler-164801.cloudfunctions.net/agile-budgeting-persistence");
            connection = (HttpURLConnection) persistenceURL.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setFixedLengthStreamingMode(sendData.length());
            OutputStream out = connection.getOutputStream();
            out.write(sendData.getBytes("UTF-8"));

            InputStream in = connection.getInputStream();
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            String response = result.toString("UTF-8");
            retObject = new JSONObject(response);
        } catch (Exception e) {
            retObject = null;
        } finally {
            if (connection != null) connection.disconnect();
        }
        return retObject;
    }
}
