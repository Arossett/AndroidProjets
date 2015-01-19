package mycompany.thistest.TFL;

import android.util.Log;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import mycompany.thistest.Interfaces.Spot;

/**
 * Created by trsq9010 on 12/12/2014.
 */
public class TFLSearch {
    /** Global instance of the HTTP transport. */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    // Google Places serach url's
    private static final String ARRIVALS_SEARCH_URL = "http://api.tfl.gov.uk/StopPoint/Bids/Arrivals";
    private static final String PLACES_SEARCH_BY_AREA = "http://api.tfl.gov.uk/StopPoint?";
    private static final String BIKE_POINTS = "http://api.tfl.gov.uk/BikePoint?";

    public List<Spot> searchbyArea(String type, double latitude, double longitude, int radius)
            throws Exception {

        if(type.equals("Bike"))
            return searchBikePoints(latitude, longitude, radius);
        try {

            HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);

            HttpRequest request = httpRequestFactory
                    .buildGetRequest(new GenericUrl(PLACES_SEARCH_BY_AREA));

            request.getUrl().put("lat", latitude);
            request.getUrl().put("lon", longitude);
            request.getUrl().put("radius", radius);

            if(type.equals("Metro")){
                request.getUrl().put("stopTypes", "NaptanRailStation + NaptanMetroStation");
                request.getUrl().put("modes", "tube + overground + dlr");
            }
            else if(type.equals("Train")){
                request.getUrl().put("stopTypes", "NaptanRailStation");
                request.getUrl().put("modes", "national-rail");
            }
            else if(type.equals("Bus")){
                request.getUrl().put("stopTypes", "NaptanPublicBusCoachTram");
                request.getUrl().put("modes", "bus");
            }

            StationsList list = request.execute().parseAs(StationsList.class);
            list.updateList(type);
            ArrayList<Spot> spots = new ArrayList<Spot>(list.stopPoints);
            return spots;

        } catch (JsonSyntaxException e) {

            Log.e("JsonError:", e.getMessage());
            return null;
        }

    }

    public List<Spot> searchBikePoints(double latitude, double longitude, int radius)
            throws Exception {

        try {

            HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);

            HttpRequest request = httpRequestFactory
                    .buildGetRequest(new GenericUrl(BIKE_POINTS));

            request.getUrl().put("lat", latitude);
            request.getUrl().put("lon", longitude);
            request.getUrl().put("radius", radius);

            BikePointsList list = request.execute().parseAs(BikePointsList.class);
            ArrayList<Spot> spots = new ArrayList<Spot>(list.places);

            return spots;

        } catch (JsonSyntaxException e) {

            Log.e("JsonError:", e.getMessage());
            return null;
        }

    }


    public List<Arrival> searchArrivals(String id)
            throws Exception {

        try {

            HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);

            HttpRequest request = httpRequestFactory
                    .buildGetRequest(new GenericUrl(ARRIVALS_SEARCH_URL));

            request.getUrl().put("ids", id);
            Gson gson = new Gson();

            Type ListType = new TypeToken<List<Arrival>>(){}.getType();
            List<Arrival> enums = gson.fromJson(request.execute().parseAsString(), ListType);

            return enums;

        } catch (Exception e) {

            Log.e("ArrivalsError:", e.getMessage());
        }
        return null;

    }

    /**
     * Creating http request Factory
     * */
    public static HttpRequestFactory createRequestFactory(
            final HttpTransport transport) {
        return transport.createRequestFactory(new HttpRequestInitializer() {
            public void initialize(HttpRequest request) {
                JsonObjectParser parser = new JsonObjectParser(new JacksonFactory());
                request.setParser(parser);
            }
        });
    }
}
