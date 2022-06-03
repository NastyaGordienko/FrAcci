package com.example.fracci.server;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fracci.MainActivity;
import com.example.fracci.MainService;
import com.example.fracci.R;
import com.example.fracci.crash.Crash;
import com.example.fracci.crash.GeneralData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class SettingsMenu extends Fragment  {
    private Context context;
    public static CrashAdapter adapter;
    float[] results_dist = new float[3];
    public static double dop_la1, dop_lo1, latitude2, longitude2, angle, latitude_dtp, longitude_dtp;

    public Geocoder gcd;
    Location location, locationB, locationA;
    Handler handler;
    public ArrayList<Double> latLng111 = new ArrayList();
    public ArrayList<Double> lonLng111 = new ArrayList();

    public static Double[] latLngdtp = {55.939499, 55.938083, 55.785322, 55.7810105, 55.7805058, 55.7811904, 55.7816212, 55.7803593};
    public static Double[] lonLngdtp = {39.068579, 39.063649, 37.454161, 37.4516157, 37.4518025, 37.4526085, 37.4503149, 37.4508778};
    View view;
    boolean permissions = false;
    private Double latLng1 = 0.0, lonLng1 = 0.0;

    private Timer mTimer;
    //private MyTimerTask mMyTimerTask, mMyTimerTask2;
    private MyTimerTask mMyTimerTaskRes;
    private MyTimerTaskGeo mMyTimerTaskGeo;
    private Location location2;

    public SettingsMenu() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //задаем разметку фрагменту
        final View view = inflater.inflate(R.layout.fragment_settings_menu, container, false);
        //ну и контекст, так как фрагменты не содержат собственного
        context = view.getContext();
        SettingsMenu fb = new SettingsMenu();
       // Output.latitude = view.findViewById(R.id.latitude);
      //  Output.latitude.setText("12345");
        Button button2 = (Button) view.findViewById(R.id.button2);
        // sufgan
        //setOutput();
      //  update();
        for (double[] latlon : new double[][]{{-260.080518, 30.344870}, {60.065965, 30.333228}, {60.080787, 0.341495}}) {
            Server.reportedCrash(new Crash(latlon[0], latlon[1]));
        }
        latLng111.add(0.0);
        lonLng111.add(0.0);
        // NastyaGordienko
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            permissions = true;
        }
        // ПОДГОТОВКА К ОПРЕДЕЛЕНИЮ МЕСТОПОЛОЖЕНИЯ
        LocationManager locationManager = (LocationManager) context.getSystemService(context.getApplicationContext().LOCATION_SERVICE);
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 900, 10, new MyLocationListener());

        mTimer = new Timer();
        mMyTimerTaskRes = new MyTimerTask();
        mMyTimerTaskGeo = new MyTimerTaskGeo();
        mTimer.schedule(mMyTimerTaskRes, 0, 500);
        mTimer.schedule(mMyTimerTaskGeo, 0, 1000);

        gcd = new Geocoder(context, Locale.getDefault());


        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    show_coord(view);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

return view;
    }

   /* public void toFrag2(View view) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Frag2 fb1 = new Frag2();
        ft.add(R.id.frame, fb1);
        ft.commit();
    }
    */

        //Наш бэкграунд так сказать----------------------------------------------------------------------------------------------------------
        void setOutput() {
            Output.speed = view.findViewById(R.id.speed);
            Output.angle = view.findViewById(R.id.angle);
            Output.distTo = view.findViewById(R.id.distanceTo);
            Output.latitude = view.findViewById(R.id.latitude);
            Output.longitude = view.findViewById(R.id.longitude);


        }

        void update() {
            Output.BRAKING.setText(Boolean.toString(GeneralData.BRAKING));
            Output.STOP.setText(Boolean.toString(GeneralData.STOP));
            Output.COLLISION.setText(Boolean.toString(GeneralData.COLLISION));
            Output.CHAOS.setText(Boolean.toString(GeneralData.MOVEMENT_IN_CHAOS));
        }

        public static void updateCrashes() {
            adapter.clear();
//        adapter.addAll(ServerConnection.loadNewCrashes(MainService.car));
            adapter.notifyDataSetChanged();
        }

        public double getSpeed(View view) {
            Output.speed.setText("" + location.getSpeed());
            Toast.makeText(context, "" + location.getSpeed(), Toast.LENGTH_LONG).show();
            return location.getSpeed();
        }

        public void getAngle(View view) {
            angle();
        }


        public double angle() {
            double res1, res2, res3, res4;

            res1 = latitude2 - latLng1;
            res2 = longitude2 - lonLng1;
            res3 = latLngdtp[0] - latLng1;//координаты дтп!
            res4 = lonLngdtp[0] - lonLng1;//координаты дтп!
            angle = (res1 * res2 + res3 * res4) / (Math.sqrt((res1 * res1 + res2 * res2)) * Math.sqrt((res3 * res3 + res4 * res4)));
            double angleDEGR = (180.0d * Math.acos(angle)) / Math.PI;
            Output.angle.setText("" + angleDEGR);
            // Output.angle.setText("" +Math.acos(angle));
            return angleDEGR;
        }

        public float getDistance(View view) {
            Location loc1 = new Location("");
            loc1.setLatitude(latitude2);// current latitude
            loc1.setLongitude(longitude2);//current  Longitude

            Location loc2 = new Location("");
            loc2.setLatitude(latLngdtp[0]);
            loc2.setLongitude(lonLngdtp[0]);
            float distanceInMeters = loc1.distanceTo(loc2);
            Output.distTo.setText("" + distanceInMeters);
            return loc1.distanceTo(loc2);

            /**
             location.distanceBetween(latitude2,longitude2,latLngdtp[0],lonLngdtp[0],results_dist);
             Output.distTo.setText("" + results_dist[0]+" "+results_dist[1]+" "+results_dist[2]);
             **/

        }


        public void show_coord(View view) throws IOException {
            /** mTimer = new Timer();
             mMyTimerTask = new MyTimerTask();
             mMyTimerTask2 = new MyTimerTask();

             if (permissions) {
             mTimer.schedule(mMyTimerTask, 1000, 5000);
             mTimer.schedule(mMyTimerTask2, 2500, 6000);
             }**/
            List<Address> addresses = gcd.getFromLocation(latitude2, longitude2, 1);
            String str = "";
            if (addresses.size() > 0) {
                str = addresses.get(0).getAddressLine(0);
            }
            Toast.makeText(context.getApplicationContext(), "" + latitude2 + "," + longitude2 + "/" + str, Toast.LENGTH_SHORT).show();
        }

        public void distanceBetween(View view) {
        }

/*
        @Override
        public void onPointerCaptureChanged(boolean hasCapture) {
            super.onPointerCaptureChanged(hasCapture);
        }
*/
        class MyLocationListener implements LocationListener {

            @Override
            public void onLocationChanged(@NonNull Location location) {
                SettingsMenu.this.location = location;

                latitude2 = location.getLatitude();
                longitude2 = location.getLongitude();
                latLng111.add(latitude2);
                lonLng111.add(longitude2);

            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        }

        class MyListener implements View.OnClickListener {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.start:
                        context.startService(new Intent(context.getApplicationContext(), MainService.class));
                        break;



                    case R.id.speed_log:
                        double s = location.getSpeed();
                        Output.speed.setText("" + s);
                        Toast.makeText(context, "" + s, Toast.LENGTH_SHORT).show();
                        break;
                }
               // update();
            }
        }

        private static class CrashAdapter extends ArrayAdapter {
            LinkedList<Crash> crashes;

            public CrashAdapter(@NonNull Context context, int resource, LinkedList<Crash> crases) {
                super(context, resource);
                this.crashes = crases;
            }

            @Override
            public int getCount() {
                return crashes.size();
            }

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_crash, null);
                }
                TextView name = convertView.findViewById(R.id.name);
                name.setText("Авария " + position);
                TextView latitude = convertView.findViewById(R.id.latitude);
                latitude.setText("latitude: " + crashes.get(position).latitude);
                TextView longitude = convertView.findViewById(R.id.longitude);
                longitude.setText("longitude: " + crashes.get(position).longitude);
                TextView distance = convertView.findViewById(R.id.distance);
                distance.setText("" + GeneralData.car.distanceTo(crashes.get(position)));
                return convertView;
            }
        }

        public static class Output {
            public static TextView speed, angle, distTo;
            public static TextView latitude, longitude;
            public static TextView BRAKING, STOP, COLLISION, CHAOS;
        }


        private class MyTimerTask extends TimerTask {

            @Override
            public void run() {
                if (latLng111.size() >= 2) {
                    latLng1 = latLng111.get(latLng111.size() - 2);
                    lonLng1 = lonLng111.get(lonLng111.size() - 2);

                    latLng111.remove(latLng111.size() - 2);
                    lonLng111.remove(lonLng111.size() - 2);
                    //logcat masive size
                }


            }
        }


        private class MyTimerTaskGeo extends TimerTask {
                    @Override
                    public void run() {
                      //  Output.latitude.setText("" + latitude2);
                      //  Output.longitude.setText("" + longitude2);
                        //Output.angle.setText(""+angle());
                        //Output.speed.setText(""+getSpeed(view));

                    }

        }



}