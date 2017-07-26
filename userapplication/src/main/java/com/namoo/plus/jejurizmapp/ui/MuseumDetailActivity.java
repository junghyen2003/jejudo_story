package com.namoo.plus.jejurizmapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.namoo.plus.jejurizmapp.R;
import com.namoo.plus.jejurizmapp.common.Constants;
import com.namoo.plus.jejurizmapp.model.MuseumModel;
import com.namoo.plus.jejurizmapp.network.ServiceBuilder;
import com.namoo.plus.jejurizmapp.network.model.MuseumDetailResponse;
import com.namoo.plus.jejurizmapp.network.service.ImageService;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jungh on 2017-05-18.
 */

public class MuseumDetailActivity extends AppCompatActivity {
    @BindView(R.id.musieum_detail_imageView1)
    public ImageView musieum_detail_imageView1;
    @BindView(R.id.musieum_detail_imageView2)
    public ImageView musieum_detail_imageView2;
    @BindView(R.id.musieum_detail_imageView3)
    public ImageView musieum_detail_imageView3;
    @BindView(R.id.musieum_detail_summary)
    public TextView musieum_detail_summary;
    @BindView(R.id.musieum_detail_open)
    public TextView musieum_detail_open;
    @BindView(R.id.musieum_detail_close)
    public TextView musieum_detail_close;
    @BindView(R.id.musieum_detail_closing_day)
    public TextView musieum_detail_closing_day;
    @BindView(R.id.musieum_detail_hasWiFi)
    public TextView musieum_detail_hasWiFi;
    @BindView(R.id.musieum_detail_toilet)
    public TextView musieum_detail_toilet;
    @BindView(R.id.musieum_detail_fee)
    public TextView musieum_detail_fee;

    MuseumModel mMuseum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum_detail);

        ButterKnife.bind(this);
        MuseumModel simMuseum = getIntent().getParcelableExtra("data");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(simMuseum.getName());

        getMuseumDetail(simMuseum);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(MuseumDetailActivity.this, MainActivity.class);
        startActivity(i);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getMuseumDetail(MuseumModel simMuseum) {
        ImageService imageService = ServiceBuilder.createService(ImageService.class, Constants.NAMOO_PLUS_BASE_URL);

        imageService.getMuseumForId(simMuseum.getId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<MuseumDetailResponse>>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("HS", "onError :" + e.getMessage());
                    }

                    @Override
                    public void onNext(Response<MuseumDetailResponse> response) {
                        if (response.isSuccessful()) {
                            if (response.code() == 200) {
                                mMuseum = response.body().getData();
                                if (mMuseum != null) {
                                    Glide.with(MuseumDetailActivity.this)
                                            .load(mMuseum.getImages().get(0).toString())
                                            .into(musieum_detail_imageView1);
                                    Glide.with(MuseumDetailActivity.this)
                                            .load(mMuseum.getImages().get(1).toString())
                                            .into(musieum_detail_imageView2);
                                    Glide.with(MuseumDetailActivity.this)
                                            .load(mMuseum.getImages().get(2).toString())
                                            .into(musieum_detail_imageView3);

                                    musieum_detail_summary.setText(mMuseum.getSummary());
                                    musieum_detail_fee.append(mMuseum.getFee());

                                    String operatingTime = mMuseum.getOperatingTime();
                                    String[] result = operatingTime.split("\n");
                                    String[] time = result[0].split(" - ");

                                    musieum_detail_open.setText(time[0]);
                                    musieum_detail_close.setText(time[1]);
                                    if(result.length >= 2)
                                    {
                                        musieum_detail_closing_day.setText(result[1]);
                                    }
                                    else{
                                        musieum_detail_closing_day.setText("年中無休");
                                    }
                                    // 이미지, 텍스트 설정 부분(식당정보)
                                    if(mMuseum.getHasWifi()){
                                        musieum_detail_hasWiFi.setText("有");
                                    }
                                    else{
                                        musieum_detail_hasWiFi.setText("無");
                                    }
                                    if(mMuseum.getToilet()){
                                        musieum_detail_toilet.setText("有");
                                    }
                                    else{
                                        musieum_detail_toilet.setText("無");
                                    }

                                }
                            } else if (response.code() == 204) {
                                Toast.makeText(MuseumDetailActivity.this, R.string.activity_compare_no_data, Toast.LENGTH_SHORT).show();
                            } else {
                                Log.i("HS", "error : " + response.code() + ":" + response.message());
                                Toast.makeText(MuseumDetailActivity.this, R.string.activity_compare_network_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.i("HS", "error : " + response.code() + ":" + response.message());
                            Toast.makeText(MuseumDetailActivity.this, R.string.activity_compare_network_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
