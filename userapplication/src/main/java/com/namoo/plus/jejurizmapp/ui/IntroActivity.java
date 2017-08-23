package com.namoo.plus.jejurizmapp.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.namoo.plus.jejurizmapp.R;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;

import static android.os.Build.VERSION_CODES.KITKAT;
import static android.os.Build.VERSION_CODES.M;

public class IntroActivity extends AppCompatActivity {
    public final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    Timer mTimer = new Timer();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        ButterKnife.bind(this);

        permission_check();
    }

    TimerTask task = new TimerTask() {
        public void run() {
            try {
                Intent i = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(i);
                finish();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    //권한 사용 체크
    public void permission_check() {
        if (Build.VERSION.SDK_INT >= KITKAT) {
            // 권한이 없을 경우
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    ||  ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        ||ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // 사용자가 임의로 권한을 취소시킨 경우
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CAMERA);
                } else {
                    // 최초로 권한을 요청하는 경우 (첫 실행)
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CAMERA);
                }
            } else {
                // 사용 권한이 모두 있을 경우
                mTimer.schedule(task, 2000);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(IntroActivity.this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (!(grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(IntroActivity.this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (!(grantResults.length > 0 && grantResults[2] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(IntroActivity.this, "파일 저장 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    mTimer.schedule(task, 2000);
                }
                return;
            }
        }
    }
}

