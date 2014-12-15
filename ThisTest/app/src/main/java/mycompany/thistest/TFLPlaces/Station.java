
package mycompany.thistest.TFLPlaces;
import com.google.api.client.util.Key;

import java.util.List;

public class Station {
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

}
