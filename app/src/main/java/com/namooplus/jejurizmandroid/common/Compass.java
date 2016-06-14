package com.namooplus.jejurizmandroid.common;

/**
 * Created by HeungSun-AndBut on 2016. 6. 7..
 */

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

//TODO 추후에 나침반, GSP, 조도 값 3가지 모두 한 클래스로 통합 관리
public class Compass implements SensorEventListener {
    private static final String TAG = "Compass";

    private SensorManager sensorManager;
    private Sensor gsensor;
    private Sensor msensor;
    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    private float azimuth = 0f;
    private float currectAzimuth = 0;

    // compass arrow to rotate
    public ImageView arrowView = null;

    public Compass(Context context) {
        sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void start() {
        sensorManager.registerListener(this, gsensor,
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, msensor,
                SensorManager.SENSOR_DELAY_GAME);
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    public float getAzimuth() {
        return currectAzimuth;
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        // get the angle around the z-axis rotated
        final float alpha = 0.97f;

        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                mGravity[0] = alpha * mGravity[0] + (1 - alpha) * event.values[0];
                mGravity[1] = alpha * mGravity[1] + (1 - alpha) * event.values[1];
                mGravity[2] = alpha * mGravity[2] + (1 - alpha) * event.values[2];
            }

            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha) * event.values[0];
                mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha) * event.values[1];
                mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha) * event.values[2];
            }

            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                // Log.d(TAG, "azimuth (rad): " + azimuth);
                azimuth = (float) Math.toDegrees(orientation[0]); // orientation
                azimuth = (azimuth + 360) % 360;

                if (arrowView == null) {
                    Log.i(TAG, "arrow view is not set");
                    return;
                }

                Animation an = new RotateAnimation(-currectAzimuth, -azimuth,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                        0.5f);
                currectAzimuth = azimuth;

                an.setDuration(500);
                an.setRepeatCount(0);
                an.setFillAfter(true);

                arrowView.startAnimation(an);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}