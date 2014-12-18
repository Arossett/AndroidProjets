
package mycompany.thistest.TFLPlaces;
import com.google.api.client.util.Key;

import java.util.List;

import mycompany.thistest.AsyncClass.Spots;

public class Station implements Spots{
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
}
