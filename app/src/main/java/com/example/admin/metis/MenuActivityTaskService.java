package com.example.admin.metis;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * This Service class make the OS to implements the onDestroy callback of MenuActivity
 */

public class MenuActivityTaskService extends Service {

//    private static final String TAG = "Metis-Application: ";
//    private static final String USERS_NODE = "Users";
//    private static final String USER_ID = "userId";
//    private FirebaseDatabase firebaseDatabase;
//    private DatabaseReference databaseReference;
//    private String userId;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        userId = intent.getStringExtra(USER_ID);
//        firebaseDatabase = FirebaseDatabase.getInstance();
//        databaseReference = firebaseDatabase.getReference().child(BAR_NAME).child(USERS_NODE).child(userId);
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

