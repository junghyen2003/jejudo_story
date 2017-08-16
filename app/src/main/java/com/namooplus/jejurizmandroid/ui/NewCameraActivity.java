package com.namooplus.jejurizmandroid.ui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.namooplus.jejurizmandroid.R;
import com.namooplus.jejurizmandroid.common.Compass;
import com.namooplus.jejurizmandroid.common.GpsInfo;
import com.namooplus.jejurizmandroid.common.LightInfo;
import com.namooplus.jejurizmandroid.common.Utils;
import com.namooplus.jejurizmandroid.model.ImageInfoModel;
import com.namooplus.jejurizmandroid.model.ImageParameters;
import com.namooplus.jejurizmandroid.view.CustomTextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.hardware.Camera.Parameters.FLASH_MODE_OFF;
import static android.hardware.Camera.Parameters.FLASH_MODE_ON;
import static com.namooplus.jejurizmandroid.common.AppSetting.ACTIVITY_CODE_CAMERA_LIST;
import static com.namooplus.jejurizmandroid.common.AppSetting.ACTIVITY_TYPE_CAMERA_LANDSCAPE;
import static com.namooplus.jejurizmandroid.common.AppSetting.INTERVAL_MAP_REFRESH;

/**
 * Created by HeungSun-AndBut on 2016. 7. 18..
 */

public class NewCameraActivity extends AppCompatActivity implements SurfaceHolder.Callback, Camera.PictureCallback {

    // 현재 GPS 사용 여부
    boolean isGPSEnabled = false;

    private static final String[] ACTIVITY_CAMERA_PERMISSION = {ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.READ_EXTERNAL_STORAGE};

    public static final String CAMERA_ID_KEY = "camera_id";
    public static final String CAMERA_FLASH_KEY = "flash_mode";
    public static final String IMAGE_INFO = "image_info";

    private static final int PICTURE_SIZE_MAX_WIDTH = 1280;
    private static final int PREVIEW_SIZE_MAX_WIDTH = 640;

    private static final int ACCESS_PERMISSION = 3390;
    private static final String IMAGE_SAVE_DATA = "image_save_data";

    private double mCurrentLat = 0; // 위도
    private double mCurrentLon = 0; // 경도

    @BindView(R.id.activity_camera_take_button)
    public ImageButton mBtnTakePicture;

    @BindView(R.id.activity_camera_flash_image)
    public ImageView mFlash;

    @BindView(R.id.activity_camera_list_button)
    public ImageView mListButton;

    @BindView(R.id.activity_camera_wall_top)
    public LinearLayout mLlWallTop;

    @BindView(R.id.activity_camera_wall_right)
    public LinearLayout mLlWallRight;

    @BindView(R.id.activity_camera_wall_left)
    public LinearLayout mLlWallLeft;

    @BindView(R.id.activity_camera_wall_bottom)
    public LinearLayout mLlWallBottom;

    @BindView(R.id.activity_camera_top_layout)
    public RelativeLayout mRlTopLayout;

    @BindView(R.id.activity_camera_preview)
    public SquareCameraPreview mPreviewView;

    public CustomTextView mCtxLocation;
    public CustomTextView mCtxLight;

    public TextView mTxLocation;
    public TextView mTxLight;


    private Compass mCompass;
    private GpsInfo mGpsInfo;
    private LightInfo mLightInfo;

    private float mLightValue;
    private float mCompassValue;
    private float mViewCompass;

    private int mOrientation;
    private boolean isRunJob;
    private int ori;

    private boolean mIsSafeToTakePhoto = false;

    private int mCameraID;
    private Camera mCamera;

    private ImageParameters mImageParameters;

    private CameraOrientationListener mOrientationListener;

    private SurfaceHolder mSurfaceHolder;
    private ArrayList<ImageInfoModel> mImageList;

    private String mFlashMode = FLASH_MODE_OFF;

    private static final int THREAD_POOL_NUM = 2;
    private static final int THREAD_MAX_POOL_NUM = 2; //LinkedBlockingQueue 이거 사용시에는 의미 없음
    private static final int THREAD_TIME_OUT = 60;
    ThreadPoolExecutor threadPool = new ThreadPoolExecutor(THREAD_POOL_NUM, THREAD_MAX_POOL_NUM, THREAD_TIME_OUT, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);

        //관광지 촬영일때는 어두운 벽 없애기
        if (getIntent().getIntExtra("type", 0) == ACTIVITY_TYPE_CAMERA_LANDSCAPE) {
            mLlWallTop.setVisibility(View.GONE);
            mLlWallBottom.setVisibility(View.GONE);
            mLlWallRight.setVisibility(View.GONE);
            mLlWallLeft.setVisibility(View.GONE);
        }

        //가로 세로에 대한 처리;;;;;조금 더 편한 방법을 찾고 싶었지만...
        ori = getResources().getConfiguration().orientation;
        if (ori == Configuration.ORIENTATION_PORTRAIT) {
            mCtxLocation = (CustomTextView) findViewById(R.id.activity_camera_location_info);
            mCtxLight = (CustomTextView) findViewById(R.id.activity_camera_light_info);
        } else if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            mTxLocation = (TextView) findViewById(R.id.activity_camera_location_info);
            mTxLight = (TextView) findViewById(R.id.activity_camera_light_info);
        }

        //가로세로 변환시에 데이터 들고오기
        if (savedInstanceState != null) {
            mImageList = savedInstanceState.getParcelableArrayList(IMAGE_SAVE_DATA);
            mCameraID = savedInstanceState.getInt(CAMERA_ID_KEY);
            mFlashMode = savedInstanceState.getString(CAMERA_FLASH_KEY);
            mImageParameters = savedInstanceState.getParcelable(IMAGE_INFO);
        } else {
            mImageList = new ArrayList<>();
            mCameraID = getBackCameraID();
            mImageParameters = new ImageParameters();
            mFlashMode = FLASH_MODE_OFF;
        }

        mPreviewView.getHolder().addCallback(this);

        //각종 센서값을 얻기 위한 초기화
        mCompass = new Compass(this);
        mLightInfo = new LightInfo(this);
        mCompass.arrowView = (ImageView) findViewById(R.id.activity_camera_compass);

        mOrientationListener = new CameraOrientationListener(this);

        //권한 확인
        turnGPSOn();

        mOrientationListener.enable();
    }

    @Override
    public void onPictureTaken(final byte[] data, Camera camera) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    int rotation = getPhotoRotation();
                    Log.i("HS", "rotation : " + rotation);
                    String path = null;

                    //세로로 찍은 사진은
                    if (rotation == 0) {
                        rotation = 90;
                    } else if (rotation == 90) {
                        rotation = 0;
                    }

                    if (rotation != 0) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        Bitmap oldBitmap = bitmap;

                        Matrix matrix = new Matrix();
                        matrix.postRotate(rotation);

                        bitmap = Bitmap.createBitmap(
                                oldBitmap, 0, 0, oldBitmap.getWidth(), oldBitmap.getHeight(), matrix, false
                        );

                        oldBitmap.recycle();
                        path = Utils.saveBitmapToFile(bitmap);
                    } else {
                        path = Utils.saveByteToFile(data);
                    }
                    if (path != null) {
                        ExifInterface exifi = new ExifInterface(path);
                        Log.i("HS", "exifi ori :" + exifi.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1));
                        exifi.setAttribute(ExifInterface.TAG_GPS_LATITUDE, String.valueOf(mCurrentLat));
                        exifi.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, String.valueOf(mCurrentLon));
                        exifi.saveAttributes();
                        mImageList.add(new ImageInfoModel(path, mCurrentLat, mCurrentLon, mLightValue, mCompassValue, mOrientation));
                    }
                } catch (Exception e) {

                }
            }
        });

        Toast.makeText(this, getString(R.string.activity_camera_click_taken), Toast.LENGTH_SHORT).show();
        restartPreview();
        setSafeToTakePhoto(true);
        mBtnTakePicture.setEnabled(true);
    }

    private void getCamera(int cameraID) {
        try {
            mCamera = Camera.open(cameraID);
            mPreviewView.setCamera(mCamera);
        } catch (Exception e) {
            Log.d("HS", "Can't open camera with id " + cameraID);
            e.printStackTrace();
        }
    }

    private void restartPreview() {
        //TODO 최적화가 필요해 보임
        if (mCamera != null) {
            stopCameraPreview();
            mCamera.release();
            mCamera = null;
        }

        getCamera(mCameraID);
        startCameraPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceHolder = holder;

        getCamera(mCameraID);
        startCameraPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

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
    protected void onStop() {
        super.onStop();
        mCompass.stop();
        mLightInfo.stop();
        isRunJob = false;

        mOrientationListener.disable();

        // stop the preview
        if (mCamera != null) {
            stopCameraPreview();
            mCamera.release();
            mCamera = null;
        }

        super.onStop();

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
        NewCameraActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLightValue = mLightInfo.getmLightValue();
                mCompassValue = mCompass.getDirection();


                if (mTxLocation != null) {
                    mTxLocation.setText("Lat : " + mCurrentLat + " Lon : " + mCurrentLon);
                    mTxLight.setText("조도:" + mLightValue);
                } else if (mCtxLocation != null) {
                    mCtxLocation.setText("Lat : " + mCurrentLat + " Lon : " + mCurrentLon);
                    mCtxLight.setText("조도:" + mLightValue);
                }

                float current = mCompass.getAzimuth();

                //나침반 에니메이션 셋팅
                Animation an = new RotateAnimation(-mViewCompass, -current,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                mViewCompass = current;

                an.setDuration(1000);
                an.setRepeatCount(0);
                an.setFillAfter(true);

                mCompass.arrowView.startAnimation(an);
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
    public void onSaveInstanceState(Bundle outState) {
//        Log.d(TAG, "onSaveInstanceState");
        outState.putInt(CAMERA_ID_KEY, mCameraID);
        outState.putString(CAMERA_FLASH_KEY, mFlashMode);
        outState.putParcelable(IMAGE_INFO, mImageParameters);
        outState.putParcelableArrayList(IMAGE_SAVE_DATA, mImageList);
        super.onSaveInstanceState(outState);
    }

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

    @OnClick(R.id.activity_camera_take_button)
    public void takeCameraClick(View v) {
        takePicture();
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
        setFlashMode();
    }

    @OnClick(R.id.activity_camera_list_button)
    public void listCameraClick(View v) {
        if (mImageList.isEmpty()) {
            Toast.makeText(this, getString(R.string.activity_camera_click_list_no_taken),
                    Toast.LENGTH_SHORT).show();
        } else if (threadPool.getActiveCount() > 0) {
            Toast.makeText(this, getString(R.string.activity_camera_click_list_no_save),
                    Toast.LENGTH_SHORT).show();
        } else {
            Intent i = new Intent(this, CameraListActivity.class);
            i.putParcelableArrayListExtra("datas", mImageList);
            startActivityForResult(i, ACTIVITY_CODE_CAMERA_LIST);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            restartPreview();
        }
    }

    @Override
    protected void onPause() {

        super.onPause();
    }


    private void stopCameraPreview() {
        setSafeToTakePhoto(false);
        setCameraFocusReady(false);

        // Nulls out callbacks, stops face detection
        mCamera.stopPreview();
        mPreviewView.setCamera(null);
    }

    private void determineDisplayOrientation() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraID, cameraInfo);

        // Clockwise rotation needed to align the window display to the natural position
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0: {
                degrees = 0;
                break;
            }
            case Surface.ROTATION_90: {
                degrees = 90;
                break;
            }
            case Surface.ROTATION_180: {
                degrees = 180;
                break;
            }
            case Surface.ROTATION_270: {
                degrees = 270;
                break;
            }
        }

        int displayOrientation;

        // CameraInfo.Orientation is the angle relative to the natural position of the device
        // in clockwise rotation (angle that is rotated clockwise from the natural position)
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            // Orientation is angle of rotation when facing the camera for
            // the camera image to match the natural orientation of the device
            displayOrientation = (cameraInfo.orientation + degrees) % 360;
            displayOrientation = (360 - displayOrientation) % 360;
        } else {
            displayOrientation = (cameraInfo.orientation - degrees + 360) % 360;
        }

        mImageParameters.mDisplayOrientation = displayOrientation;
        mImageParameters.mLayoutOrientation = degrees;

        mCamera.setDisplayOrientation(mImageParameters.mDisplayOrientation);
    }

    /**
     * Start the camera preview
     */
    private void startCameraPreview() {
        determineDisplayOrientation();
        setupCamera();

        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();

            setSafeToTakePhoto(true);
            setCameraFocusReady(true);
        } catch (IOException e) {
            Log.d("HS", "Can't start camera preview due to IOException " + e);
            e.printStackTrace();
        }
    }


    private int getPhotoRotation() {
        Display display = getWindowManager().getDefaultDisplay();
        int rotation = 0;
        switch (display.getRotation()) {
            case Surface.ROTATION_0:
                rotation = 0;
                break;
            case Surface.ROTATION_90:
                rotation = 90;
                break;
            case Surface.ROTATION_180:
                rotation = 180;
                break;
            case Surface.ROTATION_270:
                rotation = 270;
                break;
        }
        return rotation;
    }

    private void setFlashMode() {
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFlashMode(mFlashMode);
        mCamera.setParameters(parameters);
    }

    /**
     * Setup the camera parameters
     */
    private void setupCamera() {
        // Never keep a global parameters
        Camera.Parameters parameters = mCamera.getParameters();

        Camera.Size bestPreviewSize = determineBestPreviewSize(parameters);
        Camera.Size bestPictureSize = determineBestPictureSize(parameters);

        parameters.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
        parameters.setPictureSize(bestPictureSize.width, bestPictureSize.height);


        // Set continuous picture focus, if it's supported
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        parameters.setFlashMode(mFlashMode);

        // Lock in the changes
        mCamera.setParameters(parameters);
    }

    private Camera.Size determineBestPreviewSize(Camera.Parameters parameters) {
        return determineBestSize(parameters.getSupportedPreviewSizes(), PREVIEW_SIZE_MAX_WIDTH);
    }

    private Camera.Size determineBestPictureSize(Camera.Parameters parameters) {
        return determineBestSize(parameters.getSupportedPictureSizes(), PICTURE_SIZE_MAX_WIDTH);
    }

    private Camera.Size determineBestSize(List<Camera.Size> sizes, int widthThreshold) {
       /* Camera.Size bestSize = null;
        Camera.Size size;
        int numOfSizes = sizes.size();
        for (int i = 0; i < numOfSizes; i++) {
            size = sizes.get(i);
            boolean isDesireRatio = (size.width / 4) == (size.height / 3);
            boolean isBetterSize = (bestSize == null) || size.width > bestSize.width;

            if (isDesireRatio && isBetterSize) {
                bestSize = size;
            }
        }

        if (bestSize == null) {
            Log.d("HS", "cannot find the best camera size");
            return sizes.get(sizes.size() - 1);
        }
*/
        Camera.Size result = null;
        Collections.sort(sizes, Collections.reverseOrder(new SizeComparator()));
        for (Camera.Size entry : sizes) {
            if (entry.height == 1080 || entry.width == 1920) {
                result = entry;
            } else {
                continue;
            }
        }

        if (result == null) {
            result = sizes.get(0);
        }

        return result;
    }

    private int getBackCameraID() {
        return Camera.CameraInfo.CAMERA_FACING_BACK;
    }

    /**
     * Take a picture
     */
    private void takePicture() {

        if (mIsSafeToTakePhoto) {
            setSafeToTakePhoto(false);

            mOrientationListener.rememberOrientation();

            // Shutter callback occurs after the image is captured. This can
            // be used to trigger a sound to let the user know that image is taken
            Camera.ShutterCallback shutterCallback = null;

            // Raw callback occurs when the raw image data is available
            Camera.PictureCallback raw = null;

            // postView callback occurs when a scaled, fully processed
            // postView image is available.
            Camera.PictureCallback postView = null;
            // jpeg callback occurs when the compressed image is available
            mCamera.takePicture(shutterCallback, raw, postView, this);

        }
    }


    private void setSafeToTakePhoto(final boolean isSafeToTakePhoto) {
        mIsSafeToTakePhoto = isSafeToTakePhoto;
    }

    private void setCameraFocusReady(final boolean isFocusReady) {
        if (this.mPreviewView != null) {
            mPreviewView.setIsFocusReady(isFocusReady);
        }
    }

    private static class CameraOrientationListener extends OrientationEventListener {

        private int mCurrentNormalizedOrientation;
        private int mRememberedNormalOrientation;

        public CameraOrientationListener(Context context) {
            super(context, SensorManager.SENSOR_DELAY_NORMAL);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            Log.i("HS","onOrientationChanged : " + orientation);
            if (orientation != ORIENTATION_UNKNOWN) {
                mCurrentNormalizedOrientation = normalize(orientation);
            }
        }

        /**
         * @param degrees Amount of clockwise rotation from the device's natural position
         * @return Normalized degrees to just 0, 90, 180, 270
         */
        private int normalize(int degrees) {
            if (degrees > 315 || degrees <= 45) {
                return 0;
            }

            if (degrees > 45 && degrees <= 135) {
                return 90;
            }

            if (degrees > 135 && degrees <= 225) {
                return 180;
            }

            if (degrees > 225 && degrees <= 315) {
                return 270;
            }

            throw new RuntimeException("The physics as we know them are no more. Watch out for anomalies.");
        }

        public void rememberOrientation() {
            mRememberedNormalOrientation = mCurrentNormalizedOrientation;
        }

        public int getRememberedNormalOrientation() {
            rememberOrientation();
            return mRememberedNormalOrientation;
        }
    }

    private class SizeComparator implements Comparator<Camera.Size> {
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

    private boolean turnGPSOn() {
        String gps = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!(gps.matches(".*gps.*") && gps.matches(".*network.*"))) {
            // GPS OFF 일때 Dialog 표시
            AlertDialog.Builder gsDialog = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog);
            gsDialog.setTitle("위치 서비스 설정");
            gsDialog.setMessage("위치 서비스 기능을 설정하셔야 서비스가 가능합니다.\n위치 서비스 기능을 설정하시겠습니까?");
            // Dialog 뒤로가기 막기
            gsDialog.setCancelable(false);
            gsDialog.setPositiveButton("네", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // GPS설정 화면으로 이동
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivity(intent);
                    isGPSEnabled = true;
                }
            })
                    .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(),"GPS를 켜고 다시 시도해 주시기 바랍니다.",Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }).create().show();
            return false;
        } else {
            return true;
        }
    }

}