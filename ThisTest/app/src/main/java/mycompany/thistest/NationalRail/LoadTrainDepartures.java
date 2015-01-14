package mycompany.thistest.NationalRail;

/**
 * Created by trsq9010 on 12/01/2015.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import mycompany.thistest.TFL.Arrival;
import mycompany.thistest.TFL.TFLSearch;
import mycompany.thistest.TransportActivity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.util.List;

import mycompany.thistest.TransportActivity;

/**
 * Created by trsq9010 on 19/12/2014.
 */
public class LoadTrainDepartures extends AsyncTask<String, String, List<String>> {
    // Progress dialog
    ProgressDialog pDialog;
    Activity activity;
    String stationName;

    public LoadTrainDepartures(Activity a, String name){
        activity = a;
        stationName = name;
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
    protected List<String> doInBackground(String... args) {
        List<String> trains = new ArrayList<String>();
        // Check if used is connected to Internet
        try {
            ReadCVS readCVS = new ReadCVS(activity);
            String stationCode = readCVS.searchCodeStation(stationName);
            RailHandler ex = new RailHandler();
            String s = ex.getResponseString(stationCode);
            XMLParser p = new XMLParser();
            List<TrainDeparture> departures = p.DepartureParser(s);
            for(TrainDeparture td : departures){
                trains.add(td.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trains;
    }

    /**
     * After completing background task Dismiss the progress dialog
     * **/
    protected void onPostExecute(final List<String> trains) {
        // dismiss the dialog after getting all products
        pDialog.dismiss();
        // updating UI from Background Thread
        activity.runOnUiThread(new Runnable() {
            public void run() {
                if(activity instanceof TransportActivity) {
                    ((TransportActivity) activity).setTrainDepartures(trains);
                }
            }
        });

    }

}
