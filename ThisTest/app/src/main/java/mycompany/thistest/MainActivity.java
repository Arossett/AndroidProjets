package mycompany.thistest;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;


public class MainActivity extends FragmentActivity /*implements LocListener*/ {

    // flag for Internet connection status
    Boolean isInternetPresent = false;

    //to check if the map is active
    Boolean mapIsOn;

    // Connection detector class
    ConnectionDetector cd;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Google Places
    GooglePlaces googlePlaces;

    // Places List
    PlacesList nearPlaces;

    // GPS Location
    GPSTracker gps;

    // Button
    Button btnShowOnMap;

    // Progress dialog

    // Places Listview
    ListView lv;

    Location locationTest;

    Boolean firstTime = true;

    //types of place to look for
    String[] types;

    String type;

    // KEY Strings
    public static String KEY_REFERENCE = "reference"; // id of the place
    public static String KEY_NAME = "name"; // name of the place
    public static String KEY_VICINITY = "vicinity"; // Place area name


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Intent i = getIntent();
        nearPlaces = (PlacesList) i.getSerializableExtra("near_places");
        type = (String) i.getSerializableExtra("types");
        firstTime = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*cd = new ConnectionDetector(getApplicationContext());



        // Check if Internet present
        isInternetPresent = cd.isConnectingToInternet();

        if (!isInternetPresent) {
            // Internet Connection is not present
            alert.showAlertDialog(MainActivity.this, "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return;
        }

        // creating GPS Class object
        gps = new GPSTracker(this);
        gps.addListener(this);
        //locationTest = gps.getLocation();


        // check if GPS location can get
        if (gps.canGetLocation()) {
            Log.d("Your Location", "latitude:" + gps.getLatitude() + ", longitude: " + gps.getLongitude());
        } else {
            // Can't get user's current location
            alert.showAlertDialog(MainActivity.this, "GPS Status",
                    "Couldn't get location information. Please enable GPS",
                    false);
            // stop executing code by return
            return;
        }*/

        // Getting listview
        lv = (ListView) findViewById(R.id.list);

        // button show on map
        btnShowOnMap = (Button) findViewById(R.id.btn_show_map);
        btnShowOnMap.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
               finish();
            }
        });

        // calling background Async task to load Google Places
        // After getting places from Google all the data is shown in listview
        //new LoadPlaces().execute();

        /** Button click event for shown on map */
        /*btnShowOnMap.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(servicesConnected() && nearPlaces.status.equals("OK")) {
                    Intent i = new Intent(getApplicationContext(),
                            PlacesMapActivity.class);
                    // Sending user current geo location
                    i.putExtra("user_latitude", Double.toString(gps.getLatitude()));
                    i.putExtra("user_longitude", Double.toString(gps.getLongitude()));

                    // passing near places to map activity
                    i.putExtra("near_places", nearPlaces);
                   // i.putExtra("gps", gps);

                    // staring activity
                    startActivity(i);
                }
            }
        });*/

        /**
         * ListItem click event
         * On selecting a listitem SinglePlaceActivity is launched
         * */
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String reference = ((TextView) view.findViewById(R.id.reference)).getText().toString();

                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        SinglePlaceActivity.class);

                // Sending place refrence id to single place activity
                // place refrence id used to get "Place full details"
                in.putExtra(KEY_REFERENCE, reference);
                startActivity(in);
            }
        });
        firstTime = false;
        createList();
    }
/*
    @Override
    public void locationHasChanged(Location location) {
       if(!firstTime)
        new LoadPlaces().execute();
    }
*/
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
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage(Html.fromHtml("<b>Search</b><br/>Loading Places..."));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Places JSON
         * */
        protected String doInBackground(String... args) {

            // creating Places class object
            googlePlaces = new GooglePlaces();
            try {
                // Separeate your place types by PIPE symbol "|"
                // If you want all types places make it as null
                // Check list of types supported by google
                String type = "cafe|restaurant|bar";
                types = parseType(type);

                // Radius in meters - increase this value if you don't find any places
                double radius = 1000; // 10000 meters
                gps.getLatitude();
                gps.getLongitude();

                // get nearest places
                nearPlaces = googlePlaces.search(gps.getLatitude(),
                        gps.getLongitude(),/*locationTest.getLatitude(), locationTest.getLongitude(),*/
                        radius, type);
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
            // dismiss the dialog after getting all products
            pDialog.dismiss();

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {

                    /**
                     * Updating parsed Places into LISTVIEW
                     * */
                    // Get json response status
                    String status = nearPlaces.status;

                    // Check for all possible status
                    if(status.equals("OK")){

                        // Successfully got places details
                        if (nearPlaces.results != null) {
                            SeparatedAdapter separatedAdapter = new SeparatedAdapter(getBaseContext());


                            for(int i = 0;i<types.length;i++) {
                                ArrayList<HashMap<String, String>> placesListItems = new ArrayList<HashMap<String,String>>();
                               // placesListItems.clear();

                                // loop through each place
                                for (Place p : nearPlaces.results) {
                                    HashMap<String, String> map = new HashMap<String, String>();

                                    // Place reference won't display in listview - it will be hidden
                                    // Place reference is used to get "place full details"
                                    map.put(KEY_REFERENCE, p.reference);

                                    // Place name
                                    map.put(KEY_NAME, p.name);
                                   // adding HashMap to ArrayList
                                    if(Arrays.asList(p.types).contains(types[i])) {
                                        placesListItems.add(map);
                                    }
                                }

                                // list adapter

                             /*   ListAdapter adapter = new SimpleAdapter(MainActivity.this, placesListItems,
                                        R.layout.list_item,
                                        new String[]{KEY_REFERENCE, KEY_NAME}, new int[]{
                                        R.id.reference, R.id.name});*/

                                 separatedAdapter.addSection(types[i], new SimpleAdapter(MainActivity.this, placesListItems,
                                         R.layout.list_item,
                                         new String[]{KEY_REFERENCE, KEY_NAME}, new int[]{
                                         R.id.reference, R.id.name}));

                            }

                            // Adding data into listview

                            lv.setAdapter(separatedAdapter);

                        }
                    }
                    else if(status.equals("ZERO_RESULTS")){
                       ListAdapter adapter = new SimpleAdapter(MainActivity.this, (new ArrayList<HashMap<String,String>>()),
                                R.layout.list_item,
                                new String[] { KEY_REFERENCE, KEY_NAME}, new int[] {
                                R.id.reference, R.id.name });
                        lv.setAdapter(adapter);
                        // Zero results found
                        alert.showAlertDialog(MainActivity.this, "Near Places",
                                "Sorry no places found. Check if your gps is available and try again.",
                                false);
                    }
                    else if(status.equals("UNKNOWN_ERROR"))
                    {
                        alert.showAlertDialog(MainActivity.this, "Places Error",
                                "Sorry unknown error occured.",
                                false);
                    }
                    else if(status.equals("OVER_QUERY_LIMIT"))
                    {
                        alert.showAlertDialog(MainActivity.this, "Places Error",
                                "Sorry query limit to google places is reached",
                                false);
                    }
                    else if(status.equals("REQUEST_DENIED"))
                    {
                        alert.showAlertDialog(MainActivity.this, "Places Error",
                                "Sorry error occured. Request is denied",
                                false);
                    }
                    else if(status.equals("INVALID_REQUEST"))
                    {
                        alert.showAlertDialog(MainActivity.this, "Places Error",
                                "Sorry error occured. Invalid Request",
                                false);
                    }
                    else
                    {
                        alert.showAlertDialog(MainActivity.this, "Places Error",
                                "Sorry error occured.",
                                false);
                    }
                }
            });

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;
        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }
        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }



    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates",
                    "Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {

            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    resultCode,
                    this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);


            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment =
                        new ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);

                // Set the dialog in the DialogFragment
                // Show the error dialog in the DialogFragment
                errorFragment.show(getFragmentManager(),
                        "Location Updates");
            }


        }
        return false;
    }

    private String[] parseType(String types){
        String delims = "[|]";
        String[] tokens = types.split(delims);
        return tokens;
    }

    private void createList() {
        if (nearPlaces != null) {
            String status = nearPlaces.status;

            // Check for all possible status
            if (status.equals("OK")) {
                // Successfully got places details
                if (nearPlaces.results != null) {

                    SeparatedAdapter separatedAdapter = new SeparatedAdapter(getBaseContext());

                    types = parseType(type);
                    for (int i = 0; i < types.length; i++) {

                        ArrayList<HashMap<String, String>> placesListItems = new ArrayList<HashMap<String, String>>();
                        // placesListItems.clear();

                        // loop through each place
                        for (Place p : nearPlaces.results) {
                            HashMap<String, String> map = new HashMap<String, String>();

                            // Place reference won't display in listview - it will be hidden
                            // Place reference is used to get "place full details"
                            map.put(KEY_REFERENCE, p.reference);

                            // Place name
                            map.put(KEY_NAME, p.name);
                            // adding HashMap to ArrayList
                            if (Arrays.asList(p.types).contains(types[i])) {
                                placesListItems.add(map);
                            }
                        }


                        separatedAdapter.addSection(types[i], new SimpleAdapter(MainActivity.this, placesListItems,
                                R.layout.list_item,
                                new String[]{KEY_REFERENCE, KEY_NAME}, new int[]{
                                R.id.reference, R.id.name}));

                    }

                    // Adding data into listview
                    lv.setAdapter(separatedAdapter);

                }
            }
        /*else if(status.equals("ZERO_RESULTS")){
            ListAdapter adapter = new SimpleAdapter(MainActivity.this, (new ArrayList<HashMap<String,String>>()),
                    R.layout.list_item,
                    new String[] { KEY_REFERENCE, KEY_NAME}, new int[] {
                    R.id.reference, R.id.name });
            lv.setAdapter(adapter);
            // Zero results found
            alert.showAlertDialog(MainActivity.this, "Near Places",
                    "Sorry no places found. Check if your gps is available and try again.",
                    false);
        }
        else if(status.equals("UNKNOWN_ERROR"))
        {
            alert.showAlertDialog(MainActivity.this, "Places Error",
                    "Sorry unknown error occured.",
                    false);
        }
        else if(status.equals("OVER_QUERY_LIMIT"))
        {
            alert.showAlertDialog(MainActivity.this, "Places Error",
                    "Sorry query limit to google places is reached",
                    false);
        }
        else if(status.equals("REQUEST_DENIED"))
        {
            alert.showAlertDialog(MainActivity.this, "Places Error",
                    "Sorry error occured. Request is denied",
                    false);
        }
        else if(status.equals("INVALID_REQUEST"))
        {
            alert.showAlertDialog(MainActivity.this, "Places Error",
                    "Sorry error occured. Invalid Request",
                    false);
        }
        else
        {
            alert.showAlertDialog(MainActivity.this, "Places Error",
                    "Sorry error occured.",
                    false);
        }*/
        }
    }

}