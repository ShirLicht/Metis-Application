package com.example.admin.metis;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import static android.Manifest.permission.CAMERA;

public class BarcodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private static final String FULL_TABLE_URL = "fullTableUrl";

    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkPermission()){
                Toast.makeText(BarcodeActivity.this, "Permission is granted!",Toast.LENGTH_LONG).show();
            }else
                requestPermission();
        }
    }

    private boolean checkPermission(){
        return (ContextCompat.checkSelfPermission(BarcodeActivity.this,
                CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    public void onRequestPermissionsReuslt(int requestCode, String permission[], int grantResults[]){
        switch(requestCode)
        {
            case REQUEST_CAMERA:
                if(grantResults.length > 0){
                    boolean camreaAccapted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(camreaAccapted){
                        Toast.makeText(BarcodeActivity.this, "Camera permission is granted!",Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(BarcodeActivity.this, "Permission Denied!",Toast.LENGTH_LONG).show();
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            if(shouldShowRequestPermissionRationale(CAMERA)){
                                displayAlertMessage("You need to allow access for both permission",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                                    requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener){
        new AlertDialog.Builder(BarcodeActivity.this)
                .setMessage(message)
                .setPositiveButton("OK",listener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void handleResult(final Result result) {
        final String scanReuslt = result.getText();
        Intent intent = new Intent(BarcodeActivity.this , TableActivity.class);
        intent.putExtra(FULL_TABLE_URL, scanReuslt);
        startActivity(intent);
        finish();
    }

    public void onResume(){
        super.onResume();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkPermission()){
                if(scannerView == null){
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();

            }
        }
    }

    public void onDestroy(){
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void onBackPressed(){
        finish();
    }
}
