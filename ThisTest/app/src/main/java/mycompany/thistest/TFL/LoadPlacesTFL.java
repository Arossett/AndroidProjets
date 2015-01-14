package mycompany.thistest.TFL;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mycompany.thistest.Interfaces.Spot;
import mycompany.thistest.MainActivity;

/**
 * Created by trsq9010 on 08/12/2014.
 */
public class LoadPlacesTFL extends AsyncTask<String, String, List<Spot>>{

    List<Spot> stationsList ;
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
        stationsList = new ArrayList<Spot>();//new StationsList();
        super.onPreExecute();

    }


    protected List<Spot> doInBackground(String... args) {

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


    protected void onPostExecute(List<Spot> stationsList) {
        //if search has return null or empty list
        //create an empty stations list to override previous search
       if (stationsList==null||stationsList.isEmpty()) {
           stationsList = new ArrayList<Spot>();
           //use to erase previous search
           //stationsList.add();
       }
       else {
            for (Spot s : stationsList) {
                if (s.getName() != null)
                    Log.v("nawak", s.getName());
                if(s.getType().equals("Bike")){
                    if(((BikePoint)s).additionalProperties!=null){
                    Log.v("bikepoint", "properties : "+((BikePoint)s).additionalProperties.size());

                    Log.v("bikepoint", "properties : "+((BikePoint)s).additionalProperties.get(0).getKey());

                        Log.v("bikepoint", "properties : "+((BikePoint)s).additionalProperties.get(0).getValue());


                    }
                }
            }
        }
        //if you want to show nearPlaces on a map linked to the activity
        if (activity instanceof MainActivity) {
            //the activity should have a method to get a customizedMap
            ((MainActivity) activity).getMap().setNearStations(stationsList);
        }
    }
}