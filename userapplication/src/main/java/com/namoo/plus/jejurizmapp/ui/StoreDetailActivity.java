package com.namoo.plus.jejurizmapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.namoo.plus.jejurizmapp.R;
import com.namoo.plus.jejurizmapp.common.Constants;
import com.namoo.plus.jejurizmapp.model.MenuModel;
import com.namoo.plus.jejurizmapp.model.StoreDetailModel;
import com.namoo.plus.jejurizmapp.model.StoreMenuModel;
import com.namoo.plus.jejurizmapp.model.StoreModel;
import com.namoo.plus.jejurizmapp.network.ServiceBuilder;
import com.namoo.plus.jejurizmapp.network.model.StoreDetailResponse;
import com.namoo.plus.jejurizmapp.network.model.StoreMenuResponse;
import com.namoo.plus.jejurizmapp.network.service.ImageService;
import com.namoo.plus.jejurizmapp.ui.listmodel.ListMenuAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by HeungSun-AndBut on 2016. 7. 29..
 */

public class StoreDetailActivity extends AppCompatActivity {

    @BindView(R.id.activity_store_detail_progressbar)
    public ProgressBar mProgressBar;
    @BindView(R.id.activity_store_detail_main_image)
    public ImageView mIvMainImage;
    @BindView(R.id.activity_store_detail_content)
    public TextView mTxContent;
    @BindView(R.id.store_open)
    public TextView mStore_open;
    @BindView(R.id.store_close)
    public TextView mStore_close;
    @BindView(R.id.store_closing_day)
    public TextView mStore_closing_day;
    @BindView(R.id.store_detail_listView)
    public ListView mStore_listView;

    private StoreDetailModel mStore;
    private StoreMenuModel mStoreMenuModel;
    private ArrayList<MenuModel> mMenuModel;
    private ListMenuAdapter mListMenuAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);

        ButterKnife.bind(this);
        StoreModel simStore = getIntent().getParcelableExtra("data");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(simStore.getName());

        getImageDetail(simStore);
        getMenuDetail(simStore);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(StoreDetailActivity.this, MainActivity.class);
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

    private void getImageDetail(StoreModel simStore) {
        ImageService imageService = ServiceBuilder.createService(ImageService.class, Constants.NAMOO_PLUS_BASE_URL);

        imageService.getStoreForId(simStore.getId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<StoreDetailResponse>>() {

                    @Override
                    public void onCompleted() {
                        mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("HS", "onError :" + e.getMessage());
                    }

                    @Override
                    public void onNext(Response<StoreDetailResponse> response) {
                        if (response.isSuccessful()) {
                            if (response.code() == 200) {
                                mStore = response.body().getData();
                                if (mStore != null) {
                                    Glide.with(StoreDetailActivity.this)
                                            .load(mStore.getMainImage())
                                            .into(mIvMainImage);
                                    mTxContent.setText(mStore.getSummary());

                                    String operatingTime = mStore.getOperatingTime();
                                    String[] result = operatingTime.split("\n");
                                    String[] time = result[0].split(" - ");

                                    mStore_open.setText(time[0]);
                                    mStore_close.setText(time[1]);
                                    if(result.length >= 2)
                                    {
                                        mStore_closing_day.setText(result[1]);
                                    }
                                    else{
                                        mStore_closing_day.setText("年中無休");
                                    }
                                    // 이미지, 텍스트 설정 부분(식당정보)
                                }
                            } else if (response.code() == 204) {
                                Toast.makeText(StoreDetailActivity.this, R.string.activity_compare_no_data, Toast.LENGTH_SHORT).show();
                            } else {
                                Log.i("HS", "error : " + response.code() + ":" + response.message());
                                Toast.makeText(StoreDetailActivity.this, R.string.activity_compare_network_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.i("HS", "error : " + response.code() + ":" + response.message());
                            Toast.makeText(StoreDetailActivity.this, R.string.activity_compare_network_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getMenuDetail(StoreModel simStore) {
        ImageService imageService = ServiceBuilder.createService(ImageService.class, Constants.NAMOO_PLUS_BASE_URL);

        imageService.getStoreMenuForId(simStore.getId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<StoreMenuResponse>>() {

                    @Override
                    public void onCompleted() {
                        mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("HS", "onError :" + e.getMessage());
                    }

                    @Override
                    public void onNext(Response<StoreMenuResponse> response) {
                        if (response.isSuccessful()) {
                            if (response.code() == 200) {
                                // 메뉴 이미지, 텍스트 설정 부분
                                mMenuModel = response.body().getData();
                                if (mMenuModel != null) {
                                    // mMenuModel => data 리스트를 가져옴 => 각각의 메뉴들이 담긴 모델
                                    mListMenuAdapter = new ListMenuAdapter(StoreDetailActivity.this, mMenuModel);
                                    mStore_listView.setAdapter(mListMenuAdapter);
                                    mListMenuAdapter.notifyDataSetChanged();
                                }
                            } else if (response.code() == 204) {
                                Toast.makeText(StoreDetailActivity.this, "가게 정보가 존재하지 않습니다...", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.i("HS", "error : " + response.code() + ":" + response.message());
                                Toast.makeText(StoreDetailActivity.this, R.string.activity_compare_network_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.i("HS", "error : " + response.code() + ":" + response.message());
                            Toast.makeText(StoreDetailActivity.this, R.string.activity_compare_network_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
