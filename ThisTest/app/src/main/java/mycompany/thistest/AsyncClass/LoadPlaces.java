package mycompany.thistest.AsyncClass;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import mycompany.thistest.PlacesMapActivity;
import mycompany.thistest.PlacesSearch.GooglePlaces;
import mycompany.thistest.PlacesSearch.PlacesList;

/**
 * Created by trsq9010 on 08/12/2014.
 */
public class LoadPlaces extends AsyncTask<String, String, String> {
    ProgressDialog pDialog;

    Activity activity;
    double radius;
    String types;
    LatLng currentPos;
    PlacesList nearPlaces;

    public LoadPlaces(Activity a, double r, String t, LatLng pos ){
        activity = a;
        radius = r;
        types = t;
        currentPos = pos;
        nearPlaces = null;
    }

    @Override
    protected void onPreExecute() {

        pDialog = new ProgressDialog(activity);
        pDialog.setMessage(Html.fromHtml("<b>Search</b><br/>Loading Places..."));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
        super.onPreExecute();

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

        activity.runOnUiThread(new Runnable() {
            public void run() {

                if (nearPlaces != null) {

                    //if you want to show nearPlaces on a map linked to the activity
                    if (activity instanceof PlacesMapActivity) {
                        //the activity should have a method to get a customizedMap
                         ((PlacesMapActivity) activity).getMap().setNearPlaces(nearPlaces);

                    }
                }

            }
        });
        try {
            if (pDialog!=null && pDialog.isShowing())
                pDialog.dismiss();
        }catch (Exception e){

        }


    }
}