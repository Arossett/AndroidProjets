package mycompany.thistest.Spots;

import com.google.api.client.util.Key;

import java.io.Serializable;
import java.util.List;

import mycompany.thistest.Interfaces.Spot;

/**
 * Created by trsq9010 on 13/01/2015.
 */
public class BikePoint implements Spot{
    @Key
    String commonName;

    @Key
    double lat;

    @Key
    double lon;

    @Key
    String id;

    @Key
    List<Property> additionalProperties;

    String type = "Bike";

    @Override
    public String getName() {
        return commonName;
    }

    @Override
    public String getId() {
        return id;
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

    public static class Property implements Serializable{
        @Key
        String key;

        @Key
        String value;

        public String getKey(){
            return key;
        }

        public String getValue(){
            return value;
        }
    }

    public String getNbBikes(){
        String nb = "";
        for(int i = 0; i<additionalProperties.size(); i++){
            if(additionalProperties.get(i).getKey().equals("NbBikes")){
                nb = additionalProperties.get(i).getValue();
            }
        }
        return nb;
    }

    public String getNbEmptyDocks(){
        String nb = "";
        for(int i = 0; i<additionalProperties.size(); i++){
            if(additionalProperties.get(i).getKey().equals("NbEmptyDocks")){
                nb = additionalProperties.get(i).getValue();
            }
        }
        return nb;
    }

    public String getBikes(){
        String b = "Bikes : "+getNbBikes();
        return b;
    }


    public String getEmpty(){
        String b = "Empty docks : "+getNbEmptyDocks();
        return b;
    }
}
