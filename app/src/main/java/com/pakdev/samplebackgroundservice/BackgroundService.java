package com.pakdev.samplebackgroundservice;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import androidx.annotation.Nullable;


public class BackgroundService extends Service {
    Context mContext = this;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;

    private String LOG_TAG="GPS_LOG";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initLocationRequest();
        ForegroundServiceInitialize();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("GEOFENCE SERVICE", "onStartCommand");
//        super.onStartCommand(intent, flags, startId);


        if (intent.getAction().equals("STARTFOREGROUND_ACTION")) {
            Log.i(LOG_TAG, "Received Start Foreground Intent ");
            // your start service code
        }
        else if (intent.getAction().equals("STOPFOREGROUND_ACTION")) {
            Log.i(LOG_TAG, "Received Stop Foreground Intent");
            //your end servce code
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @SuppressLint("MissingPermission")
    private void initLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(2000);//20000
        mLocationRequest.setFastestInterval(1000);//15000
        mLocationRequest.setSmallestDisplacement(1);


        // mLocationRequest.set
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }


    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());

                Toast.makeText(mContext, "Location: " + location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_SHORT).show();


            }
        }
    };



    public void ForegroundServiceInitialize() {

        String GROUP_KEY_FOREGROUND = "com.android.main.foreground";
        Intent intent = Intent.makeRestartActivityTask(new ComponentName(this, BackgroundService.class));
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        NotificationChannelClass nc = new NotificationChannelClass();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Creating Channel
            nc.createMainNotificationChannel(this);
            //building Notification.
            Notification.Builder notifi = new Notification.Builder(getApplicationContext(), nc.getMainNotificationId());
            notifi.setSmallIcon(R.mipmap.ic_launcher);
            notifi.setContentTitle("Auto Location");
            notifi.setContentText("Location Service");
            notifi.setContentIntent(contentIntent);
            notifi.setGroup(GROUP_KEY_FOREGROUND);
            //getting notification object from notification builder.
            Notification n = notifi.build();
            int mNotificationId = 001;

            NotificationManager mNotificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(mNotificationId, n);

            //  startting foreground
            startForeground(1, n);


        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                //for devices less than API Level 26
                Notification notification = new Notification.Builder(getApplicationContext())

                        .setContentTitle("Auto Location")
                        .setContentText("Location Service")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(contentIntent)
                        .setGroup(GROUP_KEY_FOREGROUND)
                        .setOngoing(true).build();
                startForeground(1, notification);
            }
            else{

                //  String GROUP_KEY_FOREGROUND = "com.tpltrakker.main.foreground";

                //for devices less than API Level 26
                Notification notification = new Notification.Builder(getApplicationContext())


                        .setContentTitle("Auto Location")
                        .setContentText("Location Service")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(contentIntent)
                        //  .setGroup(GROUP_KEY_FOREGROUND)
                        .setOngoing(true).build();
                startForeground(1, notification);

            }
        }


    }

}
