package mycompany.thistest.PlacesSearch;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.maps.model.LatLng;

import mycompany.thistest.MainActivity;
import mycompany.thistest.R;

/**
 * Created by trsq9010 on 08/12/2014.
 */
public class LoadPlaces extends AsyncTask<String, String, String> {

    private OnTaskComplete onTaskComplete;

    public interface OnTaskComplete {
        public void setMyTaskComplete(PlacesList places);
    }


    public void setMyTaskCompleteListener(OnTaskComplete onTaskComplete) {
        this.onTaskComplete = onTaskComplete;
    }

    double radius;
    String types;
    LatLng currentPos;
    PlacesList nearPlaces;


    public LoadPlaces(double r, String t, LatLng pos ){

        radius = r;
        types = t;
        currentPos = pos;
        nearPlaces = null;
    }


    protected String doInBackground(String... args) {

        // creating Places class object
        GooglePlaces googlePlaces = new GooglePlaces();
        try {

            // get nearest places
            nearPlaces = googlePlaces.search(currentPos.latitude,
                    currentPos.longitude,
                    radius, types);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    protected void onPostExecute(String file_url) {
        onTaskComplete.setMyTaskComplete(nearPlaces);

        /*activity.runOnUiThread(new Runnable() {
            public void run() {
                if (nearPlaces != null) {
                    if(nearPlaces.results.isEmpty()) {
                        nearPlaces.results.add(new Place());
                    }
                    //if you want to show nearPlaces on a map linked to the activity
                    if (activity instanceof MainActivity) {
                        //the activity should have a method to get a customizedMap
                         ((MainActivity) activity).getMap().updateNearPlaces(nearPlaces);
                    }
                }
            }
        });
        /*try {
            if (pDialog!=null && pDialog.isShowing())
                pDialog.dismiss();
        }catch (Exception e){

        }*/


    }
}