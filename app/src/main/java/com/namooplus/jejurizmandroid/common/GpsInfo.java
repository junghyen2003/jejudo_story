package com.namooplus.jejurizmandroid.common;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.namooplus.jejurizmandroid.R;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by HeungSun-AndBut on 2016. 6. 7..
 */

public class GpsInfo {

    private final Context mContext;

    Location location;
    double lat; // 위도
    double lon; // 경도

    // GPS UPDATE DISTANCE (1m)
    private static final long MIN_DISTANCE_UPDATES = 1;

    // GPS UPDATE TIME (1sec)
    private static final long MIN_TIME_UPDATES = 1000 * 1 * 1;

    private LocationListener locationListener;
    protected LocationManager locationManager;

    public GpsInfo(Context context, LocationListener listener) {
        this.mContext = context;
        locationListener = listener;
    }

    public boolean checkLocation() {
        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

        //GPS가 사용가능한지
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showSettingsAlert();
            return false;
        } else {
            return true;
        }
    }

    public Location initLocation() {
        try {
            if (checkLocation()) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER, MIN_TIME_UPDATES, MIN_DISTANCE_UPDATES,
                                locationListener);

                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                lat = location.getLatitude();
                                lon = location.getLongitude();
                            }
                    }
                }
            }
        } catch (SecurityException e) {
            Log.i("HS","SecurityException : " + e.getMessage());
        }
        return location;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        alertDialog.setTitle(mContext.getResources().getString(R.string.activity_camera_gps_setting));
        alertDialog.setMessage(mContext.getResources().getString(R.string.activity_camera_gps_message));

        // OK 를 누르게 되면 설정창으로 이동합니다.
        alertDialog.setPositiveButton(mContext.getString(R.string.activity_camera_gps_go),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                        dialog.dismiss();

                    }
                });
        // Cancle 하면 종료 합니다.
        alertDialog.setNegativeButton(mContext.getString(R.string.activity_camera_gps_no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

}