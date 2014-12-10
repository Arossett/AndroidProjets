package mycompany.thistest.Connectivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import mycompany.thistest.PlacesMapActivity;

public class ConnectivityChangeReceiver
        extends BroadcastReceiver {

    boolean isConnected;
    Activity activity;

    public ConnectivityChangeReceiver(Activity a){
        isConnected = false;
        activity = a;

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        debugIntent(intent, "placesLocationConnectivity");
        ConnectionDetector cd = new ConnectionDetector(activity.getBaseContext());

        if(isConnected&&cd.servicesConnected()) {
            ((PlacesMapActivity) activity).updateMap();
        }
    }

    private void debugIntent(Intent intent, String tag) {
        Log.v(tag, "action: " + intent.getAction());
        Log.v(tag, "component: " + intent.getComponent());
        Bundle extras = intent.getExtras();
        if (extras != null) {
            isConnected = true;
            for (String key: extras.keySet()) {
                Log.v(tag, "key [" + key + "]: " +
                        extras.get(key));
                if(key.equals("noConnectivity"))
                    isConnected = false;
            }
        }
        else {
            Log.v(tag, "no extras");
        }
    }

    public boolean getConnectivity(){
        return isConnected;
    }



}