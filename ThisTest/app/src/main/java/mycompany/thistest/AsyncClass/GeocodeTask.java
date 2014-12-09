package mycompany.thistest.AsyncClass;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import mycompany.thistest.PlacesMapActivity;


public class GeocodeTask extends AsyncTask<String, Void, List<Address>> {

   private Context context;
   LatLng posFound;
   Activity activity;

    public GeocodeTask(Activity a){
       activity = a;
       posFound = new LatLng(0,0);


   }

   @Override
   protected List<Address> doInBackground(String... locationName) {
       // Creating an instance of Geocoder class
        Geocoder geocoder = new Geocoder(activity.getBaseContext());

       List<Address> addresses = null;

       Log.v("SearchBar", "location to found " + locationName[0]);

       try {
           // Getting a maximum of 3 Address that matches the input text
           addresses = geocoder.getFromLocationName(locationName[0], 3);
       } catch (IOException e) {
           e.printStackTrace();
           Log.e("searchtask", e.toString());
       }
       return addresses;
   }

   @Override
   protected void onPostExecute(List<Address> addresses) {

       if(addresses==null || addresses.size()==0){
           Toast.makeText(context, "No Location found", Toast.LENGTH_SHORT).show();
           return;
       }

       // Adding Markers on Google Map for each matching address
       for(int i=0;i<addresses.size();i++){

           Address address = (Address) addresses.get(i);

           // Creating an instance of GeoPoint, to display in Google Map
           posFound = new LatLng(address.getLatitude(), address.getLongitude());
           if(activity instanceof PlacesMapActivity) {
               PlacesMapActivity pl = (PlacesMapActivity) activity;
               //pl.setCurrentPos(posFound);
               pl.getMap().animateCamera(CameraUpdateFactory.newLatLng(posFound));
           }

       }
   }

}