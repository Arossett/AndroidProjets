package mycompany.thistest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Handler;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import mycompany.thistest.Connectivity.GoogleServicesConnectionDetector;
import mycompany.thistest.Connectivity.ConnectivityChangeReceiver;
import mycompany.thistest.Dialogs.TypesChoice;


public class MainActivity extends Activity implements TypesChoice.NoticeDialogListener {

    public static final float ZOOM_MIN = 14.5f;

    TypesChoice myDiag;

    CustomizedMap map;

    //types of places to find
    String placesTypes;

    //types of places to find
    String transportType;

    //true if Google Play Services are available
    boolean isService;

    //to check change of connection
    ConnectivityChangeReceiver connectivityChangeReceiver;

    //to be sure that there is no dialog displayed yet
    boolean isDialog;

    //used to get former position on the map and information displayed when screen is rotated
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_places_map);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check if google play services are available
        GoogleServicesConnectionDetector cd = new GoogleServicesConnectionDetector(getBaseContext());
        isService = cd.servicesConnected();

        if(isService) {
            setContentView(R.layout.activity_places_map);



            //used to restore view when app rotated
            if (savedInstanceState != null) {
                placesTypes = savedInstanceState.getString("places_types");
                //transportType = savedInstanceState

                //map.setTypes(placesTypes);
                /*map.setOldPos(new LatLng
                        (savedInstanceState.getDouble("latitude"), savedInstanceState.getDouble("longitude")));*/

                CustomizedMap customMap =  savedInstanceState.getParcelable("map");
                GoogleMap googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
                if(customMap !=null) {
                    map = new CustomizedMap(this, googleMap, customMap );
                }
                else {
                    map = new CustomizedMap(googleMap, this);
                }

                isDialog = savedInstanceState.getBoolean("isDialogDisplayed");


            } else {
                placesTypes = null;
                isDialog = false;
                //create a customized map from map fragment
                GoogleMap m = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
                m.addMarker(new MarkerOptions().title("nana").position(m.getCameraPosition().target));
                map = new CustomizedMap(m, this);

            }

            //initialize the connectionChange receiver
            connectivityChangeReceiver =  new ConnectivityChangeReceiver(new Handler() {
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    map.setIsConnected(msg.getData().getBoolean("isConnected"));
                }});

            registerReceiver(
                    connectivityChangeReceiver,
                    new IntentFilter(
                            ConnectivityManager.CONNECTIVITY_ACTION));



            //to add listener on button "See list"
            setButtonsListener();

            //to display choices window when app is starting (check if there is no dialog yet)
            if (!isDialog) {
                myDiag = new TypesChoice();
                Bundle diagBundle = new Bundle();

                //type of places to look for
                diagBundle.putStringArray("types",getResources().getStringArray(R.array.place_types));

                //to identify the dialog fragment (used for places search in this case)
                diagBundle.putInt("search_id", R.id.place_settings);

                //to indicate if multiple choice can be done
                diagBundle.putBoolean("isMultipleChoice", true);

                //true if user can close dialog without killing app
                diagBundle.putBoolean("closable", false);

                myDiag.setArguments(diagBundle);
                myDiag.show(getFragmentManager(), "Diag");

                isDialog = true;
            }
        }

        //if Google Services are not available, display a message
        else{
            RelativeLayout rl = new RelativeLayout(getApplicationContext());
            TextView tv = new TextView(getBaseContext());
            tv.setTextAppearance(getBaseContext(), R.style.AppTheme);
            tv.setText(R.string.google_services_not_available);
            rl.addView(tv);
            setContentView(rl);
        }

    }

    //to add buttons' listener
    private void setButtonsListener(){
        //to set the button used to show places list
        Button b = (Button) findViewById(R.id.button);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(map.getCameraPosition().zoom>ZOOM_MIN) {
                    Intent i = new Intent(getApplicationContext(),
                            PlacesListActivity.class);
                    // passing near places to map activity
                    i.putExtra("near_places", map.getNearPlaces());
                    i.putExtra("types", placesTypes);
                    startActivity(i);
                }
            }
        };
        b.setOnClickListener(onClickListener);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //used so the map is not always updated when user is still moving it
        if(ev.getAction()==MotionEvent.ACTION_UP) {
            map.setIsMoving(true);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //type of places
        //outState.putString("places_types", placesTypes);
        //outState.putDouble("latitude", map.getCameraPosition().target.latitude);
        //outState.putDouble("longitude",map.getCameraPosition().target.longitude);

        outState.putBoolean("isDialogDisplayed", isDialog);

        outState.putParcelable("map", getMap());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_places_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //add place choices windows to the menu
        if (id == R.id.place_settings) {
            myDiag=new TypesChoice();
            Bundle diagBundle = new Bundle();
            diagBundle.putStringArray("types",getResources().getStringArray(R.array.place_types));
            diagBundle.putInt("search_id", R.id.place_settings);
            diagBundle.putBoolean("isMultipleChoice", true);
            myDiag.setArguments(diagBundle);
            myDiag.show(getFragmentManager(), "Diag");
            return true;
        }

        //add transport choices windows to the menu
        if (id == R.id.transport_settings) {
            myDiag=new TypesChoice();
            Bundle diagBundle = new Bundle();
            diagBundle.putStringArray("types",getResources().getStringArray(R.array.transport_types));
            diagBundle.putInt("search_id", R.id.transport_settings);
            myDiag.setArguments(diagBundle);
            myDiag.show(getFragmentManager(), "Diag");
            return true;
        }



        return super.onOptionsItemSelected(item);
    }

    //implement method when positive button on choices dialog has been clicked
    @Override
    public boolean onDialogPositiveClick(TypesChoice dialog) {
        //get list of items selected
        ArrayList<String> list = dialog.getmSelectedItems();
        //check what kind of choices have been made
        int id = dialog.getTypeId();

        //switch between the different kind of choices that can be made with a TypesChoice Dialog
        switch (id){
            case R.id.place_settings: {
                placesTypes = "";
                for (String s : list) {
                    placesTypes = placesTypes + "|" + s;
                }
                map.setTypes(placesTypes);
                break;
            }
            case R.id.transport_settings: {
                transportType = list.get(0);
                map.setTransports(transportType);
                break;
            }
            default:{
                break;
            }
        }

        return true;
    }

    public CustomizedMap getMap(){
        return map;
    }

    @Override
    protected void onDestroy() {
        if(isService)
            unregisterReceiver(connectivityChangeReceiver);
        super.onDestroy();
    }


}
