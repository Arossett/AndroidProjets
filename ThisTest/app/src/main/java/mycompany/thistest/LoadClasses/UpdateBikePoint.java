package mycompany.thistest.LoadClasses;

import java.util.List;

import mycompany.thistest.APIRequests.TFLSearch;
import mycompany.thistest.Interfaces.Spot;

/**
 * Created by trsq9010 on 04/02/2015.
 */
public class UpdateBikePoint extends LoadData {
    String id;

    public UpdateBikePoint(String i){
        id = i;
    }

    @Override
    protected Object doInBackground(String... args) {
        TFLSearch stationsSearch = new TFLSearch();
        Spot bikePoint = null;
        try {

            // bike point updated information
            bikePoint = stationsSearch.searchBikePoint(id);
            //List<Arrivals> arrivals = stationsSearch.searchArrivals();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bikePoint;
    }

}
