package mycompany.thistest.Spots;

import com.google.android.gms.maps.model.Marker;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import mycompany.thistest.Interfaces.Spot;
import mycompany.thistest.LoadClasses.LoadData;

/**
 * Created by trsq9010 on 19/01/2015.
 */
public class SpotSearch implements Serializable {
    String type;
    HashMap<Marker, Spot> markers;
    LoadData loadSpots;
    List<Spot> spots;
    LoadData.OnTaskComplete onTaskComplete;
    SpotType spotType;

    public SpotSearch(String typ, HashMap<Marker, Spot> marker, LoadData loadData, SpotType spotTyp){
        type = typ;
        markers = marker;
        loadSpots = loadData;
        spots = null;
        spotType = spotTyp;
    }

    public  SpotSearch(SpotType spotTyp){
        type = "";
        markers = new HashMap<Marker, Spot>();
        loadSpots = new LoadData() {
            @Override
            protected Object doInBackground(String... args) {
                return null;
            }
        };
        spots = null;
        spotType = spotTyp;
    }

    public void setOnTaskComplete(LoadData.OnTaskComplete taskComplete){
        onTaskComplete = taskComplete;
    };

    public void updateMarkers(HashMap<Marker, Spot> newSpots){
        for (Iterator iterator = markers.entrySet().iterator(); iterator.hasNext(); ) {
            HashMap.Entry<Marker, Spot> pairs = (HashMap.Entry<Marker, Spot>)iterator.next();
            pairs.getKey().remove();
            iterator.remove();
        }
        markers = newSpots;
    }

    public void setType(String typ){
        type = typ;
    }

    public HashMap<Marker, Spot> getMarkers(){
        return markers;
    }

    public String getType(){
        return type;
    }

    public LoadData getLoadSpots(){
        return loadSpots;
    }

    public void  setLoadSpots(LoadData loadData){
        loadSpots = loadData;
        loadSpots.setMyTaskCompleteListener(onTaskComplete);
    }

    public void setSpots(List<Spot> sp){
        spots = sp;
    }

    public List<Spot> getSpots(){
        return spots;
    }

    public enum SpotType
    {
        TRANSPORT,
        PLACE
    }

    public SpotType getSearchType(){
        return spotType;
    }



}
