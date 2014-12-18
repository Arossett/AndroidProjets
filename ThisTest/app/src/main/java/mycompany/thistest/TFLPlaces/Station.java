
package mycompany.thistest.TFLPlaces;
import com.google.api.client.util.Key;

import java.util.List;

import mycompany.thistest.Spot;

public class Station implements Spot {
   // @Key
   	//public String $type;
    //@Key
   	//public List matches;
  /*  @Key
   	public String stationId;

   @Key
    public String name;

    public String getName(){
        return name;
    }

    public String getId(){
        return stationId;
    }*/

    @Key
    String naptanId;

    @Key
    public String commonName;

    @Key
    double lat;

    @Key
    double lon;

    String type;

    List<Arrivals> arrivals;

    public void setArrivals(List<Arrivals> arr){
        arrivals = arr;
    }
    public List<Arrivals> getArrivals(){
        return arrivals;
    }

    public String getNaptanId(){
        return naptanId;
    }

    @Override
    public String getName() {
        return commonName;
    }

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
}
