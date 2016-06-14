package com.namooplus.jejurizmandroid.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraHostProvider;
import com.commonsware.cwac.camera.CameraUtils;
import com.commonsware.cwac.camera.CameraView;
import com.commonsware.cwac.camera.PictureTransaction;
import com.commonsware.cwac.camera.SimpleCameraHost;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.namooplus.jejurizmandroid.ExcelManager;
import com.namooplus.jejurizmandroid.R;
import com.namooplus.jejurizmandroid.common.CameraConfig;
import com.namooplus.jejurizmandroid.common.Compass;
import com.namooplus.jejurizmandroid.common.GpsInfo;
import com.namooplus.jejurizmandroid.common.LightInfo;
import com.namooplus.jejurizmandroid.common.Utils;
import com.namooplus.jejurizmandroid.view.DrawingView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.google.android.gms.maps.CameraUpdateFactory.newLatLng;

/**
 * Created by HeungSun-AndBut on 2016. 6. 5..
 */

public class CameraActivity extends AppCompatActivity implements View.OnClickListener,
        View.OnTouchListener, CameraHostProvider, OnMapReadyCallback, CompoundButton.OnCheckedChangeListener {

    private static final String[] ACTIVITY_CAMERA_PERMISSION = {ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ,Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int ACCESS_PERMISSION = 3390;


    static final int FOCUS_AREA_WEIGHT = 1000;

    private double mCurrentLat = 0; // 위도
    private double mCurrentLon = 0; // 경도

    private static final Interpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final Interpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();

    private CameraView mCameraView;
    private ImageButton mBtnTakePicture;
    private TextView mTxLocation;
    private View mVShutter;
    private TextView mTxLight;
    private DrawingView mDvDrawingView;
    private List<Camera.Area> mFocusList;
    private Switch mSwitch;


    private ProgressDialog mProgressDialog;
    //화면 방향
    private int mDeviceOrientation;

    private int mCameraWidth;
    private int mCameraHeight;
    private MapFragment mapFragment;
    public static MyCameraHost mMyCameraHost;

    private GoogleMap map;
    private Compass mCompass;
    private GpsInfo mGpsInfo;
    private LightInfo mLightInfo;


    private float mLightValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMyCameraHost = new MyCameraHost(this);

        setContentView(R.layout.activity_camera);

        mCameraView = (CameraView) findViewById(R.id.activity_camera_cameraview);
        mBtnTakePicture = (ImageButton) findViewById(R.id.activity_camera_take_button);
        mVShutter = findViewById(R.id.activity_camera_shutter);
        mDvDrawingView = (DrawingView) findViewById(R.id.activity_camera_drawingview);
        mTxLocation = (TextView) findViewById(R.id.activity_camera_location_info);
        mTxLight = (TextView) findViewById(R.id.activity_camera_light_info);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.activity_camera_map_fragment);
        mCompass = new Compass(this);
        mCompass.arrowView = (ImageView) findViewById(R.id.activity_camera_compass);
        mSwitch = (Switch) findViewById(R.id.activity_camera_map_switch);

        mSwitch.setOnCheckedChangeListener(this);
        mBtnTakePicture.setOnClickListener(this);
        mTxLocation.setOnClickListener(this);
        mCameraView.setOnTouchListener(this);

        mapFragment.getMapAsync(this);
        mapFragment.getView().setVisibility(View.GONE);

        mCameraView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mCameraView.getViewTreeObserver().removeOnPreDrawListener(this);

                mCameraWidth = mCameraView.getWidth();
                mCameraHeight = mCameraView.getHeight();

                return true;
            }
        });

        mTxLocation.setText("Lat : " + mCurrentLat + " Lon : " + mCurrentLon);

        mLightInfo = new LightInfo(this, lightSensorListener);

        //방향전환 감지
        addSensorListener();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                (PackageManager.PERMISSION_GRANTED != checkSelfPermission(ACCESS_FINE_LOCATION) ||
                        (PackageManager.PERMISSION_GRANTED != checkSelfPermission(Manifest.permission.CAMERA)) ||
                        (PackageManager.PERMISSION_GRANTED != checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) ||
                        (PackageManager.PERMISSION_GRANTED != checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)))) {
            requestPermissions(ACTIVITY_CAMERA_PERMISSION, ACCESS_PERMISSION);
        } else {
            mGpsInfo = new GpsInfo(this, locationListener);
            mGpsInfo.initLocation();
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.activity_camera_map_switch:
                if (isChecked) {
                    mapFragment.getView().setVisibility(View.VISIBLE);
                } else {
                    mapFragment.getView().setVisibility(View.GONE);
                }
                break;
        }
    }

    //조도 값 리스너
    private SensorEventListener lightSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                mLightValue = event.values[0];
                DecimalFormat format = new DecimalFormat(".##");
                mTxLight.setText(format.format(mLightValue));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
    //위치 값 리스너
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();

            LatLng latlng = new LatLng(mCurrentLat, mCurrentLon);
            if (map != null) {
                map.moveCamera(newLatLng(latlng));
            }
            Log.i("HS", "location listener");

            mTxLocation.setText("Lat : " + mCurrentLat + " Lon : " + mCurrentLon);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i("HS", "onStatusChanged");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i("HS", "onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i("HS", "onProviderDisabled");
        }
    };


    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        switch (requestCode) {
            case RESULT_OK:
                mGpsInfo = new GpsInfo(this, locationListener);
                mGpsInfo.initLocation();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == ACCESS_PERMISSION && grantResults.length > 0) {
            //위치 권한
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED && permissions[0].equals(Manifest.permission.CAMERA)) {
                Toast.makeText(this, getResources().getString(R.string.activity_camera_location_permission),
                        Toast.LENGTH_SHORT).show();
                finish();
            } else {
                mGpsInfo = new GpsInfo(this, locationListener);
                mGpsInfo.initLocation();
            }

            //카메라 권한
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED && permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, getResources().getString(R.string.activity_camera_location_permission),
                        Toast.LENGTH_SHORT).show();
                finish();
            } else {
                mGpsInfo = new GpsInfo(this, locationListener);
                mGpsInfo.initLocation();
            }

            //파일 쓰기 권한
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED && permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, getResources().getString(R.string.activity_camera_location_permission),
                        Toast.LENGTH_SHORT).show();
                finish();
            }

            //읽기 쓰기 권한
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, getResources().getString(R.string.activity_camera_location_permission),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            Log.i("HS", "onMapReady");
            map = googleMap;
            map.setMyLocationEnabled(true);

            LatLng latlng = new LatLng(mCurrentLat, mCurrentLon);

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13));

        } catch (SecurityException e) {
            Log.i("HS", "onMapReady " + e.getMessage());
        }
    }

    //화면 포커스 맞추기
    private void focusOnTouch(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        Rect touchRect = new Rect(
                (int) (x - 100),
                (int) (y - 100),
                (int) (x + 100),
                (int) (y + 100));

        Rect cameraViewRect = new Rect();
        mCameraView.getLocalVisibleRect(cameraViewRect);

        // 사각형 범위가 카메라뷰 위치를 벗어나는경우 카메라뷰의 최대 위치로 보정한다
        if (touchRect.left < cameraViewRect.left) {
            touchRect.left = cameraViewRect.left;
        }

        if (touchRect.right > cameraViewRect.right) {
            touchRect.right = cameraViewRect.right;
        }

        if (touchRect.top < cameraViewRect.top) {
            touchRect.top = cameraViewRect.top;
        }

        if (touchRect.bottom > cameraViewRect.bottom) {
            touchRect.bottom = cameraViewRect.bottom;
        }


        final Rect targetFocusRect = new Rect(
                touchRect.left * 2000 / mCameraView.getWidth() - FOCUS_AREA_WEIGHT,
                touchRect.top * 2000 / mCameraView.getHeight() - FOCUS_AREA_WEIGHT,
                touchRect.right * 2000 / mCameraView.getWidth() - FOCUS_AREA_WEIGHT,
                touchRect.bottom * 2000 / mCameraView.getHeight() - FOCUS_AREA_WEIGHT);


        doTouchFocus(targetFocusRect);


        // 사각형의 화면 뷰를 1초간 보여주었다가 없앤다
        // Remove the square indicator after 1000 msec
        mDvDrawingView.setHaveTouch(true, touchRect);
        mDvDrawingView.invalidate();


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                mDvDrawingView.setHaveTouch(false, new Rect(0, 0, 0, 0));
                mDvDrawingView.invalidate();
            }
        }, 1000);


    }

    //카메라 방향 알아오기
    private void addSensorListener() {

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {

                float x = event.values[0];
                float y = event.values[1];

                if (x < 5 && x > -5 && y > 5)
                    mDeviceOrientation = 0;
                else if (x < -5 && y < 5 && y > -5)
                    mDeviceOrientation = 90;
                else if (x < 5 && x > -5 && y < -5)
                    mDeviceOrientation = 180;
                else if (x > 5 && y < 5 && y > -5)
                    mDeviceOrientation = 270;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // TODO Auto-generated method stub

            }
        }, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    public void doTouchFocus(Rect tfocusRect) {
        try {

            // Area 리스트에 넣고 포커스를 잡는다
            mFocusList = new ArrayList<Camera.Area>();
            Camera.Area focusArea = new Camera.Area(tfocusRect, FOCUS_AREA_WEIGHT);
            mFocusList.add(focusArea);

            mCameraView.autoFocus();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_camera_take_button:
                onTakePicture(v);
                break;
        }
    }

    public void afterTakenPicture(File file) {
        ExcelManager.getInstance().saveExcelFile(file.getAbsolutePath(),
                mLightValue, mCompass.getAzimuth(), mCurrentLat, mCurrentLon);
        mBtnTakePicture.setEnabled(true);
        setProgress(false);
        Toast.makeText(CameraActivity.this, "작업 완료", Toast.LENGTH_SHORT).show();
    }

    private void takePicture() {
        try {
            mCameraView.takePicture(false, true);
            mBtnTakePicture.setEnabled(false);
            animateShutter();
        } catch (IllegalStateException ex) {
            Toast.makeText(CameraActivity.this, "사진찍기 에러 : " + ex.toString(), Toast.LENGTH_SHORT).show();

        }
    }

    public void onTakePicture(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && mFocusList == null
                ) {
            mCameraView.autoFocus();
        } else {
            takePicture();
        }

    }

    private void setProgress(boolean show) {
        if (show) {
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(CameraActivity.this);
                mProgressDialog.setMessage(getString(R.string.activity_camera_progress));
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            } else if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        } else {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }

    }

    private void animateShutter() {
        mVShutter.setVisibility(View.VISIBLE);
        mVShutter.setAlpha(0.f);

        ObjectAnimator alphaInAnim = ObjectAnimator.ofFloat(mVShutter, "alpha", 0f, 0.8f);
        alphaInAnim.setDuration(100);
        alphaInAnim.setStartDelay(100);
        alphaInAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        ObjectAnimator alphaOutAnim = ObjectAnimator.ofFloat(mVShutter, "alpha", 0.8f, 0f);
        alphaOutAnim.setDuration(200);
        alphaOutAnim.setInterpolator(DECELERATE_INTERPOLATOR);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(alphaInAnim, alphaOutAnim);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mVShutter.setVisibility(View.GONE);
                setProgress(true);
            }
        });
        animatorSet.start();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.activity_camera_cameraview:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        focusOnTouch(event);
                    }
                }
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCompass.start();
        mLightInfo.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCompass.stop();
        mLightInfo.stop();
        mCameraView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCompass.start();
        mLightInfo.start();
        mCameraView.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLightInfo.stop();
        mCompass.stop();
    }


    @Override
    public CameraHost getCameraHost() {
        return mMyCameraHost;
    }

    class MyCameraHost extends SimpleCameraHost {

        final double SIZE_MULTIPLE = 1.5;
        Activity activity;
        Camera.Size bestPictureSize;

        public MyCameraHost(Activity activity) {
            super(activity);
            this.activity = activity;
        }

        @Override
        protected File getPhotoDirectory() {
            return new File(CameraConfig.SAVE_IMAGE_PATH);
        }

        @Override
        public boolean useFullBleedPreview() {
            return true;
        }

        // 미리보기 화면에서 사진 크기 해상도를 미리 가져온다
        @Override
        public Camera.Parameters adjustPreviewParameters(Camera.Parameters parameters) {
            bestPictureSize = getBestPictureSize(parameters);
            return super.adjustPreviewParameters(parameters);
        }

        @Override
        public Camera.Size getPictureSize(PictureTransaction xact, Camera.Parameters parameters) {


            //   return super.getPictureSize(xact, parameters);
            if (bestPictureSize == null) {
                bestPictureSize = getBestPictureSize(parameters);
            }

            return bestPictureSize;

        }

        private Camera.Size getBestPictureSize(Camera.Parameters parameters) {
            Camera.Size result;

            if (mCameraWidth == 0) {
                return CameraUtils.getLargestPictureSize(this, parameters, false);
            }
            List<Camera.Size> sizes = parameters.getSupportedPictureSizes();

            Collections.sort(sizes,
                    Collections.reverseOrder(new SizeComparator()));
            result = sizes.get(sizes.size() - 1);

            for (Camera.Size entry : sizes) {

                if (entry.height >= mCameraWidth * SIZE_MULTIPLE && entry.width >= mCameraHeight * SIZE_MULTIPLE) {
                    result = entry;
                } else {
                    break;
                }


            }
            return result;

        }

        private Bitmap getCorrectOrientImage(Bitmap bitmap) {
            bitmap = Utils.rotate(bitmap, mDeviceOrientation);
            return bitmap;
        }

        private Bitmap getCorrectOrientImage(Bitmap bitmap, String path) {

            ExifInterface exif = null;
            try {

                exif = new ExifInterface(path);

                if (exif != null) {
                    int exifOrientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int exifDegree = Utils.exifOrientationToDegrees(exifOrientation);
                    bitmap = Utils.rotate(bitmap, exifDegree);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return bitmap;


        }


        @Override
        public Camera.Parameters adjustPictureParameters(PictureTransaction xact, Camera.Parameters parameters) {

            if (CameraConfig.FLASH_SETTING_VALUE) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            }

            return super.adjustPictureParameters(xact, parameters);
        }

        @Override
        public void saveImage(PictureTransaction xact, byte[] image) {

            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

            try {

                final File photo = getPhotoPath();

                if (photo.exists()) {
                    photo.delete();
                }


                // 회전값을 보정한다
                bitmap = Utils.rotate(bitmap, mDeviceOrientation);
                //bitmap = getCorrectOrientImage(bitmap, photo.toString());
                // bitmap = getCorrectOrientImage(bitmap, photo.toString());

                float ratio = mCameraHeight / mCameraWidth;

                Bitmap crop_bitmap = Utils.cropCenterBitmap(bitmap, bitmap.getWidth(), (int) (bitmap.getWidth() * ratio));
                FileOutputStream fos;

                fos = new FileOutputStream(photo);

                crop_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                fos.flush();
                fos.close();


                // 사진을 저장한뒤 미디어를 스캔해서 저장한 파일을 읽어온다
                MediaScannerConnection.scanFile(activity, new String[]{photo.getPath()}, new String[]{"image/jpeg"}, new MediaScannerConnection.MediaScannerConnectionClient() {
                    @Override
                    public void onMediaScannerConnected() {

                    }

                    @Override
                    public void onScanCompleted(String path, Uri uri) {

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                afterTakenPicture(photo);
                            }
                        });

                    }
                });


            } catch (Exception e) {
                handleException(e);

                e.printStackTrace();
            }

        }

        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            try {

                // 터치해서 포커스 잡는 경우
                if (mFocusList != null) {
                    Camera.Parameters param = camera.getParameters();

                    int maxNumFocusAreas = param.getMaxNumFocusAreas();
                    int maxNumMeteringAreas = param.getMaxNumMeteringAreas();

                    param.setFocusAreas(mFocusList);
                    param.setMeteringAreas(mFocusList);

                    camera.setParameters(param);
                    super.onAutoFocus(success, camera);
                }
                // 아무터치하지않고 그냥 바로 촬영버튼 누른경우
                else {
                    //  super.onAutoFocus(success, camera);
                    takePicture();
                }


            } catch (Exception e) {
                e.printStackTrace();

            }

        }

        private class SizeComparator implements
                Comparator<Camera.Size> {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                int left = lhs.width * lhs.height;
                int right = rhs.width * rhs.height;

                if (left < right) {
                    return (-1);
                } else if (left > right) {
                    return (1);
                }

                return (0);
            }


        }

    }

}
