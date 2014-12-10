package mycompany.thistest.Connectivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.internal.ca;

import mycompany.thistest.Dialogs.AlertDialogManager;

public class ConnectionDetector {

    private Context _context;
    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;


    public ConnectionDetector(Context context){
        this._context = context;

    }

    /**
     * Checking for all possible internet providers
     * **/
    /*public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }*/


    public boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(_context);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates",
                    "Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
           try {
                Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                        resultCode,
                        (Activity) _context,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                AlertDialogManager alert = new AlertDialogManager();


                // If Google Play services can provide an error dialog
                if (errorDialog != null) {
                    alert.showAlertDialog(_context, "Location Updates", errorDialog.toString(), false);
                }
            }catch (Exception e){
               Log.d("googleplay", "services null");
            }
        }
        return false;
    }

    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */
                switch (resultCode) {
                    case Activity.RESULT_OK :
                    /*
                     * Try the request again
                     */
                        break;
                }
        }
    }


}

