package mycompany.thistest.NationalRail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by trsq9010 on 12/01/2015.
 */
public class TrainDeparture {
    String destination;
    String departureTime;
    String platform;
    String onTime;
    List<String> callingPoints;

    public TrainDeparture(){
        callingPoints = new ArrayList<String>();
        platform = "";

    }

    public void setDestination(String dest){
        destination = dest;
    }

    public void setDepartureTime(String depTime){
        departureTime = depTime;
    }

    public void setPlatform(String plat){
        platform = plat;
    }

    public void setOnTime(String onTim){
        onTime = onTim;
    }

    public List<String> getCallingPoints(){
        return callingPoints;
    }

    public void setCallingPoints(List<String> points){
        callingPoints = points;
    }

    public String toString(){
        String s = "Platform "+platform+" "+departureTime+" "+destination+"\n"+onTime;
        return s;
    }
}
