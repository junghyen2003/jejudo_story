package com.namooplus.jejurizmandroid.ui;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.namooplus.jejurizmandroid.ExcelManager;
import com.namooplus.jejurizmandroid.R;
import com.namooplus.jejurizmandroid.common.AppSetting;
import com.namooplus.jejurizmandroid.common.PreferenceManager;
import com.namooplus.jejurizmandroid.common.Utils;
import com.namooplus.jejurizmandroid.model.ImageInfoModel;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by HeungSun-AndBut on 2016. 7. 12..
 */

public class ImageDetailActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    @BindView(R.id.app_tool_bar)
    public Toolbar toolbar;

    @BindView(R.id.activity_camera_image_view)
    public ImageView mIvImage;

    @BindView(R.id.activity_camera_image_edit)
    public EditText mEtEdit;

    @BindView(R.id.activity_camera_image_confirm)
    public Button mBtConfirm;

    @BindView(R.id.activity_camera_image_cancle)
    public Button mBtCancle;

    private ArrayList<ImageInfoModel> mImageList;

    private GoogleMap map;

    private Marker mMarker;
    private MarkerOptions mo;

    private MapFragment mapFragment;

    private double mCurrentLat;
    private double mCurrentLong;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera_image);

        mImageList = getIntent().getParcelableArrayListExtra("datas");

        if (mImageList.isEmpty()) {
            //TODO 토스트 메세지??????
            return;
        }

        ButterKnife.bind(this);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.activity_camera_image_map);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("GPS 확인 및 제목 입력");

        mCurrentLat = mImageList.get(0).getLatitude();
        mCurrentLong = mImageList.get(0).getLongitude();

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        mIvImage.setImageBitmap(BitmapFactory.decodeFile(mImageList.get(0).getFilePath(), options));

    }

    @OnClick(R.id.activity_camera_image_confirm)
    public void onClickConfirm() {
        //TODO 다이얼로그 띄우기
    }

    @OnClick(R.id.activity_camera_image_cancle)
    public void onClickCancle() {
        //TODO 다이얼로그 띄우기
    }


    @Override
    public void onMapLongClick(LatLng latLng) {
        if (map != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)             // Sets the center of the map to current location
                    .zoom(17)                   // Sets the zoom
                    .tilt(0)                   // Sets the tilt of the camera to 0 degrees
                    .build();                   // Creates a CameraPosition from the builder
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            if (mMarker != null) {
                mMarker.remove();
            }

            mo.position(latLng);
            mMarker = map.addMarker(mo);

            mCurrentLat = latLng.latitude;
            mCurrentLong = latLng.longitude;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            map = googleMap;
            map.setMyLocationEnabled(false);

            map.getUiSettings().setCompassEnabled(false);

            LatLng latlng = new LatLng(mCurrentLat, mCurrentLong);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17));

            mo = new MarkerOptions();
            mo.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher));
            mo.position(latlng);
            mMarker = map.addMarker(mo);

            map.setOnMapLongClickListener(this);
        } catch (SecurityException e) {
            Log.i("HS", "onMapReady " + e.getMessage());
        }
    }

    private void saveData() {

        try {

            String userTitle = mEtEdit.getText().toString();
            if (userTitle.isEmpty()) {
                Toast.makeText(this, "제목이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            int lastNum = PreferenceManager.getInstance(this).getLastImagePathNum();
            File saveDir = new File(AppSetting.SAVE_IMAGE_PATH + lastNum);

            Log.i("HS", "save dir : " + saveDir.getAbsolutePath());

            if (saveDir.exists()) {
                //TODO 혹시 꼬여서?? 존재한다면 안에 있는 파일까지 몽딱 삭제 한 후에 작업
            } else {
                saveDir.mkdir();
            }

            File excelFile = ExcelManager.getInstance().createExcelFile(userTitle + ".xls");

            if(excelFile == null) {
                Log.e("HS","엑셀 파일 생성 실패 ");
                return;
            }

            for (ImageInfoModel item : mImageList) {
                //수정된 위치값 주입
                item.setLatitude(mCurrentLat);
                item.setLongitude(mCurrentLong);

                String finalPath = saveDir.getAbsolutePath() + userTitle
                        + "(" + String.valueOf(mImageList.indexOf(item)) + ")";

                Log.i("HS", "최종 목적지 위치 : " + finalPath);
                //원래 파일에서

                Utils.copyFile(new File(item.getFilePath()), new File(finalPath));

                //변경된 주소 저장
                item.setFilePath(finalPath);

                ExcelManager.getInstance().saveExcelFile(excelFile, userTitle, item.getFilePath(),
                        item.getLight(), item.getDirection(), item.getLatitude(), item.getLongitude());

            }

            //임시 이미지 파일 삭제
            File temDir = new File(AppSetting.SAVE_IMAGE_TEMP_PATH);
            Utils.deleteDir(temDir);


        } catch (Exception e) {

        }

    }


}
