package mycompany.thistest.TFLPlaces;

import com.google.api.client.util.Key;

/**
 * Created by trsq9010 on 12/12/2014.
 */
public class Arrivals {
    @Key
    String lineName;

    @Key
    String destinationName;

    @Key
    int timeToStation;

    @Key
    String towards;

    @Key
    String platformName;

    public String getDestinationName(){
        return destinationName;
    }


    //display destination of trains
    public String toString(){
        String s = "";
        if(destinationName!=null                )
            s = platformName + " for " + destinationName + " expected in " + timeToStation/60;
        else
            s = platformName + " " + lineName + " Line expected in " + timeToStation/60;
        return s;
    }



}

