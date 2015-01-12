package mycompany.thistest.NationalRail;

import android.app.Activity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by trsq9010 on 09/01/2015.
 */
public class ReadCVS {

    public ReadCVS(Activity a){
        activity = a;
    }

    Activity activity;
    public String searchCodeStation(String station){
        BufferedReader br = null;
        String code = "";

        try {
            InputStream is = activity.getAssets().open("station_codes.csv");

            String line = "\n";
            String cvsSplitBy = ",";


            br = new BufferedReader(new BufferedReader(new InputStreamReader(is)));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] l = line.split(cvsSplitBy);
                if(l[0].equals(station)) {
                    Log.v("codestation", "code : " + l[1]);
                    code = l[1];
                    break;
                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Done");
        return code;
    }

}
