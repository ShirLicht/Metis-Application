package com.example.admin.metis;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * This Service class make the OS to implements the onDestroy callback of MenuActivity
 */

public class MenuActivityTaskService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
       //databaseReference.removeValue();
       stopSelf();
    }
}

