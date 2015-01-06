package mycompany.thistest;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mycompany.thistest.Fragments.NextArrivalsItem;
import mycompany.thistest.Fragments.ListFragment;
import mycompany.thistest.Fragments.NextArrivalsListAdapter;
import mycompany.thistest.TFLPlaces.LoadArrivals;
import mycompany.thistest.TFLPlaces.Arrival;
import mycompany.thistest.Fragments.LineDetails;


public class TransportActivity extends Activity implements ListFragment.OnFragmentInteractionListener {
    String stationId;
    List<Arrival> arrivals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent i = getIntent();
        stationId = (String) i.getSerializableExtra("stationId");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport);



        //load transport next arrivals to the current station
        new LoadArrivals(this, stationId).execute();

        //if there is different ids linked to this station (for overground case for example)
        //load arrivals for these other station ids
        if((ArrayList<String>)i.getSerializableExtra("railIds")!=null) {
            ArrayList<String> railIds = (ArrayList<String>) i.getSerializableExtra("railIds");
            for(String s :railIds){
                new LoadArrivals(this, s).execute();
            }
        }

    }

    public void setArrivals(List<Arrival> arr){
        arrivals = arr;
    }

    public void addNewLineFragment(){

        /*ListFragment lf = new ListFragment();
        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values);
        lf.setListAdapter(adapter);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.add(R.id.container, lf);
*/

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


        //TODO: fragment problem
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        ListFragment nextArrivalsListFragment = new ListFragment();
        ArrayList<NextArrivalsItem> nextArrivalsItemList = new ArrayList<NextArrivalsItem>();
        //for each line, display a new fragment with arrivals sorted by platform
        for(HashMap.Entry<String, ArrayList<Arrival>> entry : arrivalsByLine.entrySet() ){
            /*
            LineDetails newFragment = LineDetails.newInstance(entry.getKey(), entry.getValue());
            transaction.add(R.id.container, newFragment);
            Log.v("newfragment",entry.getKey());*/
            nextArrivalsItemList.add(new NextArrivalsItem(entry.getKey(), entry.getValue()));
        }
        nextArrivalsListFragment.setmAdapter(new NextArrivalsListAdapter(this, nextArrivalsItemList));
        transaction.add(R.id.container, nextArrivalsListFragment);
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
}
