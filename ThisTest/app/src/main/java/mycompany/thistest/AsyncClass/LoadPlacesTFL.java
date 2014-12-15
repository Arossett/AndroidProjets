package mycompany.thistest.AsyncClass;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import mycompany.thistest.TFLPlaces.Arrivals;
import mycompany.thistest.TFLPlaces.StationsList;
import mycompany.thistest.TFLPlaces.TFLSearch;

/**
 * Created by trsq9010 on 08/12/2014.
 */
public class LoadPlacesTFL extends AsyncTask<String, String, List<Arrivals> >{

    StationsList stationsList ;
    String stationName;

    public LoadPlacesTFL(String name){
        stationName = name;
    }

    @Override
    protected void onPreExecute() {
        stationsList = new StationsList();
        super.onPreExecute();

    }


    protected List<Arrivals> doInBackground(String... args) {

        // creating Places class object
        TFLSearch stationsSearch = new TFLSearch();

        try {

            // get nearest places
            stationsList = stationsSearch.searchbyArea( "Metro",51.49, -0.27, 1000);
            List<Arrivals> arrivals = stationsSearch.searchArrivals();
            return arrivals;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    protected void onPostExecute(List<Arrivals> arrivals) {
      /*  ArrayList<String> nextTrains = new ArrayList<String>();
        for(Arrivals a: arrivals) {
            nextTrains.add(a.toString());
            Log.v("nawak", a.toString());
        }*/


    }
}