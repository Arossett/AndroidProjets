package mycompany.thistest.LoadClasses;

import mycompany.thistest.APIRequests.GooglePlaces;
import mycompany.thistest.GooglePlaces.PlaceDetails;

/**
 * Created by trsq9010 on 16/01/2015.
 */
public class LoadPlaceDetails extends LoadData{

    @Override
    protected Object doInBackground(String... args) {
        String reference = args[0];
        PlaceDetails placeDetails = null;
        // creating Places class object
        GooglePlaces googlePlaces = new GooglePlaces();

        // Check if used is connected to Internet
        try {
            placeDetails = googlePlaces.getPlaceDetails(reference);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return placeDetails;
    }
}
