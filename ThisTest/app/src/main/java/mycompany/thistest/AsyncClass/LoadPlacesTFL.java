package mycompany.thistest.AsyncClass;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import mycompany.thistest.TFLPlaces.Arrivals;
import mycompany.thistest.TFLPlaces.Station;
import mycompany.thistest.TFLPlaces.StationsList;
import mycompany.thistest.TFLPlaces.TFLSearch;

/**
 * Created by trsq9010 on 08/12/2014.
 */
public class LoadPlacesTFL extends AsyncTask<String, String, StationsList>{

    StationsList stationsList ;
    String type;
    double latitude;
    double longitude;
    int radius;

    public LoadPlacesTFL(String t, double lat, double lon, int r){
        type = t;
        latitude = lat;
        longitude = lon;
        radius = r;
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

        for(Station s: stationsList.stopPoints) {
            if(s.commonName!= null)
            Log.v("nawak", s.commonName);
        }


    }
}