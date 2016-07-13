package com.namooplus.jejurizmandroid.view;

import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.namooplus.jejurizmandroid.R;
import com.namooplus.jejurizmandroid.common.PreferenceManager;

import java.io.File;
import java.io.FileOutputStream;

import static com.namooplus.jejurizmandroid.common.AppSetting.IMAGE_SAMPLE_SIZE;
import static com.namooplus.jejurizmandroid.common.AppSetting.SAVE_IMAGE_PATH;


/**
 * Created by HeungSun-AndBut on 2016. 6. 19..
 */

public class AfterTakenDailog extends DialogFragment {

    private EditText mEtTitle;
    private TextView mTxBright;
    private TextView mTxCompass;
    private TextView mTxLongitute;
    private TextView mTxLatitude;
    private Button mBtnOk;
    private Button mBtnCancle;
    private ImageView mIvMain;
    private MapView mMapView;

    private String mTitle;
    private Bitmap mBit;
    private float mBright;
    private float mCompass;
    private double mLat;
    private double mLon;

    PreferenceManager mPreferenceManager;

    public static AfterTakenDailog newInstance(Bitmap bit, float bright, float compass, double lat, double lon) {

        AfterTakenDailog f = new AfterTakenDailog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putParcelable("bit", bit);
        args.putFloat("bright", bright);
        args.putFloat("compass", compass);
        args.putDouble("lat", lat);
        args.putDouble("lon", lon);

        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBit = getArguments().getParcelable("bit");
        mBright = getArguments().getFloat("bright");
        mCompass = getArguments().getFloat("compass");
        mLat = getArguments().getDouble("lat");
        mLon = getArguments().getDouble("lon");

        mPreferenceManager = new PreferenceManager(getContext());
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_after_taken, container);

        getDialog().setTitle("최종 확인");

        mIvMain = (ImageView) view.findViewById(R.id.dialog_after_taken_image);
        mEtTitle = (EditText) view.findViewById(R.id.dialog_after_taken_title);
        mTxBright = (TextView) view.findViewById(R.id.dialog_after_taken_bright);
        mTxCompass = (TextView) view.findViewById(R.id.dialog_after_taken_compass);
        mTxLongitute = (TextView) view.findViewById(R.id.dialog_after_taken_longitute);
        mTxLatitude = (TextView) view.findViewById(R.id.dialog_after_taken_latitude);
        mMapView = (MapView) view.findViewById(R.id.dialog_after_taken_mapview);
        mMapView.onCreate(savedInstanceState);

        mTxBright.setText("조도 : " + mBright);
        mTxCompass.setText("방향 : " + mCompass);
        mTxLongitute.setText("경도 : " + mLon);
        mTxLatitude.setText("위도 : " + mLat);

        mEtTitle.setText(mPreferenceManager.getImageTitle());

        mBtnOk = (Button) view.findViewById(R.id.dialog_after_taken_ok);
        mBtnCancle = (Button) view.findViewById(R.id.dialog_after_taken_cancle);

        int width = mBit.getWidth() / IMAGE_SAMPLE_SIZE;
        int height = mBit.getHeight() / IMAGE_SAMPLE_SIZE;

        mBit = Bitmap.createScaledBitmap(mBit, width, height, true);

        mIvMain.setImageBitmap(mBit);

        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTitle = mEtTitle.getText().toString();
                if (mTitle.isEmpty()) {
                    Toast.makeText(getActivity(), R.string.dialog_after_taken_dialog_need_title, Toast.LENGTH_SHORT).show();
                } else {
                    saveData();
                }
            }
        });

        mBtnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBit != null) {
                    mBit.recycle();
                }
                dismiss();
            }
        });

        mapSetting(width, height);
        return view;
    }

    private void mapSetting(int width, int height) {

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mMapView.getLayoutParams();
        params.height = height;
        params.width = width * 2;
        mMapView.setLayoutParams(params);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        GoogleMap googleMap = mMapView.getMap();

        googleMap.getUiSettings().setCompassEnabled(false);
        mMapView.setClickable(false);
        LatLng latLng = new LatLng(mLat, mLon);
        MarkerOptions marker = new MarkerOptions().position(latLng);

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        // adding marker
        googleMap.addMarker(marker);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .bearing(mCompass)
                .zoom(17)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


    }

    private void saveData() {
        FileOutputStream fos = null;
        try {
            File dir = new File(SAVE_IMAGE_PATH);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            final String imageFilename = System.currentTimeMillis() + "_" + mTitle + ".png";

            final File file = new File(SAVE_IMAGE_PATH + imageFilename);

            if (file.exists()) {
                file.delete();
            }

            fos = new FileOutputStream(file);

            mBit.compress(Bitmap.CompressFormat.PNG, 100, fos);

            fos.flush();

            //안드로이드 이미지 캐쉬에 등록
            MediaScannerConnection.scanFile(getActivity(), new String[]{file.getAbsolutePath()},
                    new String[]{"image/png"}, new MediaScannerConnection.MediaScannerConnectionClient() {
                        @Override
                        public void onMediaScannerConnected() {

                        }

                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), R.string.dialog_after_taken_dialog_save, Toast.LENGTH_SHORT).show();
                                    //엑셀에 저장
                                    //ExcelManager.getInstance().saveExcelFile(file.getAbsolutePath(), mTitle,
                                      //      mBright, mCompass, mLat, mLon);
                                    mPreferenceManager.saveImageTitle(mTitle);
                                    dismiss();
                                }
                            });

                        }
                    });

        } catch (final Exception e) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            });

        } finally {
            try {
                mBit.recycle();
                if (fos != null) {
                    fos.close();
                }
            } catch (final Exception e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });
            }
        }
    }
}
