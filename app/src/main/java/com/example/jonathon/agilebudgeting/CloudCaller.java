package com.example.jonathon.agilebudgeting;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jonathon on 12/9/17.
 */

class CloudCaller {
    public static JSONObject sendJSON(JSONObject objectToSend) {
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
