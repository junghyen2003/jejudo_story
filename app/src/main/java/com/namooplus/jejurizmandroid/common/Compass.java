package com.namooplus.jejurizmandroid.common;

/**
 * Created by HeungSun-AndBut on 2016. 6. 7..
 */

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Surface;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

//TODO 추후에 나침반, GSP, 조도 값 3가지 모두 한 클래스로 통합 관리
public class Compass implements SensorEventListener {
    private static final String TAG = "Compass";

    private SensorManager sensorManager;
    private Sensor gsensor;
    private Sensor msensor;
    private float azimuth = 0f;
    private float currectAzimuth = 0;

    private float[] temporaryRotationMatrix = new float[9];
    private float[] rotationMatrix = new float[9];
    private float[] accelerometerData = new float[3];
    private float[] magneticData = new float[3];
    private float[] orientationData = new float[3];

    private Activity mActivity;

    // compass arrow to rotate
    public ImageView arrowView = null;

    public Compass(Activity activity) {
        mActivity = activity;
        sensorManager = (SensorManager) mActivity
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

    private void configureDeviceAngle() {
        switch (mActivity.getWindowManager().getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_0: // Portrait
                SensorManager.remapCoordinateSystem(temporaryRotationMatrix, SensorManager.AXIS_Z,
                        SensorManager.AXIS_Y, rotationMatrix);
                break;
            case Surface.ROTATION_90: // Landscape
                SensorManager.remapCoordinateSystem(temporaryRotationMatrix, SensorManager.AXIS_Y,
                        SensorManager.AXIS_MINUS_Z, rotationMatrix);
                break;
            case Surface.ROTATION_180: // Portrait
                SensorManager.remapCoordinateSystem(temporaryRotationMatrix, SensorManager.AXIS_MINUS_Z,
                        SensorManager.AXIS_MINUS_Y, rotationMatrix);
                break;
            case Surface.ROTATION_270: // Landscape
                SensorManager.remapCoordinateSystem(temporaryRotationMatrix, SensorManager.AXIS_MINUS_Y,
                        SensorManager.AXIS_Z, rotationMatrix);
                break;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // get the angle around the z-axis rotated

        int sensorType = event.sensor.getType();
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            accelerometerData = event.values;
        } else if (sensorType == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticData = event.values;
        }

        //값 보정
        SensorManager.getRotationMatrix(temporaryRotationMatrix, null, accelerometerData, magneticData);
        configureDeviceAngle();

        //실제 값 가져오기
        SensorManager.getOrientation(rotationMatrix, orientationData);
        azimuth = (float) Math.toDegrees(orientationData[0]);


        //핸드폰을 가로로 한상태에서 눈에서 45도로 위로 했을때를 기준으로 산출
        int direction = (int) azimuth;
        int result = (int) (azimuth + 180) % 360;
        if (-45 <= direction && direction < 45) {
            Log.i(TAG, "direction : 남쪽");
        } else if (45 <= direction && direction < 125) {
            Log.i(TAG, "direction : 서쪽");
        } else if ((125 <= direction && direction < 180) || (-125 > direction)) {
            Log.i(TAG, "direction : 북쪽");
        } else if (-45 >= direction && direction > -125) {
            Log.i(TAG, "direction : 동쪽");
        }
        //같은 기준으로 했을때 북쪽을 기준으로 했을대 방향
        Log.i(TAG, "integer : " + result);

        if (arrowView == null) {
            Log.i(TAG, "arrow view is not set");
            return;
        }

        //나침반 에니메이션 셋팅
        Animation an = new RotateAnimation(-currectAzimuth, -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        currectAzimuth = azimuth;

        an.setDuration(1000);
        an.setRepeatCount(0);
        an.setFillAfter(true);

        arrowView.startAnimation(an);

        /*
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

                Log.i("HS","currectAzimuth : " + currectAzimuth);
                Log.i("HS","orientation : " + mOrientation);

                arrowView.startAnimation(an);
            }
        }*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}