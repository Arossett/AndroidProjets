package mycompany.thistest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import mycompany.thistest.LoadClasses.LoadGooglePlaces;
import mycompany.thistest.LoadClasses.LoadStationsTFL;
import mycompany.thistest.Connectivity.GPSTracker;
import mycompany.thistest.PlacesSearch.PlacesList;
import mycompany.thistest.Utilities.Utils;

/**
 * Created by trsq9010 on 16/12/2014.
 */
public class CustomizedMap implements Parcelable{

    public static final float ZOOM_MIN = 14.5f;
    private static final int SPOT_PLACE = 0;
    private static final int SPOT_STATION = 1;
    // Nearest places
    private PlacesList nearPlaces;
    LoadGooglePlaces loadPlaces;

    LoadStationsTFL loadStations;
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

    //boolean isMoving;

    boolean isConnected;

    private List<Spot> transportStations;

    public CustomizedMap(GoogleMap m, MainActivity mapActivity){
        loadPlaces = new LoadGooglePlaces();
        loadStations = new LoadStationsTFL();
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
        isConnected = true;
        transports = null;
        radius = activity.getResources().getInteger(R.integer.radius);
        max_pos = 0;

        nearPlaces = null;
        transportStations = null;

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

    public CustomizedMap(Activity a, GoogleMap ma, CustomizedMap m){
        this.markerStations = m.markerStations;
        this.markerPlaces = m.markerPlaces;
        this.loadPlaces = m.loadPlaces;
        this.loadStations = m.loadStations;
        this.utils = m.utils;
        this.circle = m.circle;
        this.map = ma;
        this.radius = m.radius;
        this.oldPos = m.oldPos;
        this.pos = map.addMarker(new MarkerOptions().position(map.getCameraPosition().target));
        this.max_pos = m.max_pos;
        this.types = m.types;
        this.transports = m.transports;
        this.isConnected = m.isConnected;
        this.activity = a;
        map.setMyLocationEnabled(true);
        setLocationButtonListener();
        setCameraListener();
        setMarkersListener();
        setButtonFind();

        this.nearPlaces = m.nearPlaces;
        this.transportStations = m.transportStations;
        if(!this.loadPlaces.isFinished()) {
            searchPlaces();
        }else {
            updateNearPlaces(nearPlaces);
        }
        if(!this.loadStations.isFinished()) {
            searchStations();
        }else {
            updateStations(transportStations);
        }

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
                        if ((utils.distFrom(oldPos, cameraPosition.target) > radius / 2 || markerPlaces.isEmpty()) /*&& isMoving*/) {
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
            searchPlaces();
        }

        if (pos != null) {
            pos.remove();
        }

        //add marker on the new location
        pos = map.addMarker(new MarkerOptions().position(cameraPosition.target));

        //isMoving = false;

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
    public void updateNearPlaces(PlacesList np){
        nearPlaces = np;
        if(nearPlaces!=null) {
            ArrayList<Spot> spots = new ArrayList<Spot>(nearPlaces.results);
            setMarkers(spots, SPOT_PLACE);
        }
    }

    //method called by loadplaces when places have been found
    //save the nearest place in the customized map class
    public void updateStations(List<Spot> sl){

        transportStations = sl;
        setMarkers(sl, SPOT_STATION);
    }

    //true if the user is moving map
   /* public void setIsMoving(boolean b){
        isMoving = b;
    }*/

    //change types of points of interests
    //should reload places
    public void setTypes(String t){
        types = t;
        //new LoadPlaces(activity, radius, types, map.getCameraPosition().target).execute();
        searchPlaces();
    }

    public CameraPosition getCameraPosition(){
        return map.getCameraPosition();
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
        searchStations();
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

                    Bitmap bmp0 = BitmapFactory.decodeResource(activity.getResources(), resID);
                    Bitmap bmp = bmp0.copy(Bitmap.Config.ARGB_8888, true);
                    Canvas canvas1 = new Canvas(bmp);

                    // paint defines the text color,
                    // stroke width, size
                    Paint color = new Paint();
                    color.setTextSize(10);
                    color.setColor(Color.BLACK);

                    //modify canvas
                    canvas1.drawBitmap(BitmapFactory.decodeResource(activity.getResources(),
                            resID), 0,0, color);
                    canvas1.drawText("50",bmp.getWidth()/2,bmp.getHeight()/2, color);

                    //if the icon has not been found just add a default marker
                    if (resID == 0) {
                        m = (map.addMarker(new MarkerOptions()
                                .position(new LatLng(s.getLatitude(), s.getLongitude()))
                                .title(s.getName())));
                    } else {
                        m = (map.addMarker(new MarkerOptions()
                                .position(new LatLng(s.getLatitude(), s.getLongitude()))
                                .title(s.getName())
                                .icon(BitmapDescriptorFactory.fromBitmap(bmp))));
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    private void searchPlaces(){
        ProgressBar pb = (ProgressBar) activity.findViewById(R.id.progressBar);
        pb.setIndeterminate(true);
        pb.setVisibility(View.VISIBLE);

        if(!loadPlaces.isFinished()){
            loadPlaces.cancel(true);
        }

        loadPlaces = new LoadGooglePlaces(radius, types, map.getCameraPosition().target);
        loadPlaces.setMyTaskCompleteListener(new LoadGooglePlaces.OnTaskComplete() {
            @Override
            public void setMyTaskComplete(Object obj) {
                if (obj != null) {
                    updateNearPlaces((PlacesList)obj);
                }
                if((loadStations.isFinished())){
                    ProgressBar pb = (ProgressBar) activity.findViewById(R.id.progressBar);
                    pb.setVisibility(View.INVISIBLE);
                }
            }
        });
        loadPlaces.execute();


    }

    public void searchStations(){
        ProgressBar pb = (ProgressBar) activity.findViewById(R.id.progressBar);
        pb.setIndeterminate(true);
        pb.setVisibility(View.VISIBLE);

        if(!loadStations.isFinished()) {
            loadStations.cancel(true);
        }


        loadStations = new LoadStationsTFL(transports,map.getCameraPosition().target, radius);

        loadStations.setMyTaskCompleteListener(new LoadStationsTFL.OnTaskComplete() {

            @Override
            public void setMyTaskComplete(Object obj) {
                updateStations((List<Spot>)obj);
                if((loadPlaces.isFinished())){
                    ProgressBar pb = (ProgressBar) activity.findViewById(R.id.progressBar);
                    pb.setVisibility(View.INVISIBLE);
                }
            }
        });
        loadStations.execute();
    }
}
