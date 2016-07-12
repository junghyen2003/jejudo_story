package com.namooplus.jejurizmandroid.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraHostProvider;
import com.commonsware.cwac.camera.CameraUtils;
import com.commonsware.cwac.camera.CameraView;
import com.commonsware.cwac.camera.PictureTransaction;
import com.commonsware.cwac.camera.SimpleCameraHost;
import com.namooplus.jejurizmandroid.R;
import com.namooplus.jejurizmandroid.common.AppSetting;
import com.namooplus.jejurizmandroid.common.Compass;
import com.namooplus.jejurizmandroid.common.GpsInfo;
import com.namooplus.jejurizmandroid.common.LightInfo;
import com.namooplus.jejurizmandroid.common.Utils;
import com.namooplus.jejurizmandroid.model.ImageInfoModel;
import com.namooplus.jejurizmandroid.view.DrawingView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.hardware.Camera.Parameters.FLASH_MODE_OFF;
import static android.hardware.Camera.Parameters.FLASH_MODE_ON;
import static com.namooplus.jejurizmandroid.common.AppSetting.INTERVAL_MAP_REFRESH;

/**
 * Created by HeungSun-AndBut on 2016. 6. 5..
 */

public class CameraActivity extends AppCompatActivity implements View.OnTouchListener,
        CameraHostProvider {

    private static final String[] ACTIVITY_CAMERA_PERMISSION = {ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int ACCESS_PERMISSION = 3390;

    static final int FOCUS_AREA_WEIGHT = 1000;

    private double mCurrentLat = 0; // 위도
    private double mCurrentLon = 0; // 경도

    private static final Interpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final Interpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();

    @BindView(R.id.activity_camera_take_button)
    public ImageButton mBtnTakePicture;

    @BindView(R.id.activity_camera_cameraview)
    public CameraView mCameraView;

    @BindView(R.id.activity_camera_shutter)
    public View mVShutter;

    @BindView(R.id.activity_camera_drawingview)
    public DrawingView mDvDrawingView;

    @BindView(R.id.activity_camera_location_info)
    public TextView mTxLocation;

    @BindView(R.id.activity_camera_light_info)
    public TextView mTxLight;

    @BindView(R.id.activity_camera_flash_image)
    public ImageView mFlash;

    @BindView(R.id.activity_camera_list_button)
    public ImageView mListButton;

    private List<Camera.Area> mFocusList;
    public static MyCameraHost mMyCameraHost;
    private Compass mCompass;
    private GpsInfo mGpsInfo;
    private LightInfo mLightInfo;

    private String mFlashMode = FLASH_MODE_OFF;

    private float mLightValue;
    private float mCompassValue;

    private Timer mTimer;
    private ArrayList<ImageInfoModel> mImageList;
    private int imageNum = 0;
    private boolean isSaveComplete;
    private int mOrientation;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMyCameraHost = new MyCameraHost(this);
        setContentView(R.layout.activity_camera);

        ButterKnife.bind(CameraActivity.this);

        mCompass = new Compass(this);
        mLightInfo = new LightInfo(this);

        mCompass.arrowView = (ImageView) findViewById(R.id.activity_camera_compass);


        mImageList = new ArrayList<>();

        //마쉬멜로우 권한 확인
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

    private TimerTask runJob = new TimerTask() {
        public void run() {
            CameraActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLightValue = mLightInfo.getmLightValue();
                    mCompassValue = mCompass.getDirection();
                    //LatLng latlng = new LatLng(mCurrentLat, mCurrentLon);

                    mTxLocation.setText("Lat : " + mCurrentLat + " Lon : " + mCurrentLon);
                    mTxLight.setText("조도:" + mLightValue);
                    mOrientation = getResources().getConfiguration().orientation;
                    Log.i("HS","방향 : " + mOrientation);


                }
            });
        }
    };

    //위치 값 리스너
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
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
        if (requestCode == ACCESS_PERMISSION && grantResults.length > 0) {
            //위치 권한
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED && permissions[0].equals(Manifest.permission.CAMERA)) {
                Toast.makeText(this, getResources().getString(R.string.activity_camera_location_permission),
                        Toast.LENGTH_SHORT).show();
                finish();
            } else {
                mGpsInfo = new GpsInfo(this, locationListener);
                mGpsInfo.initLocation();
            }

            //카메라 권한
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED && permissions[0].equals(ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, getResources().getString(R.string.activity_camera_location_permission),
                        Toast.LENGTH_SHORT).show();
                finish();
            } else {
                mGpsInfo = new GpsInfo(this, locationListener);
                mGpsInfo.initLocation();
            }

            //파일 쓰기 권한
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED && permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, getResources().getString(R.string.activity_camera_location_permission),
                        Toast.LENGTH_SHORT).show();
                finish();
            }

            //읽기 쓰기 권한
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, getResources().getString(R.string.activity_camera_location_permission),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
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

    @OnClick(R.id.activity_camera_take_button)
    public void takeCameraClick(View v) {
        onTakePicture(v);
    }

    @OnClick(R.id.activity_camera_flash_image)
    public void flashClick(View v) {
        if (mFlashMode.equals(FLASH_MODE_OFF)) {
            mFlashMode = FLASH_MODE_ON;
        } else {
            mFlashMode = FLASH_MODE_OFF;
        }
    }

    @OnClick(R.id.activity_camera_list_button)
    public void listCameraClick(View v) {
        if(isSaveComplete) {
            Intent i = new Intent(CameraActivity.this, CameraListActivity.class);
            i.putParcelableArrayListExtra("datas", mImageList);
            startActivity(i);
            init();
        } else {
            Toast.makeText(this, "이미지 저장중입니다. 다시 한번 시도하세요",Toast.LENGTH_SHORT).show();
        }
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
            }
        });
        animatorSet.start();
    }

    private void init() {
        imageNum = 0;
        mImageList.clear();
        isSaveComplete = false;
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
        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.schedule(runJob, 0, INTERVAL_MAP_REFRESH);
        }
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
            return new File(AppSetting.SAVE_IMAGE_PATH);
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

        //사진 결과물 사이즈 조정
        private Camera.Size getBestPictureSize(Camera.Parameters parameters) {
            Camera.Size result;
            List<Camera.Size> sizes = parameters.getSupportedPictureSizes();

            result = CameraUtils.getLargestPictureSize(this, parameters, false);

            Collections.sort(sizes, Collections.reverseOrder(new SizeComparator()));
            for (Camera.Size entry : sizes) {

                if (entry.height == 1080 || entry.width == 1920) {
                    result = entry;
                } else {
                    break;
                }

            }

            return result;

        }

        @Override
        public Camera.Parameters adjustPictureParameters(PictureTransaction xact, Camera.Parameters parameters) {
            parameters.setFlashMode(mFlashMode);

            return super.adjustPictureParameters(xact, parameters);
        }

        public Bitmap loadBitmapFromView(View v) {
            Bitmap b = Bitmap.createBitmap(v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
            v.draw(c);
            return b;
        }

        @Override
        public void saveImage(PictureTransaction xact, byte[] image) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            try {
                String path = Utils.saveBitmapToFile(bitmap, imageNum++);

                if (!path.isEmpty()) {
                    mImageList.add(new ImageInfoModel(path, mCurrentLat, mCurrentLon, mLightValue, mCompassValue, mOrientation));
                }

                CameraActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //TODO 이미지가 저장하는 시간이 걸리는데 이것을 어떻게 처리 할지 고민을 해야함.
                        mBtnTakePicture.setEnabled(true);
                    }
                });

                isSaveComplete = true;

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
