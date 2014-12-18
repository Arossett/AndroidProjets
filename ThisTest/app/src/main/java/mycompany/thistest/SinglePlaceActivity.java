package mycompany.thistest;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;


import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import mycompany.thistest.AsyncClass.LoadPlaceDetails;
import mycompany.thistest.Connectivity.GoogleServicesConnectionDetector;
import mycompany.thistest.Dialogs.AlertDialogManager;
import mycompany.thistest.PlacesSearch.GooglePlaces;
import mycompany.thistest.PlacesSearch.PlaceDetails;

public class SinglePlaceActivity extends Activity {
    // flag for Internet connection status
    Boolean isInternetPresent = false;

    // Connection detector class
    GoogleServicesConnectionDetector cd;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Google Places
    GooglePlaces googlePlaces;

    // Place Details
    PlaceDetails placeDetails;

    // KEY Strings
    public static String KEY_REFERENCE = "reference"; // id of the place

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_place);

        Intent i = getIntent();

        // Place referece id
        String reference = i.getStringExtra(KEY_REFERENCE);

        // Calling a Async Background thread
        new LoadPlaceDetails(SinglePlaceActivity.this, googlePlaces).execute(reference);
    }

    public void setPlaceDetails(PlaceDetails pD){
        placeDetails = pD;
    }

    public void showDetails(){
        if(placeDetails != null){
            String status = placeDetails.status;

            // check place deatils status
            // Check for all possible status
            if(status.equals("OK")){
                if (placeDetails.result != null) {
                    String name = placeDetails.result.name;
                    String address = placeDetails.result.formatted_address;
                    String phone = placeDetails.result.formatted_phone_number;
                    String latitude = Double.toString(placeDetails.result.geometry.location.lat);
                    String longitude = Double.toString(placeDetails.result.geometry.location.lng);
                    String rating = null;
                    if(placeDetails.result.rating!=null)
                        rating = Double.toString(placeDetails.result.rating);

                    Log.d("Place ", name + address + phone + latitude + longitude );

                    // Displaying all the details in the view
                    // single_place.xml
                    TextView lbl_name = (TextView) findViewById(R.id.name);
                    TextView lbl_address = (TextView) findViewById(R.id.address);
                    TextView lbl_phone = (TextView) findViewById(R.id.phone);
                    TextView lbl_location = (TextView) findViewById(R.id.location);
                    RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);

                    // Check for null data from google
                    // Sometimes place details might missing
                    name = name == null ? "Not present" : name; // if name is null display as "Not present"
                    address = address == null ? "Not present" : address;
                    phone = phone == null ? "Not present" : phone;
                    latitude = latitude == null ? "Not present" : latitude;
                    longitude = longitude == null ? "Not present" : longitude;

                    if(rating==null)
                        ratingBar.setVisibility(View.INVISIBLE);
                    else {
                        float fl = Float.parseFloat(rating);
                        ratingBar.setRating(Float.parseFloat(rating));
                    }
                    lbl_name.setText(name);
                    lbl_address.setText(address);
                    lbl_phone.setText(Html.fromHtml("<b>Phone:</b> " + phone));
                    lbl_location.setText(Html.fromHtml("<b>Latitude:</b> " + latitude + ", <b>Longitude:</b> " + longitude));

                }
            }
            else if(status.equals("ZERO_RESULTS")){
                alert.showAlertDialog(SinglePlaceActivity.this, "Near Places",
                        "Sorry no place found.",
                        false);
            }
            else if(status.equals("UNKNOWN_ERROR"))
            {
                alert.showAlertDialog(SinglePlaceActivity.this, "Places Error",
                        "Sorry unknown error occured.",
                        false);
            }
            else if(status.equals("OVER_QUERY_LIMIT"))
            {
                alert.showAlertDialog(SinglePlaceActivity.this, "Places Error",
                        "Sorry query limit to google places is reached",
                        false);
            }
            else if(status.equals("REQUEST_DENIED"))
            {
                alert.showAlertDialog(SinglePlaceActivity.this, "Places Error",
                        "Sorry error occured. Request is denied",
                        false);
            }
            else if(status.equals("INVALID_REQUEST"))
            {
                alert.showAlertDialog(SinglePlaceActivity.this, "Places Error",
                        "Sorry error occured. Invalid Request",
                        false);
            }
            else
            {
                alert.showAlertDialog(SinglePlaceActivity.this, "Places Error",
                        "Sorry error occured.",
                        false);
            }
        }else{
            alert.showAlertDialog(SinglePlaceActivity.this, "Places Error",
                    "Sorry error occured.",
                    false);
        }
    }

}