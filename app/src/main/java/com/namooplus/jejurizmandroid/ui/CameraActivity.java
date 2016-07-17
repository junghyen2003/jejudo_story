package com.namooplus.jejurizmandroid.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.namooplus.jejurizmandroid.view.CustomTextView;
import com.namooplus.jejurizmandroid.view.DrawingView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.hardware.Camera.Parameters.FLASH_MODE_OFF;
import static android.hardware.Camera.Parameters.FLASH_MODE_ON;
import static com.namooplus.jejurizmandroid.common.AppSetting.ACTIVITY_CODE_CAMERA_LIST;
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

    @BindView(R.id.activity_camera_flash_image)
    public ImageView mFlash;

    @BindView(R.id.activity_camera_list_button)
    public ImageView mListButton;

    public CustomTextView mCtxLocation;
    public CustomTextView mCtxLight;

    public TextView mTxLocation;
    public TextView mTxLight;


    private List<Camera.Area> mFocusList;
    public static MyCameraHost mMyCameraHost;
    private Compass mCompass;
    private GpsInfo mGpsInfo;
    private LightInfo mLightInfo;

    private String mFlashMode = FLASH_MODE_OFF;

    private float mLightValue;
    private float mCompassValue;

    private ArrayList<ImageInfoModel> mImageList;
    private boolean isSaveComplete;
    private int mOrientation;
    private boolean isRunJob;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMyCameraHost = new MyCameraHost(this);
        setContentView(R.layout.activity_camera);

        ButterKnife.bind(CameraActivity.this);

        int ori = getResources().getConfiguration().orientation;

        if (ori == Configuration.ORIENTATION_PORTRAIT) // 세로 전환시
        {
            mCtxLocation = (CustomTextView)findViewById(R.id.activity_camera_location_info);
            mCtxLight = (CustomTextView)findViewById(R.id.activity_camera_light_info);
        }
        else if (ori == Configuration.ORIENTATION_LANDSCAPE)// 가로 전환시
        {
            mTxLocation = (TextView)findViewById(R.id.activity_camera_location_info);
            mTxLight = (TextView)findViewById(R.id.activity_camera_light_info);
        }

        mCompass = new Compass(this);
        mLightInfo = new LightInfo(this);
        mImageList = new ArrayList<>();
        mCompass.arrowView = (ImageView) findViewById(R.id.activity_camera_compass);

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

    @Override
    public void onBackPressed() {
        if (mImageList.isEmpty()) {
            super.onBackPressed();
        } else {
            checkDialog();
        }
    }

    private void checkDialog() {
        AlertDialog.Builder db = new AlertDialog.Builder(this);
        db.setTitle("저장된 사진이 있습니다.")
                .setMessage("계속하시면 삭제됩니다.")
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.deleteFile(mImageList);
                        finish(); //확인버튼 누루면 앱 종료
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }


    private void loopJob() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (isRunJob) {
                    try {
                        Thread.sleep(INTERVAL_MAP_REFRESH);
                        timerJob();
                    } catch (Exception e) {
                    }
                }
            }
        }.start();
    }

    private void timerJob() {
        CameraActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLightValue = mLightInfo.getmLightValue();
                mCompassValue = mCompass.getDirection();

                if(mTxLocation != null) {
                    mTxLocation.setText("Lat : " + mCurrentLat + " Lon : " + mCurrentLon);
                    mTxLight.setText("조도:" + mLightValue);
                } else if(mCtxLocation != null) {
                    mCtxLocation.setText("Lat : " + mCurrentLat + " Lon : " + mCurrentLon);
                    mCtxLight.setText("조도:" + mLightValue);
                }


                mOrientation = getResources().getConfiguration().orientation;
            }
        });
    }

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTIVITY_CODE_CAMERA_LIST:
                //앞 페이지에서 이미지를 삭제 할수도있다. 그래서 리스트 재 구성
                mImageList.clear();
                if (data != null) {
                    ArrayList<ImageInfoModel> list = data.getParcelableArrayListExtra("datas");
                    if (!list.isEmpty()) {
                        mImageList.addAll(list);
                    }
                }

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
            }

            //카메라 권한
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED && permissions[0].equals(ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, getResources().getString(R.string.activity_camera_location_permission),
                        Toast.LENGTH_SHORT).show();
                finish();
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

            mGpsInfo = new GpsInfo(this, locationListener);
            mGpsInfo.initLocation();
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
            mFlash.setImageResource(R.drawable.ic_flash_on_white_24dp);
        } else {
            mFlashMode = FLASH_MODE_OFF;
            mFlash.setImageResource(R.drawable.ic_flash_off_white_24dp);
        }
    }

    @OnClick(R.id.activity_camera_list_button)
    public void listCameraClick(View v) {
        if (isSaveComplete && !mImageList.isEmpty()) {
            Intent i = new Intent(CameraActivity.this, CameraListActivity.class);
            i.putParcelableArrayListExtra("datas", mImageList);
            startActivityForResult(i, ACTIVITY_CODE_CAMERA_LIST);
        } else {
            Toast.makeText(this, "다시 한번 시도하세요", Toast.LENGTH_SHORT).show();
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
        isRunJob = true;
        loopJob();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCompass.stop();
        mLightInfo.stop();
        isRunJob = false;
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
            return new File(AppSetting.SAVE_IMAGE_TEMP_PATH);
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
            try {
                String path = Utils.saveByteToFile(image, mImageList.size());

                if (!path.isEmpty()) {
                    mImageList.add(new ImageInfoModel(path, mCurrentLat, mCurrentLon, mLightValue, mCompassValue, mOrientation));
                }

                CameraActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBtnTakePicture.setEnabled(true);
                        isSaveComplete = true;
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
