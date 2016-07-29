package com.namoo.plus.jejurizmapp.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.namoo.plus.jejurizmapp.R;
import com.namoo.plus.jejurizmapp.common.Constants;
import com.namoo.plus.jejurizmapp.model.StoreDetailModel;
import com.namoo.plus.jejurizmapp.model.StoreModel;
import com.namoo.plus.jejurizmapp.network.ServiceBuilder;
import com.namoo.plus.jejurizmapp.network.model.StoreDetailResponse;
import com.namoo.plus.jejurizmapp.network.service.ImageService;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    private StoreDetailModel mStore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);

        ButterKnife.bind(this);
        StoreModel simStore = getIntent().getParcelableExtra("data");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mStore.getName());

        getImageDetail(simStore);
    }

    private void getImageDetail(StoreModel simStore) {
        ImageService imageService = ServiceBuilder.createService(ImageService.class, Constants.NAMOO_PLUS_BASE_URL);

        imageService.getStoreForId(simStore.getId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StoreDetailResponse>() {

                    @Override
                    public void onCompleted() {
                        Log.i("HS", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("HS", "onError :" + e.getMessage());
                    }

                    @Override
                    public void onNext(StoreDetailResponse storeDetailResponse) {
                        mStore = storeDetailResponse.getData();

                    }
                });
    }

    private void settingData() {
        if (mStore != null) {
            Glide.with(this)
                    .load(mStore.getMainImage())
                    .into(mIvMainImage);
            mTxContent.setText(mStore.getSummary());
        } else {
            Toast.makeText(this, "데이터 호출 실패 ", Toast.LENGTH_SHORT).show();
        }
        mProgressBar.setVisibility(View.GONE);
    }
}
