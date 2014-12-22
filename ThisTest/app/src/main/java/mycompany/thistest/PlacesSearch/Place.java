package mycompany.thistest.PlacesSearch;


import java.io.Serializable;
import java.util.Arrays;

import com.google.api.client.util.Key;

import mycompany.thistest.Interfaces.Spot;
import mycompany.thistest.Utils;

/** Implement this class from "Serializable"
 * So that you can pass this class Object to another using Intents
 * Otherwise you can't pass to another actitivy
 * */
public class Place implements Serializable, Spot {
    @Key
    public String place_id;

    @Key
    public String id;

    @Key
    public String name;

    @Key
    public String reference;

    @Key
    public String icon;

    @Key
    public String vicinity;

    @Key
    public Geometry geometry;

    @Key
    public String formatted_address;

    @Key
    public String formatted_phone_number;

    @Key
    public Double rating;

    @Key
    public String[] types;

    public String type;

    public Place(){
        reference = null;
    }

    @Override
    public String toString() {
        return name + " - " + id + " - " + reference;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return reference;
    }

    @Override
    public double getLongitude() {
        return geometry.location.lng;
    }

    @Override
    public double getLatitude() {
        return geometry.location.lat;
    }

    @Override
    public String getType() {
        return type;
    }

    public static class Geometry implements Serializable
    {
        @Key
        public Location location;
    }

    public static class Location implements Serializable
    {
        @Key
        public double lat;

        @Key
        public double lng;
    }

    public void setType(String t){
        type = types[0];
        Utils utils = new Utils();
        String[] tp = utils.parseType(t);
        //to show only icon corresponding to types searched
        for (int i = 0; i < types.length; i++) {
            if (Arrays.asList(tp).contains(types[i])) {
                type = types[i];
                break;
            }
        }
    }

}