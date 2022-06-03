package com.example.fracci.crash;

import android.util.Log;

import com.example.fracci.server.ServerConnection;

public class CrashRadar implements Loopable {
    private boolean RUN = true;
    Crash crash;

    public CrashRadar() {
        crash = new Crash(0, 0);
        while (RUN) {
            try {
                cycle();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void cycle() throws InterruptedException {
        Log.i("TEST", "check crash");
//        checkCrash();
        switch (GeneralData.getMODE()) {
            case GeneralData.MODE_NO_MOVE:
                Thread.sleep(60000);
            case GeneralData.MODE_LIGHT_MOVE:
                Thread.sleep(60000);
            case GeneralData.MODE_NORMAL_MOVE:
                Thread.sleep(60000);
            case GeneralData.MODE_FAST_MOVE:
                Thread.sleep(60000);
        }
    }

    void checkCrash() {
        for (Double latitude : ServerConnection.crashesNearMap.keySet()) {
            for (Double longotude : ServerConnection.crashesNearMap.get(latitude)) {
                if (isInRadius(crash.setLat(latitude).setLong(longotude))) {
                    if (isInVector(crash)) {
                        pushNoti();
                    }
                }
            }
        }
    }

    private double zeroLat, zeroLong;

    private boolean isInVector(Crash crash) {
        double arcAngle = Math.atan(zeroLong / zeroLat);
        arcAngle = zeroLat >= 0 ? arcAngle : arcAngle+180;
        return Math.abs(GeneralData.car.vector - arcAngle) < 15;
    }

    private boolean isInRadius(Crash crash) {
        zeroLat = crash.latitude - GeneralData.car.latitude;
        zeroLong = crash.longitude - GeneralData.car.longitude;
        return Math.abs(zeroLat) < 0.02 && Math.abs(zeroLong) < 0.02;
    }

    protected void pushNoti() {
        Log.i("CrashRadar", "we find the crash");
        // посылка уведомления об аварии
    }

    @Override
    public void stop() {
        RUN = false;
    }
}