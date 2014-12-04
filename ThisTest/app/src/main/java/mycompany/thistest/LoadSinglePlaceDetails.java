package mycompany.thistest;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Created by trsq9010 on 04/12/2014.
 */
public class LoadSinglePlaceDetails extends AsyncTask<String, String, String> {
    PlaceDetails placeDetails;

    public PlaceDetails getDetails(){
        return placeDetails;
    }

    /**
     * Before starting background thread Show Progress Dialog
     * */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    /**
     * getting Profile JSON
     * */
    protected String doInBackground(String... args) {
        String reference = args[0];

        // creating Places class object
        GooglePlaces googlePlaces = new GooglePlaces();

        // Check if used is connected to Internet
        try {
            placeDetails = googlePlaces.getPlaceDetails(reference);

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
        // updating UI from Background Thread


    }


}
