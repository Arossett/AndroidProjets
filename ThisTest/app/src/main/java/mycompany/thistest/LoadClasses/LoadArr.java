package mycompany.thistest.LoadClasses;

import java.util.ArrayList;
import java.util.List;

import mycompany.thistest.TFL.Arrival;
import mycompany.thistest.TFL.TFLSearch;

/**
 * Created by trsq9010 on 16/01/2015.
 */
public class LoadArr extends LoadData{
    ArrayList<String> stationIds;

    public LoadArr(ArrayList<String> ids){
        stationIds = ids;
    }

    @Override
    protected Object doInBackground(String... args) {
        List<Arrival> arrivals = new ArrayList<Arrival>();
        try {
            for(String stationId : stationIds) {
                arrivals.addAll(new TFLSearch().searchArrivals(stationId));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrivals;
    }
}
