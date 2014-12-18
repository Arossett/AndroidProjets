package mycompany.thistest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.internal.f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import mycompany.thistest.AsyncClass.GeocodeTask;
import mycompany.thistest.AsyncClass.LoadPlaces;
import mycompany.thistest.AsyncClass.LoadPlacesTFL;
import mycompany.thistest.Connectivity.GPSTracker;
import mycompany.thistest.PlacesSearch.Place;
import mycompany.thistest.PlacesSearch.PlacesList;

/**
 * Created by trsq9010 on 16/12/2014.
 */
public class CustomizedMap {

    public static final float ZOOM_MIN = 14.5f;
    // Nearest places
    PlacesList nearPlaces;
    // Map view
    GoogleMap map;

    Activity activity;

    Utils utils;

    //circle used to show perimeter where places were looked
    Circle circle;

    //to get the reference of a particular marker (used to display details about a place from map)
    HashMap<Marker, String> markerRef;

    //radius of the perimeter where we look for places
    int radius;

    //last position where places were displayed
    LatLng oldPos;

    //the farest location of marker
    int max_pos;

    String types;

    String[] transports;

    //marker of the camera, needed to remove marker from previous location
    Marker pos;

    boolean isMoving;

    boolean isConnected;

    public CustomizedMap(GoogleMap m, PlacesMapActivity mapActivity){
        activity = mapActivity;
        map = m;
        map.setMyLocationEnabled(true);
        utils = new Utils();
        map.setPadding(0, utils.dpToPx(50, activity.getBaseContext()), 0, 0);
        markerRef = new HashMap<Marker, String>();
        pos = null;
        GPSTracker gps = new GPSTracker(activity);
        oldPos = new LatLng(gps.getLatitude(), gps.getLongitude());
        isMoving = true;
        isConnected = true;
        transports = null;

        //currentPos = new LatLng(user_lat, user_long);
        radius = activity.getResources().getInteger(R.integer.radius);
        max_pos = 0;

        CircleOptions circleOptions = new CircleOptions()
                .center(oldPos)
                .radius(max_pos)
                .fillColor(0x220000FF)
                .strokeColor(Color.TRANSPARENT);
        circle = map.addCircle(circleOptions);

        setLocationButtonListener();
        setCameraListener();
        setMarkersListener();
        setButtonFind();
    }


    private void setLocationButtonListener(){
        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                GPSTracker gps = new GPSTracker(activity);
                if(!gps.canGetLocation()) {
                    Toast.makeText(activity.getBaseContext(), R.string.not_location_access, Toast.LENGTH_SHORT).show();
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

    public GoogleMap getMap(){
        return map;
    }

    //add listener to handle markers when touched
    private void setMarkersListener(){
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String reference = markerRef.get(marker);

                // Starting new intent
                Intent in = new Intent(activity.getApplicationContext(),
                        SinglePlaceActivity.class);

                // Sending place refrence id to single place activity stop pretending !!!
                // place refrence id used to get "Place full details"
                in.putExtra("reference", reference);
                activity.startActivity(in);
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



    //to initialize and set camera listener (to do in OnCreate)
    private void setCameraListener(){
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(oldPos,(float)(ZOOM_MIN+0.5)));

        final GoogleMap.OnCameraChangeListener listener = new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                //check if internet is enabled
                if (isConnected) {
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
                        activity.findViewById(R.id.button).setVisibility(View.INVISIBLE);
                    }
                } else {
                    //if no connection, advert user
                    Toast.makeText(activity.getBaseContext(), R.string.connection_lost, Toast.LENGTH_SHORT).show();
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

        //search near places around new location
        if(types!=null) {
            new LoadPlaces(activity, radius, types, cameraPosition.target).execute();
        }

        if (pos != null) {
            pos.remove();
        }

        //add marker on the new location
        pos = map.addMarker(new MarkerOptions().position(cameraPosition.target));

        isMoving = false;

        //draw a circle surrounding all places found
        if (circle == null) {
            CircleOptions circleOptions = new CircleOptions()
                    .center(cameraPosition.target)
                    .radius(max_pos)
                    .fillColor(0x220000FF)
                    .strokeColor(Color.TRANSPARENT);
            circle = map.addCircle(circleOptions);
            activity.findViewById(R.id.button).setVisibility(View.VISIBLE);
        }

        //if transports should be found
        if(transports !=null)
            setTransports(transports);
    }

    //method called by loadplaces when places have been found
    //save the nearest place in the customized map class
    public void setNearPlaces(PlacesList np){
        nearPlaces = np;
        setMarkers();
    }

    //true if the user is moving map
    public void setIsMoving(boolean b){
        isMoving = b;
    }

    //change types of points of interests
    //should reload places
    public void setTypes(String t){
        types = t;
        new LoadPlaces(activity, radius, types, map.getCameraPosition().target).execute();
    }

    public CameraPosition getCameraPosition(){
        return map.getCameraPosition();
    }

    public void animateCamera(CameraUpdate camUpdate){
        map.animateCamera(camUpdate);
    }

    //to use if beginning's location should be different from user's one
    public void setOldPos(LatLng pos){
        oldPos = pos;
    }

    //used to update connection state for each change
    public void setIsConnected(boolean b){
        isConnected = b;
    }

    //return near places
    public PlacesList getNearPlaces(){
        return nearPlaces;
    }

    //to add search button listener
    private void setButtonFind(){
        try {
            //to set research bar
            Button btn_find = (Button) activity.findViewById(R.id.btn_find);
            View.OnClickListener findClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //get location edited by user
                    if (isConnected) {
                        EditText etLocation = (EditText) activity.findViewById(R.id.et_location);
                        String location = etLocation.getText().toString();

                        //search it on the map
                        if (location != null && !location.equals("")) {
                            new GeocodeTask(activity, map).execute(location);
                        }
                    } else {
                        Toast.makeText(activity.getBaseContext(), R.string.connection_lost, Toast.LENGTH_SHORT).show();
                    }
                }
            };
            btn_find.setOnClickListener(findClickListener);
        }catch (Exception e) {
            Log.e("FindButton", "doesn't exist");
        }
    }

    //to save transports mode chosen by user
    public void setTransports(String[] type){
        transports = type;
        double lat = map.getCameraPosition().target.latitude;
        double lon = map.getCameraPosition().target.longitude;
        for(String s : type)
            new LoadPlacesTFL(s,lat, lon, radius).execute();
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

                //add only places which are inside a circle around the user
                if (distance <= radius) {
                    max_pos = Math.max(max_pos, distance);

                    //used to draw icon following type of the place
                    String mDrawableName = place.types[0];
                    String[] str = utils.parseType(types);

                    //to show only icon corresponding to types searched
                    for (int i = 0; i < str.length; i++) {
                        if (Arrays.asList(place.types).contains(str[i])) {
                            mDrawableName = str[i];
                            break;
                        }
                    }

                    //get icon corresponding to the type of the place
                    int resID = activity.getResources().getIdentifier(mDrawableName, "drawable", activity.getPackageName());
                    Marker m;
                    //if the icon has not been found just add a default marker
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
                    //if the place is too far, remove it from list
                    iterator.remove();
                }
            }
        }
        Log.v("distance", "distance max = " + max_pos);
        //update circle
        circle.setCenter(map.getCameraPosition().target);
        circle.setRadius(max_pos);
    }


}
