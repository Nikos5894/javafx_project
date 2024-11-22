package com.example.toursystem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class GeoCodingHandler {

    private static final String API_KEY = "3f97f8583a6748e287b93288133e49cb";

    public static String reverseGeocode(double latitude, double longitude) {
        String requestUrl = String.format(
                "https://api.opencagedata.com/geocode/v1/json?q=%f,%f&key=%s", latitude, longitude, API_KEY);
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject json = new JSONObject(response.toString());
            return json.getJSONArray("results").getJSONObject(0).getString("formatted");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static double[] geocodeAddress(String address) {
        String requestUrl = String.format("https://api.opencagedata.com/geocode/v1/json?q=%s&key=%s", address, API_KEY);
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            JSONObject json = new JSONObject(content.toString());
            JSONObject location = json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry");
            double lat = location.getDouble("lat");
            double lng = location.getDouble("lng");

            return new double[]{lat, lng};
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
