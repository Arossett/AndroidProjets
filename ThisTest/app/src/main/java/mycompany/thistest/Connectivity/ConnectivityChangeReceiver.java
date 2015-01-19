package mycompany.thistest.Connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import android.os.Handler;

public class ConnectivityChangeReceiver
        extends BroadcastReceiver {

    boolean isConnected;
    private final Handler handler; // Handler used to execute code on the UI thread

    public ConnectivityChangeReceiver(Handler h){
        handler = h;
        isConnected = true;

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        debugIntent(intent, "placesLocationConnectivity");
       // ConnectionDetector cd = new ConnectionDetector(activity.getBaseContext());
       handler.post(new Runnable() {
            @Override
            public void run() {
                Bundle messageBundle=new Bundle();
                Message myMessage=handler.obtainMessage();
                //Ajouter des données à transmettre au Handler via le Bundle
                messageBundle.putBoolean("isConnected", isConnected);
                //Ajouter le Bundle au message
                myMessage.setData(messageBundle);
                //Envoyer le message
                handler.sendMessage(myMessage);

            }
        });
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


}