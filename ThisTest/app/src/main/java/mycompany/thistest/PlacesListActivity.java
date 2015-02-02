package mycompany.thistest;

import android.app.Dialog;
import android.app.DialogFragment;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import mycompany.thistest.Dialogs.AlertDialogManager;
import mycompany.thistest.Spots.Place;
import mycompany.thistest.PlacesSearch.PlacesList;
import mycompany.thistest.Utilities.SeparatedAdapter;


public class PlacesListActivity extends FragmentActivity /*implements LocListener*/ {


    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Places List
    PlacesList nearPlaces;

    // Button
    Button btnShowOnMap;

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


                        separatedAdapter.addSection(types[i], new SimpleAdapter(PlacesListActivity.this, placesListItems,
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