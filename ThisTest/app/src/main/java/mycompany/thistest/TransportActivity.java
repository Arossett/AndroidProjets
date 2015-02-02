package mycompany.thistest;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import mycompany.thistest.Fragments.NextArrivalsItem;
import mycompany.thistest.Fragments.ListFragment;
import mycompany.thistest.Fragments.NextArrivalsListAdapter;
import mycompany.thistest.Interfaces.Spot;
import mycompany.thistest.LoadClasses.LoadArr;
import mycompany.thistest.LoadClasses.LoadData;
import mycompany.thistest.LoadClasses.LoadTrDepart;
import mycompany.thistest.NationalRail.ReadCVS;
import mycompany.thistest.Spots.BikePoint;
import mycompany.thistest.TFL.Arrival;
import mycompany.thistest.Spots.Station;


public class TransportActivity extends Activity implements ListFragment.OnFragmentInteractionListener {

    ListFragment nextArrivalsListFragment;
    Spot station;
    ArrayList<String> trainDepartures;
    LoadData loadNextTransports;
    String stationCode;
    Timer myTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport);

        if(savedInstanceState==null){

            Intent i = getIntent();
            station = (Spot) i.getSerializableExtra("station");
            String type = station.getType();
            String stationName = station.getName();

            if (type.equals("Train")) {
                String name = stationName.split(" Rail Station")[0];

                ReadCVS readCVS = new ReadCVS(this);
                stationCode = readCVS.searchCodeStation(name);
                loadNextTransports = new LoadTrDepart(stationCode);
                loadNextTransports.setMyTaskCompleteListener(new LoadArr.OnTaskComplete() {
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
                loadNextTransports.execute();
            } else if (type.equals("Metro") || type.equals("Bus")) {
                //load transport next arrivals to the current station
                loadNextTransports = new LoadArr(((Station) station).getRailId());
                loadNextTransports.setMyTaskCompleteListener(new LoadArr.OnTaskComplete() {
                    @Override
                    public void setMyTaskComplete(Object obj) {
                        if (obj != null) {
                            shapeArrivals((List<Arrival>) obj);
                        }
                    }
                });
                loadNextTransports.execute();

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
        nextArrivalsListFragment = null;
        myTimer = new Timer();
        MyTimerTask myTimerTask= new MyTimerTask();
        myTimer.scheduleAtFixedRate(myTimerTask, 6000, 6000); //(timertask,delay,period)
    }


private class MyTimerTask extends TimerTask {
    @Override
    public void run() {
        Log.v("alarmTime", "ring");
        if (station.getType().equals("Train")) {
            loadNextTransports = new LoadTrDepart(stationCode);
            loadNextTransports.setMyTaskCompleteListener(new LoadArr.OnTaskComplete() {
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
            loadNextTransports.execute();
        } else if (station.getType().equals("Metro") || station.getType().equals("Bus")) {
            //load transport next arrivals to the current station
            loadNextTransports = new LoadArr(((Station) station).getRailId());
            loadNextTransports.setMyTaskCompleteListener(new LoadArr.OnTaskComplete() {
                @Override
                public void setMyTaskComplete(Object obj) {
                    if (obj != null) {
                        shapeArrivals((List<Arrival>) obj);
                    }
                }
            });
        }
            loadNextTransports.execute();
    }
}

    //to sort arrivals by metro line
    public void shapeArrivals(List<Arrival> arrivals){
        HashMap<String, ArrayList<String>> arrivalsByLine = new HashMap<String, ArrayList<String>>();
        for(Arrival a: arrivals){
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

    //to a listFragment with all lines arrivals displayed
    public void addFragment(HashMap<String, ArrayList<String>> arrivals){
        final ArrayList<NextArrivalsItem> nextArrivalsItemList = new ArrayList<NextArrivalsItem>();
        for(HashMap.Entry<String, ArrayList<String>> entry : arrivals.entrySet() ) {
            for(String s:entry.getValue()) {
                Log.v("arrivalsdetails1", entry.getKey()+" "+s);
            }
            int resID = getResources().getIdentifier(entry.getKey().split(" ")[0], "string", getPackageName());
            int c;
            if(resID != 0){
                c = Color.parseColor(getResources().getString(resID));
            }else{
                c = Color.parseColor("#FFFFFF");
            }
            nextArrivalsItemList.add(new NextArrivalsItem(entry.getKey(), entry.getValue(), c));
        }

        if(nextArrivalsListFragment == null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            nextArrivalsListFragment = new ListFragment();
            nextArrivalsListFragment.setmAdapter(new NextArrivalsListAdapter(this, nextArrivalsItemList));
            transaction.add(R.id.container, nextArrivalsListFragment, "line_fragment");
            transaction.commit();
        }else{
            runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nextArrivalsListFragment.update(nextArrivalsItemList);
                }
            });
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(getFragmentManager().findFragmentByTag("line_fragment")!=null) {
            getFragmentManager().findFragmentByTag("line_fragment").setRetainInstance(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        myTimer.cancel();
        super.onDestroy();
    }
}
