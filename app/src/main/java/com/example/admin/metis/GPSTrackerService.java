package com.example.admin.metis;
import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.os.Binder;

public class GPSTrackerService extends Service implements LocationListener {

    private final String TAG = "Metis-Application: ";
    private final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private final String NETWORK_ACCESS = Manifest.permission.ACCESS_NETWORK_STATE;
    private final String WIFI_ACCESS = Manifest.permission.ACCESS_WIFI_STATE;

    private final IBinder gpsTrackerBinder = new TheBinder();
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private final int NETWORK_PERMISSION_REQUEST_CODE = 2525;

    private LocationManager locationManager;
    private Location location;
    private Activity uiActivity;

    public GPSTrackerService() {
        Log.i(TAG, "GPSTrackerService default constructor");
    }

    public GPSTrackerService(Activity uiActivity) {
        this.uiActivity = uiActivity;
        initData();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return gpsTrackerBinder;
    }

    public class TheBinder extends Binder {
        GPSTrackerService getService() {
            return GPSTrackerService.this;
        }
    }

    private void initData() {
        locationManager = (LocationManager) uiActivity.getSystemService(Context.LOCATION_SERVICE);
    }

    private boolean is_network_permission_granted() {
        return locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);
    }

    private boolean is_GPS_permission_granted() {
        return (ContextCompat.checkSelfPermission(uiActivity, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                ||
                ContextCompat.checkSelfPermission(uiActivity, COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    public boolean isGPSEnable() {
        return locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
    }

    private boolean isNetworkEnable() {
        return locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);
    }

    @SuppressWarnings("MissingPermission")
    public boolean initDeviceLocation(Activity uiActivity) {
        this.uiActivity = uiActivity;
        initData();
        if(initDeviceLocationByGps()) {
            return true;
        }
        else if(initDeviceLocationByNetwork()){
            return true;
        }
        else{
            Log.e(TAG, "no gps/network provide by android :(");
            return false;
        }
    }

    public Location getDeviceLocation() {
        if(location == null)
            Log.w(TAG,"location object is null");
        return location;
    }

    @SuppressWarnings("MissingPermission")
    private boolean initDeviceLocationByGps(){
        String[] locationPermissions = {FINE_LOCATION, COARSE_LOCATION};

        Log.d(TAG,"try to init device location with gps");

        if (!is_GPS_permission_granted()) {
            ActivityCompat.requestPermissions(uiActivity, locationPermissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
        if (isGPSEnable()) {
            if (location == null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10 * 1000, 10, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(location != null){
                        Log.i(TAG, "GPS is enable + permissions");
                        return true;
                    }
                }
            }
        }

        Log.w(TAG,"GPS is not enable, try to connect through network");
        return false;
    }

    @SuppressWarnings("MissingPermission")
    private boolean initDeviceLocationByNetwork(){
        String[] networkPermission = {NETWORK_ACCESS};

        Log.d(TAG,"try to init device location with network");

        if (!is_GPS_permission_granted() && locationManager == null) {
            ActivityCompat.requestPermissions(uiActivity, networkPermission, NETWORK_PERMISSION_REQUEST_CODE);
        }
        if (isNetworkEnable()) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, this);
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if(location != null){
                    Log.i(TAG, "Network is enable + permissions");
                    return true;
                }
            }
        }

        Log.e(TAG,"Network service is not enable");
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //This method is called by the activity( bindService() ) who is looking for bind this service.
    //onBind() return IBinder object that defines the interface that the client use to interact
    //with the service

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG,"onLocationChanged : the device relocation itself");
        initDeviceLocation(uiActivity);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}
