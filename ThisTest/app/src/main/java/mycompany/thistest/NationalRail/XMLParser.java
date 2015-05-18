package mycompany.thistest.NationalRail;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import mycompany.thistest.APIRequests.RailHandler;

/**
 * Created by trsq9010 on 09/01/2015.
 */
public class XMLParser {

    public List<TrainDeparture> DepartureParser(String xml) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;

        List<TrainDeparture> arrivals = new ArrayList<TrainDeparture>();

        try {
            db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            try {
                Document doc = db.parse(is);
                NodeList nodes = doc.getElementsByTagName("service");

                for (int i = 0; i < nodes.getLength(); i++) {
                    Element element = (Element) nodes.item(i);

                    final TrainDeparture arr = new TrainDeparture();

                    //get node "destination"
                    Element destination = (Element) element.getElementsByTagName("destination").item(0);

                    //get node destinationName and print it
                    Element destinationName = (Element) destination.getElementsByTagName("locationName").item(0);
                    arr.setDestination(destinationName.getTextContent());

                    //get departure time of train from the current station
                    Element time = (Element) element.getElementsByTagName("std").item(0);
                    arr.setDepartureTime(time.getTextContent());

                    //get platform
                    Element platform = (Element) element.getElementsByTagName("platform").item(0);
                    if(platform !=null) {
                       arr.setPlatform(platform.getTextContent());
                    }

                    //get if train will be late or on time
                    Element onTime = (Element) element.getElementsByTagName("etd").item(0);
                    arr.setOnTime(onTime.getTextContent());

                    //if you need to get calling points
                    final Element serviceId = (Element) element.getElementsByTagName("serviceID").item(0);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            RailHandler ex = new RailHandler();
                            String xml = ex.getDetails(serviceId.getTextContent());
                            XMLParser parser = new XMLParser();
                            arr.setCallingPoints(parser.DetailsParser(xml));
                        }
                    }).start();
                    arrivals.add(arr);
                }

            } catch (SAXException e) {
                // handle SAXException
            } catch (IOException e) {
                // handle IOException
            }
        } catch (ParserConfigurationException e1) {
            // handle ParserConfigurationException
        }
        return arrivals;
    }

    public List<String> DetailsParser(String xml) {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;

        List<String> points = new ArrayList<String>();

        try {
            db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));

            try {
                Document doc = db.parse(is);
                NodeList nodes = doc.getElementsByTagName("subsequentCallingPoints");
                Element el = (Element)((Element)nodes.item(0)).getElementsByTagName("callingPointList").item(0);
                NodeList nodeList = el.getElementsByTagName("callingPoint");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Element element = (Element) nodeList.item(i);
                    //get node calling
                    Element callinglist = (Element) element.getElementsByTagName("locationName").item(0);
                    points.add(callinglist.getTextContent());
                }

            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return points;
    }
}
