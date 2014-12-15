package mycompany.thistest.TFLPlaces;

import android.util.Log;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpParser;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

/**
 * Created by trsq9010 on 12/12/2014.
 */
public class TFLSearch {
    /** Global instance of the HTTP transport. */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    // Google API Key
    private static final String API_KEY = "AIzaSyCEKx1Gh_u5r8yEulPe3MyLH_ZOcrPgHTc";

    // Google Places serach url's
    private static final String PLACES_SEARCH_URL = "http://api.tfl.gov.uk/StopPoint/Search/";
    private static final String ARRIVALS_SEARCH_URL = "http://api.tfl.gov.uk/StopPoint/Bids/Arrivals";
    private static final String PLACES_SEARCH_BY_AREA = "http://http://api.tfl.gov.uk/StopPoint?";

    private double _latitude;
    private double _longitude;
    private double _radius;

    /**
     * Searching places

     * @return list of places
     * */
    /*public StationsList search(String s)
            throws Exception {

        try {

            HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);

            HttpRequest request = httpRequestFactory
                    .buildGetRequest(new GenericUrl(PLACES_SEARCH_URL));

            request.getUrl().put("query", s);

            request.getUrl().put("modes", "tube + overground");
            StationsList list = request.execute().parseAs(StationsList.class);

            return list;

        } catch (JsonSyntaxException e) {

            Log.e("JsonError:", e.getMessage());
            return null;
        }

    }*/

    public StationsList searchbyArea(String type, double latitude, double longitude, double radius)
            throws Exception {

        try {

            HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);

            HttpRequest request = httpRequestFactory
                    .buildGetRequest(new GenericUrl(PLACES_SEARCH_BY_AREA));

            request.getUrl().put("lat", latitude);
            request.getUrl().put("lon", longitude);
            request.getUrl().put("lat", latitude);

            if(type.equals("Metro")){
                request.getUrl().put("stopTypes", "NaptanRailStation+NaptanMetroStation");

                request.getUrl().put("modes", "tube+overground+dlr");

            }
            Log.d("nawak", "url : "+request.getUrl().toString());
            Log.d("nawak", "json : "+request.execute().parseAsString());
            StationsList list = request.execute().parseAs(StationsList.class);
            Log.d("nawak", list.stopPoints.get(0).commonName);
            return list;

        } catch (JsonSyntaxException e) {

            Log.e("JsonError:", e.getMessage());
            return null;
        }

    }


    public List<Arrivals> searchArrivals()
            throws Exception {

        try {

            //StationsList list = search(s);

            HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);

            HttpRequest request = httpRequestFactory
                    .buildGetRequest(new GenericUrl(ARRIVALS_SEARCH_URL));

            request.getUrl().put("ids", "940GZZLUTNG");
            Gson gson = new Gson();

            Type ListType = new TypeToken<List<Arrivals>>(){}.getType();
            List<Arrivals> enums = gson.fromJson(request.execute().parseAsString(), ListType);
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
