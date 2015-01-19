package mycompany.thistest.LoadClasses;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import mycompany.thistest.Interfaces.Spot;
import mycompany.thistest.TFL.TFLSearch;

/**
 * Created by trsq9010 on 16/01/2015.
 */
public class LoadStationsTFL extends LoadData{
    String type;
    double latitude;
    double longitude;
    int radius;

    public LoadStationsTFL(String t, LatLng pos, int r){
        type = t;
        latitude = pos.latitude;
        longitude = pos.longitude;
        radius = r;
    }

    public LoadStationsTFL(){
        type = "";
        latitude = 0;
        longitude = 0;
        radius = 0;
    }

    @Override
    protected Object doInBackground(String... args) {
        TFLSearch stationsSearch = new TFLSearch();
        List<Spot> stationsList = null;
        try {

            // get nearest places
            stationsList = stationsSearch.searchbyArea(type, latitude, longitude, radius);
            //List<Arrivals> arrivals = stationsSearch.searchArrivals();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stationsList;
    }
}
