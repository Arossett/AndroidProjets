package mycompany.thistest.APIRequests;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;

import java.io.IOException;
import java.net.Proxy;
import java.net.SocketTimeoutException;

/**
 * Created by trsq9010 on 07/01/2015.
 */
public class RailHandler {
    private static final String MAIN_REQUEST_URL = "https://lite.realtime.nationalrail.co.uk/OpenLDBWS/ldb6.asmx";
    public static final String TOKEN = "622d00bd-748d-4d88-9747-55ec03d5e7d5";
    public static final String LDB = "http://thalesgroup.com/RTTI/2014-02-20/ldb/";
    private static final String SOAP_ACTION = "http://thalesgroup.com/RTTI/2008-02-20/ldb/GetDepartureBoard";
    public static final String TYP =  "http://thalesgroup.com/RTTI/2013-11-28/Token/types";


    private final SoapSerializationEnvelope getSoapSerializationEnvelope(SoapObject request) {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.dotNet = true;
        envelope.implicitTypes = true;
        envelope.setAddAdornments(false);
        envelope.setOutputSoapObject(request);
        return envelope;
    }

    private final HttpTransportSE getHttpTransportSE() {
        HttpTransportSE ht = new HttpTransportSE(Proxy.NO_PROXY,MAIN_REQUEST_URL,60000);
        ht.debug = true;
        ht.setXmlVersionTag("<!--?xml version=\"1.0\" encoding= \"UTF-8\" ?-->");
        return ht;
    }

    public String getResponseString(String stationCode) {
        String data = null;
        String methodname = "GetDepartureBoardRequest";

        SoapObject request = new SoapObject(LDB, methodname);
        request.addProperty("numRows", 10);
        request.addProperty("crs", stationCode);
        request.addProperty("timeWindow", 60);

        SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);

        //to add headers with token
        Element h = new Element().createElement(TYP, "AccessToken");
        Element username = new Element().createElement(TYP, "TokenValue");
        username.addChild(Node.TEXT, TOKEN);
        h.addChild(Node.ELEMENT, username);
        envelope.headerOut = new Element[1];
        envelope.headerOut[0] = h;
        HttpTransportSE ht = getHttpTransportSE();

        try {
            ht.call(SOAP_ACTION, envelope);
            data = ht.responseDump.toString();

        } catch (SocketTimeoutException t) {
            Log.e("error", "error 0");
            t.printStackTrace();
        } catch (IOException i) {
            Log.e("error", "error 1 "+i.toString());
            i.printStackTrace();
        } catch (Exception q) {
            Log.e("error", "error 2 "+q.toString());
            q.printStackTrace();
        }
        return data;
    }


    public String getDetails(String serviceID) {
        String data = null;
        String methodname = "GetServiceDetailsRequest";

        SoapObject request = new SoapObject(LDB, methodname);
        request.addProperty("serviceID", serviceID);

        SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);

        Element h = new Element().createElement(TYP, "AccessToken");
        Element username = new Element().createElement(TYP, "TokenValue");
        username.addChild(Node.TEXT, TOKEN);
        h.addChild(Node.ELEMENT, username);
        envelope.headerOut = new Element[1];
        envelope.headerOut[0] = h;

        HttpTransportSE ht = getHttpTransportSE();
        try {
            ht.call(SOAP_ACTION, envelope);

            data = ht.responseDump.toString();

        } catch (SocketTimeoutException t) {
            Log.e("error",t.toString());
            t.printStackTrace();
        } catch (IOException i) {
            Log.e("error", i.toString());
            i.printStackTrace();
        } catch (Exception q) {
            Log.e("error", q.toString());
            q.printStackTrace();
        }
        return data;
    }
}
