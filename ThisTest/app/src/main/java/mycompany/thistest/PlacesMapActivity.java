package mycompany.thistest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import mycompany.thistest.AsyncClass.GeocodeTask;
import mycompany.thistest.AsyncClass.LoadPlaces;
import mycompany.thistest.Connectivity.ConnectionDetector;
import mycompany.thistest.Connectivity.ConnectivityChangeReceiver;
import mycompany.thistest.Connectivity.GPSTracker;
import mycompany.thistest.Dialogs.TypesChoice;
import mycompany.thistest.PlacesSearch.Place;
import mycompany.thistest.PlacesSearch.PlacesList;


public class PlacesMapActivity extends Activity implements TypesChoice.NoticeDialogListener {

    public static final float ZOOM_MIN = 14.5f;
    // Nearest places
    PlacesList nearPlaces;

    // Map view
    GoogleMap map;

    //radius of the perimeter where we look for places
    double radius;

    //last position where places were displayed
    LatLng oldPos;

    //right if the camera is still moving
    boolean isMoving;

    //circle used to show perimeter where places were looked
    Circle circle;

    //types of places to find
    String types;

    //to get the reference of a particular marker (used to display details about a place from map)
    HashMap<Marker, String> markerRef;

    //marker of the camera, needed to remove marker from previous location
    Marker pos;

    //the farest location of marker
    int max_pos;

    //to use methods from Utils class
    Utils utils;

    //true if Google Play Services are available
    boolean isService;

    //to check change of connection
    ConnectivityChangeReceiver connectivityChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ConnectionDetector cd = new ConnectionDetector(getBaseContext());
        isService = cd.servicesConnected();

        super.onCreate(savedInstanceState);

        if(isService) {
            setContentView(R.layout.activity_places_map);

            connectivityChangeReceiver =  new ConnectivityChangeReceiver(this);
            registerReceiver(
                    connectivityChangeReceiver,
                    new IntentFilter(
                            ConnectivityManager.CONNECTIVITY_ACTION));
            initParams();

            //used to restore view when app rotated
            if (savedInstanceState != null) {
                types = savedInstanceState.getString("types");
                oldPos = new LatLng
                        (savedInstanceState.getDouble("latitude"), savedInstanceState.getDouble("longitude"));
                //updateMap();
            } else {
                types = null;
            }

            //add listeners to handle all events
            setCameraListener();
            setButtonsListener();
            setMarkersListener();
            setLocationButtonListener();

            //to display choices window
            if (types == null) {
                TypesChoice myDiag = new TypesChoice();
                myDiag.setCancelable(false);
                myDiag.show(getFragmentManager(), "Diag");
            }
        }
        //if Google Services are not available, display a message
        else{
            RelativeLayout rl = new RelativeLayout(getApplicationContext());
            TextView tv = new TextView(getBaseContext());
            tv.setTextAppearance(getBaseContext(), R.style.AppTheme);
            tv.setText("Google Play Services are not available, please download or update it and " +
                    "relaunch application");
            rl.addView(tv);
            setContentView(rl);
        }

    }

    private void setLocationButtonListener(){
        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                GPSTracker gps = new GPSTracker(PlacesMapActivity.this);
                if(!gps.canGetLocation()) {
                    Toast.makeText(getBaseContext(), "Please enable location access", Toast.LENGTH_SHORT).show();

                    return true;
                }
                else{
                    double lat = gps.getLatitude();
                    double lng = gps.getLongitude();
                    map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));
                    return true;
                }

            }
        });
    }

    //add listener to handle markers when touched
    private void setMarkersListener(){
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String reference = markerRef.get(marker);

                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        SinglePlaceActivity.class);

                // Sending place refrence id to single place activity stop pretending !!!
                // place refrence id used to get "Place full details"
                in.putExtra("reference", reference);
                startActivity(in);
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                map.stopAnimation();

                return true;
            }
        });
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
                            MainActivity.class);
                    // passing near places to map activity
                    i.putExtra("near_places", nearPlaces);
                    i.putExtra("types", types);
                    startActivity(i);
                }
            }
        };
        b.setOnClickListener(onClickListener);


        //to set research bar
        Button btn_find = (Button) findViewById(R.id.btn_find);
        View.OnClickListener findClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get location edited by user
                if(connectivityChangeReceiver.getConnectivity())
                {
                    EditText etLocation = (EditText) findViewById(R.id.et_location);
                    String location = etLocation.getText().toString();

                    //search it on the map
                    if (location != null && !location.equals("")) {
                        new GeocodeTask(PlacesMapActivity.this).execute(location);

                    }
                }
                else
                {
                    Toast.makeText(getBaseContext(), "Please turn on your connection",Toast.LENGTH_SHORT ).show();
                }
            }
        };
        btn_find.setOnClickListener(findClickListener);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //used so the map is not always updated when user is still moving it
        if(ev.getAction()==MotionEvent.ACTION_UP) {
            isMoving = true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("types", types);
        outState.putDouble("latitude",map.getCameraPosition().target.latitude);
        outState.putDouble("longitude",map.getCameraPosition().target.longitude);
        super.onSaveInstanceState(outState);
    }

    //to initialize parameters of the activity
    private void initParams(){
        utils = new Utils();
        pos = null;
        markerRef = new HashMap<Marker, String>();
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMyLocationEnabled(true);
        map.setPadding(0, utils.dpToPx(50, getBaseContext()), 0, 0);

        GPSTracker gps = new GPSTracker(this);
        oldPos = new LatLng(gps.getLatitude(), gps.getLongitude());

        //currentPos = new LatLng(user_lat, user_long);
        radius = 400;
        max_pos = 0;

       CircleOptions circleOptions = new CircleOptions()
                .center(oldPos)
                .radius(max_pos)
                .fillColor(0x220000FF)
                .strokeColor(Color.TRANSPARENT);
        circle = map.addCircle(circleOptions);
    }

    //to initialize and set camera listener (to do in OnCreate)
    private void setCameraListener(){
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(oldPos,(float)(ZOOM_MIN+0.5)));

        final GoogleMap.OnCameraChangeListener listener = new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                //check if internet is enabled
                if(connectivityChangeReceiver.getConnectivity()) {
                    //if camera is closed enough and has moved enough to update the map
                    if (cameraPosition.zoom >= ZOOM_MIN) {
                        if ((utils.distFrom(oldPos, cameraPosition.target) > radius / 2 || markerRef.isEmpty()) && isMoving) {
                            updateMap();
                        }
                    } else {
                        //if zoom is too far, clean the map
                        map.clear();
                        circle = null;
                        markerRef.clear();
                        findViewById(R.id.button).setVisibility(View.INVISIBLE);
                    }
                }else{
                    Toast.makeText(getBaseContext(), "Please turn on your connection", Toast.LENGTH_SHORT).show();
                }
            }
        };
        map.setOnCameraChangeListener(listener);
    }

    //update map with places found around new location
    public void updateMap(){
        CameraPosition cameraPosition = map.getCameraPosition();

        Log.v("zoomCamera", "camera zoom" + cameraPosition.zoom);

        oldPos = cameraPosition.target;

        if(types!=null) {
            new LoadPlaces(PlacesMapActivity.this, radius, types, cameraPosition.target).execute();
        }

        if (pos != null) {
            pos.remove();
        }
        pos = map.addMarker(new MarkerOptions().position(cameraPosition.target));

        isMoving = false;

        if (circle == null) {
            CircleOptions circleOptions = new CircleOptions()
                    .center(cameraPosition.target)
                    .radius(max_pos)
                    .fillColor(0x220000FF)
                    .strokeColor(Color.TRANSPARENT);
            circle = map.addCircle(circleOptions);
            findViewById(R.id.button).setVisibility(View.VISIBLE);
        }
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

        //add choices windows to the menu
        if (id == R.id.action_settings) {
            TypesChoice myDiag=new TypesChoice();
            myDiag.show(getFragmentManager(), "Diag");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //to add markers on map following places found
    public void setMarkers(){
        max_pos = 0;

        //clean hash map with previous markets
        for(Marker m : markerRef.keySet())
            m.remove();
        markerRef.clear();

        //for each place, add a market with properties
        if(nearPlaces.results!= null) {
            for (Iterator<Place> iterator = nearPlaces.results.iterator(); iterator.hasNext(); ) {
                Place place = iterator.next();

                double latitude = place.geometry.location.lat; // latitude
                double longitude = place.geometry.location.lng; // longitude
                int distance = (int) utils.distFrom(map.getCameraPosition().target, new LatLng(latitude, longitude));

                if (distance <= radius) {
                    max_pos = Math.max(max_pos, distance);

                    //used when search other that by radar
                    String mDrawableName = place.types[0];
                    String[] str = utils.parseType(types);

                    for (int i = 0; i < str.length; i++) {
                        if (Arrays.asList(place.types).contains(str[i])) {
                            mDrawableName = str[i];
                            break;
                        }
                    }

                    int resID = getResources().getIdentifier(mDrawableName, "drawable", getPackageName());
                    Marker m;
                    if (resID == 0) {
                        m = (map.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .title(place.name)));
                    } else {
                        m = (map.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .title(place.name)
                                .icon(BitmapDescriptorFactory.fromResource(resID))));
                    }

                    markerRef.put(m, place.reference);
                } else {
                    iterator.remove();
                }
            }
        }
        Log.v("distance","distance max = " + max_pos);
        circle.setCenter(map.getCameraPosition().target);
        circle.setRadius(max_pos);
    }

    @Override
    public boolean onDialogPositiveClick(TypesChoice dialog) {
        ArrayList<String> list = dialog.getmSelectedItems();
        types = "";
        for(String s : list) {
            types = types + "|" + s;
            Log.v("types", s);
        }
        new LoadPlaces(PlacesMapActivity.this, radius, types, map.getCameraPosition().target).execute();

        return true;
    }


    public void setNearPlaces(PlacesList np){
        nearPlaces = np;
    }

    public GoogleMap getMap(){
        return map;
    }

    @Override
    protected void onDestroy() {
        if(isService)
            unregisterReceiver(connectivityChangeReceiver);
        super.onDestroy();
    }
}
