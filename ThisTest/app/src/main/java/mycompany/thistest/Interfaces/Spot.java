package mycompany.thistest.Interfaces;

import java.io.Serializable;

/**
 * Created by trsq9010 on 18/12/2014.
 */
public interface Spot extends Serializable{
    public String getName();

    public String getId();

    public double getLongitude();

    public double getLatitude();

    public String getType();

}
