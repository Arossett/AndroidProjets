package mycompany.thistest.TFL;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import mycompany.thistest.MainActivity;

/**
 * Created by trsq9010 on 08/12/2014.
 */
public class LoadPlacesTFL extends AsyncTask<String, String, StationsList>{

    StationsList stationsList ;
    String type;
    double latitude;
    double longitude;
    int radius;
    Activity activity;

    public LoadPlacesTFL(String t, double lat, double lon, int r, Activity a){
        type = t;
        latitude = lat;
        longitude = lon;
        radius = r;
        activity =a;
    }

    @Override
    protected void onPreExecute() {
        stationsList = new StationsList();
        super.onPreExecute();

    }


    protected StationsList doInBackground(String... args) {

        // creating Places class object
        TFLSearch stationsSearch = new TFLSearch();

        try {

            // get nearest places
            stationsList = stationsSearch.searchbyArea(type, latitude, longitude, radius);
            //List<Arrivals> arrivals = stationsSearch.searchArrivals();
            return stationsList;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    protected void onPostExecute(StationsList stationsList) {
        //if search has return null or empty list
        //create an empty stations list to override previous search
       if (stationsList== null||stationsList.stopPoints.isEmpty()) {
           stationsList = new StationsList();
           stationsList.stopPoints.add(new Station());
       }
       else {
            for (Station s : stationsList.stopPoints) {
                if (s.commonName != null)
                    Log.v("nawak", s.commonName);
            }
        }
        //if you want to show nearPlaces on a map linked to the activity
        if (activity instanceof MainActivity) {
            //the activity should have a method to get a customizedMap
            ((MainActivity) activity).getMap().setNearStations(stationsList);
        }
    }
}