package mycompany.thistest.Fragments;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mycompany.thistest.R;
import mycompany.thistest.TFLPlaces.Arrival;

/**
 * Created by trsq9010 on 05/01/2015.
 */
public class NextArrivalsItem {

    private String itemTitle;
    HashMap<String, ArrayList<Arrival>> arrivalsByPlatform;
    ArrayList<Arrival> arrivals;

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public NextArrivalsItem(String title,  ArrayList<Arrival> arr){
        this.itemTitle = title;
        arrivals = arr;
        //arrivalsByPlatform = setListArrivals(arrivals);
    }

    public ArrayList<Arrival> getArrivals(){
        return arrivals;
    }

/*
    private HashMap<String, ArrayList<Arrival>> setListArrivals(ArrayList<Arrival> arrivals){
        //create a hashmap to sort arrivals by platform number
        HashMap<String, ArrayList<Arrival>> arrivalsByPlat= new HashMap<String, ArrayList<Arrival>>();
        for(Arrival a : arrivals){
            if(arrivalsByPlat.containsKey(a.getPlatform())){
                arrivalsByPlat.get(a.getPlatform()).add(a);
            }else{
                ArrayList<Arrival> list = new ArrayList<Arrival>();
                list.add(a);
                arrivalsByPlat.put(a.getPlatform(), list);
            }
        }
        return arrivalsByPlat;
    }*/
}
