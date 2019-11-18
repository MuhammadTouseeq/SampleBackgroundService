package com.pakdev.samplebackgroundservice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

startService();

    }

private void startService()
{
    Intent intentService=new Intent(MainActivity.this,BackgroundService.class);
    intentService.setAction("STARTFOREGROUND_ACTION");
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


        startForegroundService(intentService);
    } else {
        startService(intentService);
    }
}

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context c) {
        ActivityManager manager = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
