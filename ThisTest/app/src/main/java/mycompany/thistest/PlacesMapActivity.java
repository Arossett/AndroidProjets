package mycompany.thistest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

import mycompany.thistest.Connectivity.GPSTracker;
import mycompany.thistest.Dialogs.TypesChoice;
import mycompany.thistest.PlacesSearch.Place;
import mycompany.thistest.PlacesSearch.PlacesList;


public class PlacesMapActivity extends Activity implements TypesChoice.NoticeDialogListener {

    public static final float ZOOM_MIN = 14.5f;
    // Nearest places
    private PlacesList nearPlaces;

    // Map view
    GoogleMap map;

    //radius of the perimeter where we look for places
    double radius;

    //last position where places were displayed
    LatLng oldPos;

    //current position of the camera
    private LatLng currentPos;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_map);
        utils = new Utils();

        TypesChoice myDiag=new TypesChoice();
        myDiag.show(getFragmentManager(), "Diag");

        initParams();
        setCameraListener();
        addButtonsListener();
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String reference = markerRef.get(marker);

                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        SinglePlaceActivity.class);

                // Sending place refrence id to single place activity
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
    private void addButtonsListener(){

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
                EditText etLocation = (EditText) findViewById(R.id.et_location);
                String location = etLocation.getText().toString();

                //search it on the map
                if(location!=null && !location.equals("")){
                    new GeocodeTask(PlacesMapActivity.this).execute(location);

                }
            }
        };
        btn_find.setOnClickListener(findClickListener);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction()==MotionEvent.ACTION_UP) {
            isMoving = true;
        }
        return super.dispatchTouchEvent(ev);
    }

    //to initialize parameters of the activity
    private void initParams(){
        pos = null;
        markerRef = new HashMap<Marker, String>();
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        GPSTracker gps = new GPSTracker(this);
        map.setMyLocationEnabled(true);
        map.setPadding(0, utils.dpToPx(50, getBaseContext()), 0, 0);

        double user_lat = gps.getLatitude();
        double user_long = gps.getLongitude();

        oldPos = new LatLng(user_lat, user_long);
        currentPos = new LatLng(user_lat, user_long);
        radius = 400;
        max_pos = 0;
        // final LatLngBounds.Builder builder = new LatLngBounds.Builder();

       CircleOptions circleOptions = new CircleOptions()
                .center(currentPos)
                .radius(max_pos)
                .fillColor(0x220000FF)
                .strokeColor(Color.TRANSPARENT);
        circle = map.addCircle(circleOptions);
        types = null;

    }


    //to initialize and set camera listener (to do in OnCreate)
    private void setCameraListener(){
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPos,(float)(ZOOM_MIN+0.5)));

        final GoogleMap.OnCameraChangeListener listener = new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                currentPos = cameraPosition.target;
                Log.v("nawak", "camera zoom" + cameraPosition.zoom);
                if(cameraPosition.zoom>=ZOOM_MIN ){
                    if((utils.distFrom(oldPos, currentPos) > radius/2||markerRef.isEmpty()) && isMoving){

                        oldPos = cameraPosition.target;
                        new LoadPlaces(PlacesMapActivity.this, radius, types, currentPos).execute();
                        if(pos != null)
                            pos.remove();
                        pos = map.addMarker(new MarkerOptions().position(currentPos));
                        isMoving = false;
                        if(circle == null) {
                            CircleOptions circleOptions = new CircleOptions()
                                    .center(currentPos)
                                    .radius(max_pos)
                                    .fillColor(0x220000FF)
                                    .strokeColor(Color.TRANSPARENT);

                            circle = map.addCircle(circleOptions);
                            findViewById(R.id.button).setVisibility(View.VISIBLE);
                        }
                    }
                }else {
                    map.clear();
                    circle = null;
                    markerRef.clear();
                    findViewById(R.id.button).setVisibility(View.INVISIBLE);

                }
            }
        };
        map.setOnCameraChangeListener(listener);
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

        //noinspection SimplifiableIfStatement
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
        for(Marker m : markerRef.keySet())
            m.remove();

        markerRef.clear();

        if(nearPlaces.results!= null)
            for (Iterator<Place> iterator = nearPlaces.results.iterator(); iterator.hasNext(); ) {
                Place place = iterator.next();

            double latitude = place.geometry.location.lat; // latitude
            double longitude = place.geometry.location.lng; // longitude
            int distance = (int)utils.distFrom(currentPos, new LatLng(latitude, longitude));

            if(distance<=radius) {
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
            }
            else{
               iterator.remove();
            }
        }

        Log.v("distance","distance max = " + max_pos);
        circle.setCenter(currentPos);
        circle.setRadius(max_pos);
    }

    @Override
    public boolean onDialogPositiveClick(TypesChoice dialog) {
        ArrayList<String> list = dialog.getmSelectedItems();
        types = "";
        for(String s : list) {
            types = types + "|" + s;
            new LoadPlaces(PlacesMapActivity.this, radius, types, currentPos).execute();
            Log.v("types", s);
        }
        return true;
    }

    public void setCurrentPos(LatLng pos){
        currentPos = pos;
    }

    @Override
    public boolean onDialogNegativeClick(TypesChoice dialog) {
        return false;
    }

    public void setNearPlaces(PlacesList np){
        nearPlaces = np;
    }

    /**
     * Background Async Task to Load Google places
     * */
    /*class LoadPlaces extends AsyncTask<String, String, String> {
        ProgressDialog pDialog;


        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(PlacesMapActivity.this);
            pDialog.setMessage(Html.fromHtml("<b>Search</b><br/>Loading Places..."));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            super.onPreExecute();

        }


        protected String doInBackground(String... args) {

            // creating Places class object
            GooglePlaces googlePlaces = new GooglePlaces();
            try {

                // get nearest places
                nearPlaces = googlePlaces.search(currentPos.latitude,
                        currentPos.longitude,
                        radius, types);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        protected void onPostExecute(String file_url) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (nearPlaces != null)
                        setMarkers();

                }
            });
            pDialog.dismiss();

        }
    }*/
}
