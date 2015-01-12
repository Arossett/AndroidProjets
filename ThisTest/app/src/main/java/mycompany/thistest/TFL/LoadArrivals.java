package mycompany.thistest.TFL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.util.List;

import mycompany.thistest.TransportActivity;

/**
 * Created by trsq9010 on 19/12/2014.
 */
public class LoadArrivals extends AsyncTask<String, String, String> {
    // Progress dialog
    ProgressDialog pDialog;
    Activity activity;
    String stationId;

    public LoadArrivals(Activity a, String id){
        activity = a;
        stationId = id;
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
        // Check if used is connected to Internet
        try {
            List<Arrival> arrivals = new TFLSearch().searchArrivals(stationId);

            if(activity instanceof TransportActivity){
                ((TransportActivity)activity).setArrivals(arrivals);
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
                if(activity instanceof TransportActivity) {
                    //add fragments displaying all lines arrivals information
                    ((TransportActivity) activity).addNewLineFragment();
                }
            }
        });

    }

}