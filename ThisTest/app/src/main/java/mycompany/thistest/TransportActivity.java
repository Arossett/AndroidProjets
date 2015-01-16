package mycompany.thistest;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mycompany.thistest.Fragments.NextArrivalsItem;
import mycompany.thistest.Fragments.ListFragment;
import mycompany.thistest.Fragments.NextArrivalsListAdapter;
import mycompany.thistest.Interfaces.Spot;
import mycompany.thistest.NationalRail.LoadTrainDepartures;
import mycompany.thistest.TFL.BikePoint;
import mycompany.thistest.TFL.LoadArrivals;
import mycompany.thistest.TFL.Arrival;
import mycompany.thistest.TFL.Station;


public class TransportActivity extends Activity implements ListFragment.OnFragmentInteractionListener {

    private ListFragment nextArrivalsListFragment;
    private ArrayList<NextArrivalsItem> nextArrivalsItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport);


            Intent i = getIntent();
            Spot station = (Spot) i.getSerializableExtra("station");
            String type = station.getType();
            String stationName = station.getName();

            if (type.equals("Train")) {
                String name = stationName.split(" Rail Station")[0];
                new LoadTrainDepartures(this, name).execute();
            } else if (type.equals("Metro") || type.equals("Bus")) {
                //load transport next arrivals to the current station
                new LoadArrivals(this, ((Station) station).getRailId()).execute();
            } else if (type.equals("Bike")) {
                String s1 = ((BikePoint) station).getBikes();
                String s2 = ((BikePoint) station).getEmpty();
                String[] bikes = new String[]{s1, s2};
                ArrayAdapter<String> myAdapter = new
                        ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_list_item_1,
                        bikes);
                ListView myList = (ListView)
                        findViewById(R.id.lv);
                myList.setAdapter(myAdapter);
            }

    }

    public void addNewFragment(List<Arrival> arrivals){

        //can be improved by looking for arrivals by line for a stopPoint
        //sort arrivals by line

        HashMap<String, ArrayList<Arrival>> arrivalsByLine = new HashMap<String, ArrayList<Arrival>>();
        for(Arrival a: arrivals){
            Log.v("arrivalsdetails", "name "+  a.getLineName() + " destination " + a.getDestinationName() + " plateforme "+a.getPlatform());
            if(arrivalsByLine.containsKey(a.getLineName())){
                arrivalsByLine.get(a.getLineName()).add(a);
            }else{
                ArrayList<Arrival> list = new ArrayList<Arrival>();
                list.add(a);
                arrivalsByLine.put(a.getLineName(), list);
            }
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        nextArrivalsListFragment = new ListFragment();
        nextArrivalsItemList = new ArrayList<NextArrivalsItem>();

        nextArrivalsListFragment.setmAdapter(new NextArrivalsListAdapter(this, nextArrivalsItemList));
        transaction.add(R.id.container, nextArrivalsListFragment);
        transaction.commit();

        //for each line, display a new fragment with arrivals sorted by platform
        for(HashMap.Entry<String, ArrayList<Arrival>> entry : arrivalsByLine.entrySet() ){
            nextArrivalsItemList.add(new NextArrivalsItem(entry.getKey(), entry.getValue()));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_transport, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(String id) {

    }

    public void setTrainDepartures(List<String> trainDepartures){

        ArrayAdapter<String> myAdapter=new
                ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                trainDepartures);
        ListView myList=(ListView)
                findViewById(R.id.lv);
        myList.setAdapter(myAdapter);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //getFragmentManager().putFragment(outState, "listFragment", nextArrivalsListFragment);
    }
}
