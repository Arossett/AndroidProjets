package mycompany.thistest.Utilities;

import android.content.Context;
import android.util.DisplayMetrics;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by trsq9010 on 08/12/2014.
 */
public class Utils {
    //to have types of place in a tab
    public String[] parseType(String types){
        String delims = "[|]";
        String[] tokens = types.split(delims);
        return tokens;
    }

    //transform displaymetrics units into pixels units
    public int dpToPx(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    //to calculate distance between 2 locations (in meters)
    public static float distFrom(LatLng pos1, LatLng pos2) {
        double lat1 = pos1.latitude;
        double lng1 = pos1.longitude;
        double lat2 = pos2.latitude;
        double lng2 = pos2.longitude;
        double earthRadius = 6371; //kilometers
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);
        return dist*1000; //to have it in meters

    }

}
