package mycompany.thistest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class PlacesMapActivity extends Activity  {

    public static final float ZOOM_MIN = 15;
    // Nearest places
    PlacesList nearPlaces;

    // Map view
    GoogleMap map;

    double radius;
    LatLng oldPos;
    LatLng currentPos;
    GPSTracker gps;
    boolean isRunning;
    Circle circle;
    String types;
    HashMap<Marker, String> markerRef;
    Marker pos;
    boolean isErased;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_map);
        TypesChoice myDiag=new TypesChoice();
        myDiag.show(getFragmentManager(), "Diag");

        initParams();
        setCameraListener();


        new LoadPlaces().execute();

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
                    new GeocoderTask().execute(location);
                }
            }
        };
        btn_find.setOnClickListener(findClickListener);
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String reference = markerRef.get(marker);
                // map.stopAnimation();

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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction()==MotionEvent.ACTION_UP) {
            isRunning = true;
        }
        return super.dispatchTouchEvent(ev);
    }

    //to initialize parameters of the activity
    private void initParams(){
        pos = null;
        markerRef = new HashMap<Marker, String>();
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        gps = new GPSTracker(this);
        map.setMyLocationEnabled(true);
        map.setPadding(0, dpToPx(50), 0, 0);

        double user_lat = gps.getLatitude();
        double user_long = gps.getLongitude();

        oldPos = new LatLng(user_lat, user_long);
        currentPos = new LatLng(user_lat, user_long);
        radius = 500;
        // final LatLngBounds.Builder builder = new LatLngBounds.Builder();

       CircleOptions circleOptions = new CircleOptions()
                .center(currentPos)
                .radius(radius)
                .fillColor(0x220000FF)
                .strokeColor(Color.TRANSPARENT);
        circle = map.addCircle(circleOptions);
        isErased = false;
        types = "restaurant|bar";

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
                    if((distFrom(oldPos, currentPos) > radius/2||markerRef.isEmpty()) && isRunning){

                        oldPos = cameraPosition.target;
                        new LoadPlaces().execute();
                        if(pos != null)
                            pos.remove();
                        pos = map.addMarker(new MarkerOptions().position(currentPos));
                        isRunning = false;
                        if(isErased) {
                            CircleOptions circleOptions = new CircleOptions()
                                    .center(currentPos)
                                    .radius(radius)
                                    .fillColor(0x220000FF)
                                    .strokeColor(Color.TRANSPARENT);

                            circle = map.addCircle(circleOptions);
                            findViewById(R.id.button).setVisibility(View.VISIBLE);
                            isErased = false;
                        }
                    }
                }else {
                    map.clear();
                    markerRef.clear();
                    isErased = true;
                    findViewById(R.id.button).setVisibility(View.INVISIBLE);

                }
            }
        };

        map.setOnCameraChangeListener(listener);

    }

    //transform displaymetrics units into pixels units
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getBaseContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //to calculate distance between 2 locations (in meters)
    public static float distFrom(LatLng pos1, LatLng pos2) {
        double lat1 = pos1.latitude;
        double lng1 = pos1.longitude;
        double lat2 = pos2.latitude;
        double lng2 = pos2.longitude;
        double earthRadius = 6371; //kilometers
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);
        return dist*1000; //to have it in meters

    }

    //to add markers on map following places found
    private void setMarkers(){
        int maximum = 0;
        for(Marker m : markerRef.keySet())
            m.remove();
        markerRef.clear();

        int num = 0;

        Log.v("results", "size : "+ nearPlaces.results.size());
        if(nearPlaces.results!= null)
        for (Place place : nearPlaces.results) {
            double latitude = place.geometry.location.lat; // latitude
            double longitude = place.geometry.location.lng; // longitude
            maximum = Math.max(maximum, (int)distFrom(currentPos, new LatLng(latitude, longitude)));

            //used when search other that by radar
            String mDrawableName = place.types[0];
            String[] str = parseType(types);
            for(int i = 0; i < str.length; i++){
                if(Arrays.asList(place.types).contains(str[i])) {
                    mDrawableName = str[i];
                    break;
                }
            }

            int resID = getResources().getIdentifier(mDrawableName , "drawable", getPackageName());
            Marker m;
            if(resID==0) {
                m = (map.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .title(place.name)));
            }
            else {
                m = (map.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .title(place.name)
                        .icon(BitmapDescriptorFactory.fromResource(resID))));
            }

            markerRef.put(m,place.reference);


               /* builder.include(new LatLng(latitude, longitude));

                // calculating map boundary area
                minLat  = (int) Math.min( user_lat, minLat );
                minLong = (int) Math.min( user_long, minLong);
                maxLat  = (int) Math.max( user_lat, maxLat );
                maxLong = (int) Math.max( user_long, maxLong );*/
            num++;
        }
        Log.v("distance","distance max = " + maximum);
        radius = maximum;
        circle.setCenter(currentPos);
        circle.setRadius(radius);

        Log.v("nawak", "number of places : "+ num);
    }

    //to have types of place in a tab
    private String[] parseType(String types){
        String delims = "[|]";
        String[] tokens = types.split(delims);
        return tokens;
    }


    // An AsyncTask class for accessing the GeoCoding Web Service
    //used for search bar
    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {
            // Creating an instance of Geocoder class

            Geocoder geocoder = new Geocoder(getBaseContext());

            List<Address> addresses = null;

            try {
                // Getting a maximum of 3 Address that matches the input text
                addresses = geocoder.getFromLocationName(locationName[0], 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {

            if(addresses==null || addresses.size()==0){
                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
                return;
            }

            // Adding Markers on Google Map for each matching address
            for(int i=0;i<addresses.size();i++){

                Address address = (Address) addresses.get(i);

                // Creating an instance of GeoPoint, to display in Google Map
                currentPos = new LatLng(address.getLatitude(), address.getLongitude());

                // Locate the first location
                if(i==0)
                    map.animateCamera(CameraUpdateFactory.newLatLng(currentPos));
            }
        }
    }


    /**
     * Background Async Task to Load Google places
     * */
    class LoadPlaces extends AsyncTask<String, String, String> {
        ProgressDialog pDialog;

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(PlacesMapActivity.this);
            pDialog.setMessage(Html.fromHtml("<b>Search</b><br/>Loading Places..."));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            super.onPreExecute();

        }

        /**
         * getting Places JSON
         * */
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

        /**
         * After completing background task Dismiss the progress dialog
         * and show the data in UI
         * Always use runOnUiThread(new Runnable()) to update UI from background
         * thread, otherwise you will get error
         * **/
        protected void onPostExecute(String file_url) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (nearPlaces != null)
                        setMarkers();

                }
            });
            pDialog.dismiss();

        }
    }



}
