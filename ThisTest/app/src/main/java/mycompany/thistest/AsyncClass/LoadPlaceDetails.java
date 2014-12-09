package mycompany.thistest.AsyncClass;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import mycompany.thistest.PlacesSearch.GooglePlaces;
import mycompany.thistest.PlacesSearch.PlaceDetails;
import mycompany.thistest.SinglePlaceActivity;

/**
 * Created by trsq9010 on 09/12/2014.
 */
public class LoadPlaceDetails extends AsyncTask<String, String, String> {
    // Progress dialog
    ProgressDialog pDialog;
    Activity activity;
    GooglePlaces googlePlaces;

    public LoadPlaceDetails(Activity a, GooglePlaces gp){
        activity = a;
        googlePlaces = gp;
    }

    /**
     * Before starting background thread Show Progress Dialog
     * */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(activity);
        pDialog.setMessage("Loading profile ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    /**
     * getting Profile JSON
     * */
    protected String doInBackground(String... args) {
        String reference = args[0];

        // creating Places class object
        googlePlaces = new GooglePlaces();

        // Check if used is connected to Internet
        try {
            PlaceDetails placeDetails = googlePlaces.getPlaceDetails(reference);
            if(activity instanceof SinglePlaceActivity)
            {
                ((SinglePlaceActivity)activity).setPlaceDetails(placeDetails);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * After completing background task Dismiss the progress dialog
     * **/
    protected void onPostExecute(String file_url) {
        // dismiss the dialog after getting all products
        pDialog.dismiss();
        // updating UI from Background Thread
        activity.runOnUiThread(new Runnable() {
            public void run() {
                if(activity instanceof SinglePlaceActivity) {
                    ((SinglePlaceActivity) activity).showDetails();

                }
                /**
                 * Updating parsed Places into LISTVIEW
                 * */


            }
        });

    }

}