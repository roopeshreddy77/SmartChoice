package team08.smartchoice;

import android.location.Address;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Batman on 7/29/16.
 */
public class GetLocationCoordinates extends AsyncTask<Void, Void, Void> {

    private String inputAddress;
    private StringBuilder JSONResults;
    private InputStreamReader inputStreamReader;
    private String googleMapsURL;
    private HttpURLConnection connection;
    private URL url;

    private Double latitude;
    private Double logitude;

    public Double getLatitude(){
        return this.latitude;
    }

    public Double getLogitude(){
        return this.logitude;
    }

    public interface AsyncResponse{
        void processFinish(Map<String ,Double> map);
    }

    public AsyncResponse delegate = null;

    public GetLocationCoordinates(String inputAddress, AsyncResponse delegate){
        this.inputAddress = inputAddress.replaceAll(" ", "%20");
        this.delegate = delegate;
    }

    private StringBuilder getAddressDetails(){
        Log.d("GetA","ascds");
        JSONResults =new StringBuilder();
        googleMapsURL = "https://maps.googleapis.com/maps/api/geocode/json?address=" + this.inputAddress
                 +"&key=AIzaSyBw1uinnIw-qoTxwwdw3c1IESXptx9iDaE";

        try {
            url = new URL(googleMapsURL);
            Log.d("URL", url.toString());
            connection = (HttpURLConnection) url.openConnection();
            Log.d("Status", String.valueOf(connection.getResponseCode()));
            inputStreamReader = new InputStreamReader(connection.getInputStream());
            Log.d("inputStream", inputStreamReader.toString());
            int read;
            while ((read = inputStreamReader.read()) != -1) {
                //Log.d("In While", "Hyyyyy");
                JSONResults.append((char) read);
            }
            Log.d("Result", JSONResults.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSONResults;
    }

    private void getCoOrdinates(){
        Log.d("getCoOrdinates","Start");
        try {
            JSONObject jsonObject = new JSONObject(JSONResults.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            JSONObject before_geometry_jsonObj = jsonArray.getJSONObject(0);

            JSONObject geometry_jsonObj = before_geometry_jsonObj.getJSONObject("geometry");

            JSONObject location_jsonObj = geometry_jsonObj.getJSONObject("location");

            String latitude = location_jsonObj.getString("lat");
            String longitude = location_jsonObj.getString("lng");

            this.latitude = Double.valueOf(latitude);
            this.logitude = Double.valueOf(longitude);

            Log.d("Lat", this.latitude.toString());
            Log.d("Lng", this.logitude.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("getCoOrdinates","End");
    }

    @Override
    protected Void doInBackground(Void... params) {
        getAddressDetails();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        getCoOrdinates();
        Map<String, Double> map = new HashMap<>();
        map.put("Lat", this.latitude);
        map.put("Lng", this.logitude);
        delegate.processFinish(map);
    }
}
