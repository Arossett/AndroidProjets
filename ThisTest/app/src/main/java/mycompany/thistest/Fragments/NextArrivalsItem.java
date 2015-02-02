package mycompany.thistest.Fragments;

import java.util.ArrayList;
import java.util.HashMap;

import mycompany.thistest.TFL.Arrival;

/**
 * Created by trsq9010 on 05/01/2015.
 */
public class NextArrivalsItem {

    private String itemTitle;
    ArrayList<String> arrivals;
    private int color;
    boolean updated;

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
        updated = true;
    }

    public NextArrivalsItem(String title, ArrayList<String> arr, int col) {
        this.itemTitle = title;
        arrivals = arr;
        color = col;
        updated = true;
        //arrivalsByPlatform = setListArrivals(arrivals);
    }

    public ArrayList<String> getArrivals() {
        return arrivals;
    }

    public int getColor() {
        return color;
    }

    public void setArrivals(ArrayList<String> arr) {
        arrivals = arr;
        updated = true;
    }

    public void hasUpdated() {
        updated = false;
    }

    public boolean getUpdate() {
        return updated;
    }
}