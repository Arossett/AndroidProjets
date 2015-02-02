package mycompany.thistest.TFL;

import com.google.api.client.util.Key;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mycompany.thistest.Spots.Station;

/**
 * Created by trsq9010 on 12/12/2014.
 */
public class StationsList {

    @Key
    public List<Station> stopPoints;

    public StationsList(){
        stopPoints = new ArrayList<Station>();
    }

    //to check if list has already a reference for a metro station
    //used to merge overground, dlr and tube
    public boolean containsStation(Station station){
        for(Station s : stopPoints){
            if(s.getIcsCode()==station.getIcsCode()&&!(s.getStopType().equals(station.getStopType()))){
                s.addRailId(station.getId());
                return true;
            }
        }
        return  false;
    }

    //update the list of stations found
    //remove stations which are in double (for underground and tube line for example)
    public void updateList(String type){
        for (Iterator<Station> iterator = stopPoints.iterator(); iterator.hasNext(); ) {
            Station s = iterator.next();
            s.setType(type);
            s.initRailsList();
            //if type searched is Metro and station found is overground or DLR
            //check if a station already existed for this place
            if(type.equals("Metro")&&s.getStopType().equals("NaptanRailStation")) {
                if (containsStation(s)){
                    iterator.remove();
                }
            }
        }
    }

}
