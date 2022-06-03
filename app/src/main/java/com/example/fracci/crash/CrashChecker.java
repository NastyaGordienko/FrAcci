package com.example.fracci.crash;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

import com.example.fracci.MainActivity;
import com.example.fracci.MainService;
import com.example.fracci.server.ServerConnection;

public class CrashChecker implements Loopable{
    private boolean RUN = true;

    public CrashChecker() {
        GeneralData.addSensor(Sensor.TYPE_ACCELEROMETER, SensorManager.SENSOR_DELAY_NORMAL);
        GeneralData.addSensor(Sensor.TYPE_GRAVITY, SensorManager.SENSOR_DELAY_NORMAL);
        while(RUN) {
            try {
                Log.i("TEST", "check me to crash");
                cycle();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void cycle() throws InterruptedException {
        while (GeneralData.BRAKING) {
            if (getCrashLvl()) {
                Thread.sleep(1000 * 7);
                if (GeneralData.STOP) {
                    GeneralData.BRAKING = false;
                    ServerConnection.reportCrash(new Crash(GeneralData.car.latitude,GeneralData.car.longitude));
                }
            }
//            Thread.sleep(10);
        }
        Thread.sleep(500);
    }

    @Override
    public void stop() {
        RUN = false;
    }

    static boolean getCrashLvl() {
        return GeneralData.COLLISION || GeneralData.MOVEMENT_IN_CHAOS;
    }

}