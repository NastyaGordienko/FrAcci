package com.example.fracci.server;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.fracci.MainActivity;
import com.example.fracci.MainService;
import com.example.fracci.crash.Car;
import com.example.fracci.crash.Crash;
import com.example.fracci.crash.GeneralData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerConnection {
    Retrofit retrofit;
    CoordsInterface serv;

    public static Map<Double, Set<Double>> crashesNearMap = new HashMap<Double, Set<Double>>();

    static public void reportCrash(Crash crash) {
        Server.reportedCrash(crash);
    }

    public static void loadNewCrashes(Car car) {
        Log.i("ServerConnection", "loading new crashes...");
        crashesNearMap = Server.getCrashes(GeneralData.car, 0.02);
        Log.i("ServerConnection", "loading is complete!");
    }
public ServerConnection(){

}
    //Серверочек,ретрофит-------------------------------------------------------------------------
//    retrofit=new Retrofit.Builder().baseUrl(" https://fracci.herokuapp.com").addConverterFactory(GsonConverterFactory.create()).build();
//    serv=retrofit.create(CoordsInterface.class);
//    public void send(View view) {
//        Call<Void> call=serv.sendCoords(String.valueOf(GeneralData.car.latitude),String.valueOf(GeneralData.car.longitude));
//        class MyThread extends Thread{
//            @Override
//            public void run() {
//                try {
//                    call.execute();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }
//        MyThread t1=new MyThread();
//        t1.start();
//
//    }
//
//    public void get(View view) {
//        Call<ArrayList<Coords>> call_get=serv.getCoords();
//        call_get.enqueue(new Callback<ArrayList<Coords>>() {
//            @Override
//            public void onResponse(Call<ArrayList<Coords>> call, Response<ArrayList<Coords>> response) {
//                ArrayList<Coords> list=response.body();
//
//                //chat.setText(list.get(list.size()-1).lat+" "+list.get(list.size()-1).lng+" ");
//            }
//            //http://192.168.0.160:8080/java/test
//            @Override
//            public void onFailure(Call<ArrayList<Coords>> call, Throwable t) {
//                Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
//
//            }
//        });
//    }

}
