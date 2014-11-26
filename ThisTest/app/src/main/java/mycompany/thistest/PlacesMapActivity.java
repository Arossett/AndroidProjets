package mycompany.thistest;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;


public class PlacesMapActivity extends Activity  {
    // Nearest places
    PlacesList nearPlaces;

    // Map view
    GoogleMap map;

   // GPSTracker gps;

    // Map overlay items
    /*List<Overlay> mapOverlays;

    AddItemizedOverlay itemizedOverlay;

    GeoPoint geoPoint;
    // Map controllers
    MapController mc;
    OverlayItem overlayitem;
*/
    double latitude;
    double longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_map);

        // Getting intent data
        Intent i = getIntent();

        // Users current geo location
        String user_latitude = i.getStringExtra("user_latitude");
        String user_longitude = i.getStringExtra("user_longitude");

        // Nearplaces list
        nearPlaces = (PlacesList) i.getSerializableExtra("near_places");
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        //gps = (GPSTracker) i.getSerializableExtra("gps");
        //gps.addListener(this);


/*
        mapView.setBuiltInZoomControls(true);

        mapOverlays = mapView.getOverlays();

        // Geopoint to place on map
        geoPoint = new GeoPoint((int) (Double.parseDouble(user_latitude) * 1E6),
                (int) (Double.parseDouble(user_longitude) * 1E6));

        // Drawable marker icon
        Drawable drawable_user = this.getResources()
                .getDrawable(R.drawable.mark_red);

        itemizedOverlay = new AddItemizedOverlay(drawable_user, this);

        // Map overlay item
        overlayitem = new OverlayItem(geoPoint, "Your Location",
                "That is you!");

        itemizedOverlay.addOverlay(overlayitem);

        mapOverlays.add(itemizedOverlay);
        itemizedOverlay.populateNow();

        // Drawable marker icon
        Drawable drawable = this.getResources()
                .getDrawable(R.drawable.mark_blue);

        itemizedOverlay = new AddItemizedOverlay(drawable, this);

        mc = mapView.getController();
*/
        // These values are used to get map boundary area
        // The area where you can see all the markers on screen
        int minLat = Integer.MAX_VALUE;
        int minLong = Integer.MAX_VALUE;
        int maxLat = Integer.MIN_VALUE;
        int maxLong = Integer.MIN_VALUE;
        double user_lat = Double.parseDouble(user_latitude);
        double user_long = Double.parseDouble(user_longitude);
        final LatLngBounds.Builder builder = new LatLngBounds.Builder();
        // check for null in case it is null
        if (nearPlaces.results != null) {
            // loop through all the places
            for (Place place : nearPlaces.results) {
                latitude = place.geometry.location.lat; // latitude
                longitude = place.geometry.location.lng; // longitude

                String mDrawableName = place.types[0];
                int resID = getResources().getIdentifier(mDrawableName , "drawable", getPackageName());
                Log.d("Drawable",mDrawableName+ " resId : " + resID);
                if(resID==0)
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .title(place.name));
                else
                    map.addMarker(new MarkerOptions()
                                    .position(new LatLng(latitude, longitude))
                                    .title(place.name)
                                    .icon(BitmapDescriptorFactory.fromResource(resID)));
                builder.include(new LatLng(latitude, longitude));

                // calculating map boundary area
                minLat  = (int) Math.min( user_lat, minLat );
                minLong = (int) Math.min( user_long, minLong);
                maxLat  = (int) Math.max( user_lat, maxLat );
                maxLong = (int) Math.max( user_long, maxLong );
            }
            map.setMyLocationEnabled(true);
            LatLng myLocation = new LatLng(user_lat, user_long);

            // map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 1));

            LatLng southwest = new LatLng(minLat, minLong);
            LatLng northeast = new LatLng(maxLat, maxLong);

            LatLngBounds bounds = new LatLngBounds(southwest, northeast );
            map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

                @Override
                public void onCameraChange(CameraPosition arg0) {
                    // Move camera.
                    map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 1));
                    // Remove listener to prevent position reset on camera move.
                    map.setOnCameraChangeListener(null);
                }
            });

            // mapOverlays.add(itemizedOverlay);

            // showing all overlay items
           // itemizedOverlay.populateNow();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
