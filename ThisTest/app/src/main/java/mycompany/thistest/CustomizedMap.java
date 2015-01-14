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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import mycompany.thistest.Connectivity.GeocodeTask;
import mycompany.thistest.Interfaces.Spot;
import mycompany.thistest.PlacesSearch.LoadPlaces;
import mycompany.thistest.TFL.BikePoint;
import mycompany.thistest.TFL.LoadPlacesTFL;
import mycompany.thistest.Connectivity.GPSTracker;
import mycompany.thistest.PlacesSearch.Place;
import mycompany.thistest.PlacesSearch.PlacesList;
import mycompany.thistest.TFL.Station;
import mycompany.thistest.TFL.StationsList;
import mycompany.thistest.Utilities.Utils;

/**
 * Created by trsq9010 on 16/12/2014.
 */
public class CustomizedMap  {

    public static final float ZOOM_MIN = 14.5f;
    private static final int SPOT_PLACE = 0;
    private static final int SPOT_STATION = 1;
    // Nearest places
    PlacesList nearPlaces;

    // Map view
    GoogleMap map;

    Activity activity;

    Utils utils;

    //circle used to show perimeter where places were looked
    Circle circle;

    HashMap<Marker, Spot> markerPlaces;

    HashMap<Marker, Spot> markerStations;

    //radius of the perimeter where we look for places
    int radius;

    //last position where places were displayed
    LatLng oldPos;

    //the farest location of marker
    int max_pos;

    String types;

    String transports;

    //marker of the camera, needed to remove marker from previous location
    Marker pos;

    boolean isMoving;

    boolean isConnected;

    public CustomizedMap(GoogleMap m, MainActivity mapActivity){
        activity = mapActivity;
        map = m;
        map.setMyLocationEnabled(true);
        utils = new Utils();
        map.setPadding(0, utils.dpToPx(50, activity.getBaseContext()), 0, 0);
        markerPlaces = new HashMap<Marker, Spot>();
        markerStations = new HashMap<Marker, Spot>();

        pos = null;
        GPSTracker gps = new GPSTracker(activity);
        oldPos = new LatLng(gps.getLatitude(), gps.getLongitude());
        isMoving = true;
        isConnected = true;
        transports = null;
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
                Intent in;
                if(markerPlaces.get(marker)!=null){
                    String reference = markerPlaces.get(marker).getId();
                    in = new Intent(activity.getApplicationContext(),
                            SinglePlaceActivity.class);
                    // Sending place reference id to single place activity stop pretending !!!
                    // place reference id used to get "Place full details"
                    in.putExtra("reference", reference);
                    activity.startActivity(in);
                }else{
                    in = new Intent(activity.getApplicationContext(), TransportActivity.class);
                    in.putExtra("station", markerStations.get(marker));
                    activity.startActivity(in);
                }
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
                    // (or if previous search is empty try to get new places)
                    if (cameraPosition.zoom >= ZOOM_MIN) {
                        if ((utils.distFrom(oldPos, cameraPosition.target) > radius / 2 || markerPlaces.isEmpty()) && isMoving) {
                            updateMap();
                        }
                    } else {
                        //if zoom is too far, clean the map
                        map.clear();
                        circle = null;
                        markerPlaces.clear();
                        markerStations.clear();
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
        ArrayList<Spot> spots = new ArrayList<Spot>(nearPlaces.results);
        setMarkers(spots, SPOT_PLACE);
    }

    //method called by loadplaces when places have been found
    //save the nearest place in the customized map class
    public void setNearStations(List<Spot> sl){
        ArrayList<Spot> spots = (ArrayList<Spot>) sl;
        setMarkers(spots, SPOT_STATION);
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
    public void setTransports(String type){
        transports = type;
        double lat = map.getCameraPosition().target.latitude;
        double lon = map.getCameraPosition().target.longitude;
        new LoadPlacesTFL(transports, lat, lon, radius, activity).execute();
    }

    //to add markers on map following places found
    public void setMarkers(List<Spot> list, int spotType){
        max_pos = 0;
        //clean hash map with previous markets

       HashMap<Marker, Spot> newSpots = new HashMap<Marker, Spot>();

        //for each place, add a market with properties
        if(list!= null && !list.isEmpty()) {

            for (Iterator<Spot> iterator = list.iterator(); iterator.hasNext(); ) {

                Spot s = iterator.next();

                int distance = (int) utils.distFrom(map.getCameraPosition().target, new LatLng(s.getLatitude(), s.getLongitude()));

                //add only places which are inside a circle around the user
                if (distance <= radius) {
                    max_pos = Math.max(max_pos, distance);

                    //used to draw icon following type of the place
                    String mDrawableName = s.getType();

                    //get icon corresponding to the type of the place
                    int resID = activity.getResources().getIdentifier(mDrawableName, "drawable", activity.getPackageName());
                    Marker m;

                    //if the icon has not been found just add a default marker
                    if (resID == 0) {
                        m = (map.addMarker(new MarkerOptions()
                                .position(new LatLng(s.getLatitude(), s.getLongitude()))
                                .title(s.getName())));
                    } else {
                        m = (map.addMarker(new MarkerOptions()
                                .position(new LatLng(s.getLatitude(), s.getLongitude()))
                                .title(s.getName())
                                .icon(BitmapDescriptorFactory.fromResource(resID))));
                    }
                    newSpots.put(m, s);
                } else {
                    //if the place is too far, remove it from list
                    list.remove(iterator);
                }
            }
        }

        switch (spotType){
            case SPOT_PLACE :{
                for (Iterator iterator = markerPlaces.entrySet().iterator(); iterator.hasNext(); ) {
                    HashMap.Entry<Marker, Spot> pairs = (HashMap.Entry<Marker, Spot>)iterator.next();
                    pairs.getKey().remove();
                    iterator.remove();
                }
                markerPlaces = newSpots;

                break;
            }
            case SPOT_STATION :{
                for (Iterator iterator = markerStations.entrySet().iterator(); iterator.hasNext(); ) {
                    HashMap.Entry<Marker, Spot> pairs = (HashMap.Entry<Marker, Spot>)iterator.next();
                    pairs.getKey().remove();
                    iterator.remove();
                }
                markerStations = newSpots;
                break;
            }
            default: {
                break;
            }
        }
    }
}
