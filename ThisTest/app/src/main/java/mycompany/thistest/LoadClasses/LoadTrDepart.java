package mycompany.thistest.LoadClasses;

import java.util.ArrayList;
import java.util.List;

import mycompany.thistest.NationalRail.RailHandler;
import mycompany.thistest.NationalRail.TrainDeparture;
import mycompany.thistest.NationalRail.XMLParser;

/**
 * Created by trsq9010 on 16/01/2015.
 */
public class LoadTrDepart extends LoadData{

    String stationCode;

    public LoadTrDepart(String name){
        stationCode = name;
    }

    @Override
    protected Object doInBackground(String... args) {
        List<String> trains = new ArrayList<String>();
        // Check if used is connected to Internet
        try {
            RailHandler ex = new RailHandler();
            String s = ex.getResponseString(stationCode);
            XMLParser p = new XMLParser();
            List<TrainDeparture> departures = p.DepartureParser(s);
            for(TrainDeparture td : departures){
                trains.add(td.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trains;
    }
}
