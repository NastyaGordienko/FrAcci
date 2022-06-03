package com.example.fracci;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.fracci.crash.CrashChecker;
import com.example.fracci.crash.CrashRadar;
import com.example.fracci.crash.GeneralData;
import com.example.fracci.server.ServerConnection;

public class MainService extends Service {

    @Override
    public void onCreate() {
        new GeneralData(getApplicationContext());
        new Thread(() -> new CrashChecker()).start();
        new Thread(() -> new CrashRadar()).start();
        // загрузка карты аварий
//        new Thread(() -> {
//            while(true) {
//                ServerConnection.loadNewCrashes(GeneralData.car);
//                try {
//                    Thread.sleep(1000 * 60 * 5);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        startService(new Intent(getApplicationContext(), MainService.class));
    }

}