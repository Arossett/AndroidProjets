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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import mycompany.thistest.Connectivity.GeocodeTask;
import mycompany.thistest.Interfaces.Spot;
import mycompany.thistest.LoadClasses.LoadData;
import mycompany.thistest.LoadClasses.LoadGooglePlaces;
import mycompany.thistest.LoadClasses.LoadStationsTFL;
import mycompany.thistest.Connectivity.GPSTracker;
import mycompany.thistest.GooglePlaces.PlacesList;
import mycompany.thistest.Spots.BikePoint;
import mycompany.thistest.Spots.SpotSearch;
import mycompany.thistest.Utilities.Utils;

/**
 * Created by trsq9010 on 16/12/2014.
 */
public class CustomizedMap implements Parcelable{

    public static final float ZOOM_MIN = 14.5f;

    // Nearest places
    private PlacesList nearPlaces;

    // Map view
    GoogleMap map;

    Activity activity;

    Utils utils;

    //circle used to show perimeter where places were looked
    //Circle circle;

    //radius of the perimeter where we look for places
    int radius;

    //last position where places were displayed
    LatLng oldPos;

    //the farest location of marker
    int max_pos;

    //marker of the camera, needed to remove marker from previous location
    Marker pos;

    boolean isConnected;

    SpotSearch searchPlace;

    final SpotSearch searchStation;

    Timer myTimer;

    public CustomizedMap(GoogleMap m, MapActivity mapActivity){
        searchPlace = new SpotSearch(SpotSearch.SpotType.PLACE);
        searchStation = new SpotSearch(SpotSearch.SpotType.TRANSPORT);
        activity = mapActivity;
        map = m;
        map.setMyLocationEnabled(true);
        utils = new Utils();
        map.setPadding(0, utils.dpToPx(50, activity.getBaseContext()), 0, 0);
        pos = null;
        GPSTracker gps = new GPSTracker(activity);
        oldPos = new LatLng(gps.getLatitude(), gps.getLongitude());
        isConnected = true;
        radius = activity.getResources().getInteger(R.integer.radius);
        max_pos = 0;
        myTimer = null;
        nearPlaces = null;

       /*CircleOptions circleOptions = new CircleOptions()
                .center(oldPos)
                .radius(max_pos)
                .fillColor(0x220000FF)
                .strokeColor(Color.TRANSPARENT);
        circle = map.addCircle(circleOptions);*/

        setLocationButtonListener();
        setCameraListener();
        setMarkersListener();
        setButtonFind();
        setListeners();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        /*dest.writeSerializable(searchPlace);
        dest.writeSerializable(searchStation);
        dest.writeInt(radius);
        dest.writeDouble(oldPos.latitude);
        dest.writeDouble(oldPos.longitude);
        dest.writeInt(max_pos);
        dest.writeInt( isConnected ? 1 :0 );
        dest.writeSerializable(nearPlaces);*/
    }

    public void updateMap(Activity a, GoogleMap ma){
        this.utils = new Utils();
        //this.circle = m.circle;
        this.map = ma;
        this.activity = a;
        this.pos = map.addMarker(new MarkerOptions().position(map.getCameraPosition().target));
        setLocationButtonListener();
        setCameraListener();
        setMarkersListener();
        setButtonFind();
        setListeners();
        if(myTimer!=null){
            MyTimerTask myTimerTask= new MyTimerTask();
            myTimer.scheduleAtFixedRate(myTimerTask, 10000, 10000);
        }

        if(!this.searchPlace.getLoadSpots().isFinished()) {
            search(searchPlace);
        }else {
            setMarkers(searchPlace);
        }
        if(!this.searchStation.getLoadSpots().isFinished()) {
            search(searchStation);
        }else {
            setMarkers(searchStation);
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

    //add listener to handle markers when touched
    private void setMarkersListener(){
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent in;
                if(searchPlace.getMarkers().get(marker)!=null){
                    String reference = searchPlace.getMarkers().get(marker).getId();
                    in = new Intent(activity.getApplicationContext(),
                            SinglePlaceActivity.class);
                    // Sending place reference id to single place activity stop pretending !!!
                    // place reference id used to get "Place full details"
                    in.putExtra("reference", reference);
                    activity.startActivity(in);
                }else{
                    in = new Intent(activity.getApplicationContext(), TransportActivity.class);
                    in.putExtra("station", searchStation.getMarkers().get(marker));
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
                    //or if previous search is empty try to get new places
                    if (cameraPosition.zoom >= ZOOM_MIN) {
                        if ((utils.distFrom(oldPos, cameraPosition.target) > radius / 2 || searchPlace.getMarkers().isEmpty()) /*&& isMoving*/) {
                            updateMap();
                        }
                    } else {
                        //if zoom is too far, clean the map
                        map.clear();
                        //circle = null;
                        searchPlace.getMarkers().clear();
                        searchStation.getMarkers().clear();
                        activity.findViewById(R.id.button).setVisibility(View.INVISIBLE);
                        if(myTimer!=null) {
                            myTimer.cancel();
                        }
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
        if(searchPlace.getType()!=null) {
            search(searchPlace);
        }
        if (pos != null) {
            pos.remove();
        }

        //add marker on the new location
        pos = map.addMarker(new MarkerOptions().position(cameraPosition.target));

        //draw a circle surrounding all places found
        /*if (circle == null) {
            CircleOptions circleOptions = new CircleOptions()
                    .center(cameraPosition.target)
                    .radius(max_pos)
                    .fillColor(0x220000FF)
                    .strokeColor(Color.TRANSPARENT);
            circle = map.addCircle(circleOptions);
            activity.findViewById(R.id.button).setVisibility(View.VISIBLE);
        }*/

        //if transports should be found
        if(searchStation.getType() !=null) {
           // setTransports(searchStation.getType());
            search(searchStation);
        }
    }

    //change types of points of interests
    //should reload places
    public void updatePlaces(String t){
        searchPlace.setType(t);
        search(searchPlace);
    }


    //to save transports mode chosen by user
    public void updateStations(String type){
        searchStation.setType(type);
        if(type.equals("Bike")){
            myTimer = new Timer();
            MyTimerTask myTimerTask= new MyTimerTask();
            myTimer.scheduleAtFixedRate(myTimerTask, 10000, 10000);
        }else if(myTimer!=null){
            myTimer.cancel();
            myTimer = null;
        }
        search(searchStation);
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

    //to add markers on map following places found
    public void setMarkers(SpotSearch search){
        max_pos = 0;

       HashMap<Marker, Spot> newSpots = new HashMap<Marker, Spot>();

        //for each place, add a market with properties
        if(search.getSpots()!= null && !search.getSpots().isEmpty()) {

            for (Iterator<Spot> iterator = search.getSpots().iterator(); iterator.hasNext(); ) {

                Spot s = iterator.next();

                int distance = (int) utils.distFrom(map.getCameraPosition().target, new LatLng(s.getLatitude(), s.getLongitude()));

                //add only places which are inside a circle around the user
                if (distance <= radius) {
                    max_pos = Math.max(max_pos, distance);

                    //used to draw icon following type of the place
                    String mDrawableName = s.getType().toLowerCase();
                    Log.v("naaame", mDrawableName);
                    //get icon corresponding to the type of the place
                    int resID = activity.getResources().getIdentifier(mDrawableName, "drawable", activity.getPackageName());
                    Marker m;
                    Bitmap bmp = BitmapFactory.decodeResource(activity.getResources(), resID);
                    if(resID == 0) {
                        resID = activity.getResources().getIdentifier("def", "drawable", activity.getPackageName());
                        bmp = BitmapFactory.decodeResource(activity.getResources(), resID);
                    }

                    if(mDrawableName.equals("bike")) {
                        bmp = bmp.copy(Bitmap.Config.ARGB_8888, true);
                        Canvas canvas1 = new Canvas(bmp);

                        // paint defines the text color,
                        // stroke width, size
                        Paint color = new Paint();
                        color.setTextSize(20);
                        color.setColor(Color.BLACK);
                        //modify canvas
                        canvas1.drawBitmap(BitmapFactory.decodeResource(activity.getResources(),
                                resID), 0, 0, color);
                        canvas1.drawText(((BikePoint)s).getNbBikes(), bmp.getWidth() / 3, bmp.getHeight() / 2, color);
                    }

                        m = (map.addMarker(new MarkerOptions()
                                .position(new LatLng(s.getLatitude(), s.getLongitude()))
                                .title(s.getName())
                                .icon(BitmapDescriptorFactory.fromBitmap(bmp))));
                    newSpots.put(m, s);
                } else {
                    //if the place is too far, remove it from list
                    search.getSpots().remove(iterator);
                }
            }
        }
        search.updateMarkers(newSpots);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void setListeners(){
        LoadData.OnTaskComplete onTaskComplete2 = (new LoadStationsTFL.OnTaskComplete() {

            @Override
            public void setMyTaskComplete(Object obj) {
                searchStation.setSpots((List<Spot>) obj);
                setMarkers(searchStation);
                if ((searchPlace.getLoadSpots().isFinished())) {
                    ProgressBar pb = (ProgressBar) activity.findViewById(R.id.progressBar);
                    pb.setVisibility(View.INVISIBLE);
                }
            }
        });

        LoadData.OnTaskComplete onTaskComplete1 = (new LoadGooglePlaces.OnTaskComplete() {
            @Override
            public void setMyTaskComplete(Object obj) {
                if (obj != null) {
                    nearPlaces = (PlacesList) obj;
                    if(nearPlaces!=null) {
                        searchPlace.setSpots(new ArrayList<Spot>(nearPlaces.results));
                        setMarkers(searchPlace);
                    }
                }
                if ((searchStation.getLoadSpots().isFinished())) {
                    ProgressBar pb = (ProgressBar) activity.findViewById(R.id.progressBar);
                    pb.setVisibility(View.INVISIBLE);
                }
            }
        });

        searchPlace.setOnTaskComplete(onTaskComplete1);
        searchStation.setOnTaskComplete((onTaskComplete2));
    }

        public void search(SpotSearch spotSearch){
            ProgressBar pb = (ProgressBar) activity.findViewById(R.id.progressBar);
            pb.setIndeterminate(true);
            pb.setVisibility(View.VISIBLE);

            if(!spotSearch.getLoadSpots().isFinished()) {
                spotSearch.getLoadSpots().cancel(true);
            }
            switch (spotSearch.getSearchType()){
                case TRANSPORT:
                    spotSearch.setLoadSpots(new LoadStationsTFL(searchStation.getType(),map.getCameraPosition().target, radius));
                    break;
                case PLACE:
                    searchPlace.setLoadSpots(new LoadGooglePlaces(radius, searchPlace.getType(), map.getCameraPosition().target));
                    break;
                default:
                    break;
            }
            spotSearch.getLoadSpots().execute();
        }


    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            Log.v("alarmTime", "ring");
            activity.runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       search(searchStation);
                   }
               }
            );
        }
    }

    public void onPause(){
        if(myTimer!=null) {
            myTimer.cancel();
        }
    }

}
