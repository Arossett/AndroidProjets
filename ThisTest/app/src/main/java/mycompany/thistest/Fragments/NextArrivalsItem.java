package mycompany.thistest.Fragments;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by trsq9010 on 05/01/2015.
 */
public class NextArrivalsItem {

    private String itemTitle;
    private String[] arrivalslist;


    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public NextArrivalsItem(String title, String[] list){
        this.itemTitle = title;
        arrivalslist = list;
    }

    public String[] getList() {
        return arrivalslist;
    }

    public void setArrivalslist(String[] itemTitle) {
        this.arrivalslist = itemTitle;
    }
}
