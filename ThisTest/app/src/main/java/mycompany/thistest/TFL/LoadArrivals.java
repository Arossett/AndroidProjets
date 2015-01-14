package mycompany.thistest.TFL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import mycompany.thistest.TransportActivity;

/**
 * Created by trsq9010 on 19/12/2014.
 */
public class LoadArrivals extends AsyncTask<String, String, List<Arrival>> {
    // Progress dialog
    ProgressDialog pDialog;
    Activity activity;
    ArrayList<String> stationIds;

    public LoadArrivals(Activity a, ArrayList<String> ids){
        activity = a;
        stationIds = ids;
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
    protected List<Arrival> doInBackground(String... args) {
        // Check if used is connected to Internet
        List<Arrival> arrivals = new ArrayList<Arrival>();
        try {
            for(String stationId : stationIds) {
                arrivals.addAll(new TFLSearch().searchArrivals(stationId));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrivals;
    }

    /**
     * After completing background task Dismiss the progress dialog
     * **/
    protected void onPostExecute(final List<Arrival> arrivals) {
        // dismiss the dialog after getting all products
        pDialog.dismiss();
        // updating UI from Background Thread
        activity.runOnUiThread(new Runnable() {
            public void run() {
                if(activity instanceof TransportActivity) {
                    //add fragments displaying all lines arrivals information
                    ((TransportActivity) activity).addNewFragment(arrivals);
                }
            }
        });
    }

}