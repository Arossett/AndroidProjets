package mycompany.thistest;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mycompany.thistest.Fragments.NextArrivalsItem;
import mycompany.thistest.Fragments.ListFragment;
import mycompany.thistest.Fragments.NextArrivalsListAdapter;
import mycompany.thistest.Interfaces.Spot;
import mycompany.thistest.LoadClasses.LoadArr;
import mycompany.thistest.LoadClasses.LoadTrDepart;
import mycompany.thistest.NationalRail.ReadCVS;
import mycompany.thistest.TFL.BikePoint;
import mycompany.thistest.TFL.Arrival;
import mycompany.thistest.TFL.Station;


public class TransportActivity extends Activity implements ListFragment.OnFragmentInteractionListener {

    private ListFragment nextArrivalsListFragment;
    private ArrayList<NextArrivalsItem> nextArrivalsItemList;
    Spot station;
    ArrayList<String> trainDepartures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport);

        if(savedInstanceState!=null){
            /*station = (Spot)savedInstanceState.getSerializable("station");
            if(station.getType().equals("Train")) {
                trainDepartures = savedInstanceState.getStringArrayList("trains");
                setTrainDepartures(trainDepartures);
            }*/

        }
        else {

            Intent i = getIntent();
            station = (Spot) i.getSerializableExtra("station");
            String type = station.getType();
            String stationName = station.getName();

            if (type.equals("Train")) {
                String name = stationName.split(" Rail Station")[0];

                ReadCVS readCVS = new ReadCVS(this);
                String stationCode = readCVS.searchCodeStation(name);
                LoadTrDepart loadTrains = new LoadTrDepart(stationCode);
                loadTrains.setMyTaskCompleteListener(new LoadArr.OnTaskComplete() {
                    @Override
                    public void setMyTaskComplete(Object obj) {
                        if (obj != null) {
                            trainDepartures = ((ArrayList<String>)obj);
                            HashMap<String, ArrayList<String>> trains = new HashMap<String, ArrayList<String>>();
                            trains.put("", trainDepartures);
                            addFragment(trains);
                        }
                    }
                });
                loadTrains.execute();
            } else if (type.equals("Metro") || type.equals("Bus")) {
                //load transport next arrivals to the current station
                LoadArr loadArr = new LoadArr(((Station) station).getRailId());
                loadArr.setMyTaskCompleteListener(new LoadArr.OnTaskComplete() {
                    @Override
                    public void setMyTaskComplete(Object obj) {
                        if (obj != null) {
                            shapeArrivals((List<Arrival>) obj);
                        }
                    }
                });
                loadArr.execute();

            } /*else if (type.equals("Bike")) {
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
            }*/
        }
    }

    public void shapeArrivals(List<Arrival> arrivals){

        //can be improved by looking for arrivals by line for a stopPoint
        //sort arrivals by line

        HashMap<String, ArrayList<String>> arrivalsByLine = new HashMap<String, ArrayList<String>>();
        for(Arrival a: arrivals){
            Log.v("arrivalsdetails", "name "+  a.getLineName() + " destination " + a.getDestinationName() + " plateforme "+a.getPlatform());
            if(arrivalsByLine.containsKey(a.getLineName())){
                arrivalsByLine.get(a.getLineName()).add(a.toString());
            }else{
                ArrayList<String> list = new ArrayList<String>();
                list.add(a.toString());
                arrivalsByLine.put(a.getLineName(), list);
            }
        }
        addFragment(arrivalsByLine);
    }

    public void addFragment(HashMap<String, ArrayList<String>> arrivals){

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        nextArrivalsItemList = new ArrayList<NextArrivalsItem>();

        for(HashMap.Entry<String, ArrayList<String>> entry : arrivals.entrySet() ) {
            nextArrivalsItemList.add(new NextArrivalsItem(entry.getKey(), entry.getValue()));
        }

        ListFragment nextArrivalsListFragment = new ListFragment();
        nextArrivalsListFragment.setmAdapter(new NextArrivalsListAdapter(this, nextArrivalsItemList));
        transaction.add(R.id.container, nextArrivalsListFragment, "line_fragment");
        transaction.commit();
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putSerializable("station", station);
        if(getFragmentManager().findFragmentByTag("line_fragment")!=null) {
            getFragmentManager().findFragmentByTag("line_fragment").setRetainInstance(true);
        }
        /*if(station.getType().equals("Train")){

           outState.putStringArrayList("trains", trainDepartures);

        }*/


    }
}
