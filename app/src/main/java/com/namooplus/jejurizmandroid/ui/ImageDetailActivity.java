package com.namooplus.jejurizmandroid.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.namooplus.jejurizmandroid.common.Utils;
import com.namooplus.jejurizmandroid.model.ImageInfoModel;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.namooplus.jejurizmandroid.common.AppSetting.EXCEL_STRING_FORMAT;
import static com.namooplus.jejurizmandroid.common.AppSetting.IMAGE_SAMPLE_SIZE;
import static com.namooplus.jejurizmandroid.common.AppSetting.IMAGE_STRING_FORMAT;
import static com.namooplus.jejurizmandroid.common.AppSetting.NAMOO_STRING_SPLIT;
import static com.namooplus.jejurizmandroid.common.AppSetting.SAME_STORE_MIN_DISTANCE;

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

    private String userTitle;
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
        mapFragment.getMapAsync(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.activity_image_detail_title_bar));

        mCurrentLat = mImageList.get(0).getLatitude();
        mCurrentLong = mImageList.get(0).getLongitude();

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = IMAGE_SAMPLE_SIZE;
        mIvImage.setImageBitmap(BitmapFactory.decodeFile(mImageList.get(0).getFilePath(), options));

    }

    private void clearAndFinish() {
        Utils.deleteDir(new File(AppSetting.SAVE_IMAGE_TEMP_PATH));
        Intent i = new Intent(ImageDetailActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);

    }

    @OnClick(R.id.activity_camera_image_confirm)
    public void onClickConfirm() {
        AlertDialog.Builder db = new AlertDialog.Builder(this);
        db.setTitle(R.string.activity_image_detail_dialog_check)
                .setMessage(R.string.activity_image_detail_dialog_check_save)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        saveData();
                        Toast.makeText(ImageDetailActivity.this,
                                R.string.activity_image_detail_save_complete,Toast.LENGTH_SHORT).show();
                        clearAndFinish();

                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }


    @OnClick(R.id.activity_camera_image_cancle)
    public void onClickCancle() {
        AlertDialog.Builder db = new AlertDialog.Builder(this);
        db.setTitle(R.string.activity_image_detail_dialog_cancle)
                .setMessage(R.string.activity_image_detail_dialog_cancle_remove)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        clearAndFinish();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }


    @Override
    public void onMapLongClick(LatLng latLng) {
        if (map != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)             // Sets the center of the map to current location
                    .zoom(map.getCameraPosition().zoom)
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
            mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gps_point));
            mo.position(latlng);
            mMarker = map.addMarker(mo);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latlng)             // Sets the center of the map to current location
                    .zoom(17)                   // Sets the zoom
                    .tilt(0)                   // Sets the tilt of the camera to 0 degrees
                    .build();                   // Creates a CameraPosition from the builder
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            map.setOnMapLongClickListener(this);
        } catch (SecurityException e) {
        }
    }

    //디렉토리 규칙 기본 주소/[사용자가 정한 타이틀]_[넘버링]/[파일이름].[파일형식] , 파일에 _가 포함되면 안된다.
    private File nextDir() {
        File nextDir = null;
        try {
            File saveDir = new File(AppSetting.SAVE_SITE_PATH);
            //String nextDirName = saveDir + "/" + userTitle + "_0";
            String nextDirName = "";
            int sameTitleNum = 0;
            File[] files = saveDir.listFiles();

            //기존 디렉토리에 일치하는 장소가 있는지 찾기
            for (File file : files) {
                String[] dirName = file.getName().split(NAMOO_STRING_SPLIT);
                if (dirName[0].equals(userTitle)) {
                    String path = file + "/" + userTitle + EXCEL_STRING_FORMAT;

                    Location endPoint = ExcelManager.getInstance().readLatLanForExcel(path);
                    Location startPoint = new Location("start");
                    startPoint.setLongitude(mCurrentLong);
                    startPoint.setLatitude(mCurrentLat);

                    float dis = startPoint.distanceTo(endPoint);
                    //같은 이름을 가졌지만 다른 업체임
                    if (startPoint.distanceTo(endPoint) > SAME_STORE_MIN_DISTANCE) {
                        //이거 다음에 디렉토리를 만들어야 함으로 번호를 가져옴, 혹시 뒤에 일치 하는게 있을수도 있으니 더 뒤지기
                        sameTitleNum = Integer.valueOf(dirName[dirName.length - 1]);
                    } else {
                        //일치하는 장소를 찾음, 다른 디렉토리 뒤져볼 필요가 없으로 탈출
                        nextDirName = file.getAbsolutePath();
                        break;
                    }
                }
            }

            //일치하는 곳이 없다면 새로 생성 시켜줘야 함
            if (nextDirName.isEmpty()) {
                nextDirName = saveDir + "/" + userTitle + "_" + (sameTitleNum + 1);
            }

            nextDir = new File(nextDirName);
            if (!nextDir.exists()) {
                nextDir.mkdir();
            }

        } catch (Exception e) {

        }
        return nextDir;
    }

    private int nextImageNum(File siteDir) {
        int finalNum = 0;
        File[] files = siteDir.listFiles();
        if (files.length > 0) {
            for (File file : files) {
                if (file.getName().contains(IMAGE_STRING_FORMAT)) {
                    String[] name = file.getName().split("\\(");
                    String num = name[name.length - 1].split("\\)")[0];
                    int temp = Integer.valueOf(num);
                    if (finalNum < temp) {
                        finalNum = temp;
                    }
                }
            }
        }
        return finalNum;
    }

    private void saveData() {
        File excelFile = null;
        try {

            userTitle = mEtEdit.getText().toString();
            if (userTitle.isEmpty()) {
                Toast.makeText(this, getString(R.string.activity_image_detail_dialog_invalid_title),
                        Toast.LENGTH_SHORT).show();
            } else {
                //장소 저장 규칙에 따라서 해당하는 디렉토리 가져오기
                File siteDir = nextDir();
                File excelPath = new File(siteDir, userTitle + EXCEL_STRING_FORMAT);

                //있으면 가져오고 없으면 새로 만들어서 가져오기
                excelFile = ExcelManager.getInstance().getExcelFile(excelPath);

                //데이터 미세 조정하고 엑셀에 저장하기
                saveExcel(siteDir, excelFile);

            }
        } catch (Exception e) {

        }
    }

    private void saveExcel(File siteDir, File excelFile) {
        try {
            int nextNum = nextImageNum(siteDir);
            for (ImageInfoModel item : mImageList) {
                //수정된 위치값 주입
                item.setLatitude(mCurrentLat);
                item.setLongitude(mCurrentLong);

                String finalPath = siteDir.getAbsolutePath() + "/" + userTitle
                        + "(" + ++nextNum + ")" + IMAGE_STRING_FORMAT;

                //원래 파일에서 복사하기
                File finalImageFile = new File(finalPath);
                Utils.copyFile(new File(item.getFilePath()), finalImageFile);

                ExifInterface exifi = new ExifInterface(finalImageFile.getAbsolutePath());
                exifi.setAttribute(ExifInterface.TAG_GPS_LATITUDE, String.valueOf(mCurrentLat));
                exifi.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, String.valueOf(mCurrentLong));
                exifi.saveAttributes();

                //변경된 주소 저장
                item.setFilePath(finalPath);

                ExcelManager.getInstance().saveExcelFile(excelFile, item.getFilePath(), userTitle,
                        item.getLight(), item.getDirection(), item.getLatitude(), item.getLongitude(),
                        item.getOrientation());

            }
        } catch (Exception e) {

        }
    }


}
