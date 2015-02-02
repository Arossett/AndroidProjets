package mycompany.thistest.PlacesSearch;

import java.io.Serializable;
import java.util.List;

import com.google.api.client.util.Key;

import mycompany.thistest.Spots.Place;

/** Implement this class from "Serializable"
 * So that you can pass this class Object to another using Intents
 * Otherwise you can't pass to another actitivy
 * */
public class PlacesList implements Serializable {

    @Key
    public String status;

    @Key
    public List<Place> results;

    public void setType(String types){
        for(Place p : results)
            p.setType(types);
    }

}