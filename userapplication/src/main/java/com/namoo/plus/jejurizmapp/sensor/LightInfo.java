package com.namoo.plus.jejurizmapp.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.text.DecimalFormat;

/**
 * Created by HeungSun-AndBut on 2016. 6. 9..
 */

//TODO 추후에 나침반, GSP, 조도 값 3가지 모두 한 클래스로 통합 관리
public class LightInfo implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private float mLightValue;
    public LightInfo(Context mContext) {
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE); // SystemService 중 sensor service를 얻어 옴
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT); // sensor service들 중 조도 센서와 연결
    }

    public void start() {
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    public void stop() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            mLightValue = event.values[0];
            DecimalFormat format = new DecimalFormat(".##");
        }
    }

    public float getmLightValue() {
        return mLightValue;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
