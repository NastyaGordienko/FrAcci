package com.example.fracci;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AutoStart extends BroadcastReceiver {

    public AutoStart() {
    }

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Toast toast = Toast.makeText(context.getApplicationContext(),
                    context.getResources().getString(R.string.hi), Toast.LENGTH_LONG);
            toast.show();
            Log.d("myapp", context.getResources().getString(R.string.hi));
            context.startService(new Intent(context.getApplicationContext(), MainService.class));
        }
    }
}