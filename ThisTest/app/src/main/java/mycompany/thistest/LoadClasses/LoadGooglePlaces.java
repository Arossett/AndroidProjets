package mycompany.thistest.LoadClasses;

import com.google.android.gms.maps.model.LatLng;

import mycompany.thistest.PlacesSearch.GooglePlaces;
import mycompany.thistest.PlacesSearch.PlacesList;

/**
 * Created by trsq9010 on 16/01/2015.
 */
public class LoadGooglePlaces extends LoadData {
    double radius;
    String types;
    LatLng currentPos;

    public LoadGooglePlaces(){
        radius = 0;
        types = "";
        currentPos = new LatLng(0,0);
    }

    public LoadGooglePlaces(double r, String t, LatLng pos){
        radius = r;
        types = t;
        currentPos = pos;
    }

    @Override
    protected Object doInBackground(String... args) {
        // creating Places class object
        GooglePlaces googlePlaces = new GooglePlaces();
        PlacesList nearPlaces = null;
        try {

            // get nearest places
            nearPlaces = googlePlaces.search(currentPos.latitude,
                    currentPos.longitude,
                    radius, types);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return nearPlaces;
    }
}
