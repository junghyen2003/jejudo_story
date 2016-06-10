package com.namooplus.jejurizmandroid.common;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by HeungSun-AndBut on 2016. 6. 9..
 */

//TODO 추후에 나침반, GSP, 조도 값 3가지 모두 한 클래스로 통합 관리
public class LightInfo {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private SensorEventListener sensorEventListener;

    public LightInfo(Context mContext, SensorEventListener sensorEventListener) {
        this.sensorEventListener = sensorEventListener;
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE); // SystemService 중 sensor service를 얻어 옴
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT); // sensor service들 중 조도 센서와 연결
    }

    public void start() {
        mSensorManager.registerListener(sensorEventListener, mSensor, SensorManager.SENSOR_DELAY_UI);
    }

    public void stop() {
        mSensorManager.unregisterListener(sensorEventListener);
    }

}
