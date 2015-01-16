package mycompany.thistest.TFL;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import mycompany.thistest.Interfaces.Spot;
import mycompany.thistest.MainActivity;
import mycompany.thistest.PlacesSearch.PlacesList;
import mycompany.thistest.R;

/**
 * Created by trsq9010 on 08/12/2014.
 */
public class LoadPlacesTFL extends AsyncTask<String, String, List<Spot>>{

    List<Spot> stationsList ;
    String type;
    double latitude;
    double longitude;
    int radius;
    //Activity activity;
    private OnTaskComplete onTaskComplete;

    public interface OnTaskComplete {
        public void setMyTaskComplete(List<Spot> places);
    }


    public void setMyTaskCompleteListener(OnTaskComplete onTaskComplete) {
        this.onTaskComplete = onTaskComplete;
    }

    public LoadPlacesTFL(String t, double lat, double lon, int r){
        type = t;
        latitude = lat;
        longitude = lon;
        radius = r;
       // activity =a;
    }

    @Override
    protected void onPreExecute() {
        /*ProgressBar pb = (ProgressBar) activity.findViewById(R.id.progressBar);
        pb.setIndeterminate(true);
        pb.setVisibility(View.VISIBLE);*/
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
        onTaskComplete.setMyTaskComplete(stationsList);

        //if search has return null or empty list
        //create an empty stations list to override previous search
       /*if (stationsList==null||stationsList.isEmpty()) {
           stationsList = new ArrayList<Spot>();
       }
        //if you want to show nearPlaces on a map linked to the activity
        if (activity instanceof MainActivity) {
            //the activity should have a method to get a customizedMap
            ((MainActivity) activity).getMap().updateStations(stationsList);
        }
        ProgressBar pb = (ProgressBar) activity.findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);*/
    }
}