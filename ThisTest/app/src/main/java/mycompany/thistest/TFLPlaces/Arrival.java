package mycompany.thistest.TFLPlaces;

import com.google.api.client.util.Key;

import java.io.Serializable;

/**
 * Created by trsq9010 on 12/12/2014.
 */
public class Arrival implements Serializable {
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

    @Key
    String naptanId;

    public String getDestinationName(){
        if(!towards.isEmpty())
            return towards;
        else
            return destinationName;
    }

    public String getLineName(){
        return lineName;
    }

    public String getPlatform(){
        String platNb;
        if(platformName.equals("null"))
            platNb = Character.toString(naptanId.charAt(naptanId.length() - 1));
        else if(platformName.length()>1) {
            platNb = Character.toString(platformName.charAt(platformName.length() - 1));
        }else
            platNb =platformName;
        return platNb;
    }

    public String getDestination(){
        return platformName;
    }

    //display destination of trains
    public String toString(){
        String s = "";
        if(destinationName!=null)
            s =  destinationName + " expected in " + timeToStation/60;

        return s;
    }



}

