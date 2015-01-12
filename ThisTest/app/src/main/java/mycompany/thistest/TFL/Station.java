
package mycompany.thistest.TFL;

import com.google.api.client.util.Key;

import java.util.ArrayList;

import mycompany.thistest.Interfaces.Spot;

public class Station implements Spot {

    @Key
    String naptanId;

    @Key
    public String commonName;

    @Key
    double lat;

    @Key
    double lon;

    @Key
    String stopType;

    @Key
    int icsCode;

    String type;

    ArrayList<String> railIds;

    public Station(){
        naptanId = null;
    }

    @Override
    public String getName() {
        return commonName;
    }

    @Override
    public String getId(){
        return naptanId;
    }

    @Override
    public double getLongitude() {
        return lon;
    }

    @Override
    public double getLatitude() {
        return lat;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String s){
        type = s;
    }

    public String getStopType(){
        return stopType;
    }

    public int getIcsCode(){
        return icsCode;
    }

    //to merge stations (overground and tube) from the same place
    public  void initRailsList(){
        railIds = new ArrayList<String>();
        railIds.clear();
    }

    public void addRailId(String id){
        railIds.add(id);
    }

    public ArrayList<String> getRailId(){
        return railIds;
    }



}
