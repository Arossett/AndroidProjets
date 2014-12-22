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

import mycompany.thistest.TFLPlaces.LoadArrivals;
import mycompany.thistest.TFLPlaces.Arrival;
import mycompany.thistest.UI.LineDetails;


public class TransportActivity extends Activity {
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
        //can be improve by looking for arrivals by line for a stopPoint
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
        for(HashMap.Entry<String, ArrayList<Arrival>> entry : arrivalsByLine.entrySet() ){
            LineDetails newFragment = LineDetails.newInstance(entry.getKey(), entry.getValue());
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.container, newFragment);
            transaction.commit();
            Log.v("newfragment",entry.getKey());
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
}
