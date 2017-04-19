package com.namoo.plus.jejurizmapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
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

    private StoreDetailModel mStore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);

        ButterKnife.bind(this);
        StoreModel simStore = getIntent().getParcelableExtra("data");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(simStore.getName());

        // 중현 인터셉트! -> bulkActivity로 넘김(보여주기)
        if(simStore.getName().equals("가락국수(粗面条)")){
            Intent i = new Intent(StoreDetailActivity.this, BulkActivity.class);
            i.putExtra("name", simStore.getName());
            startActivity(i);
            finish();
        } else if(simStore.getName().equals("대관령아구찜조개구이(大关岭辣炖安康鱼烤蛤蜊)")){
            Intent i = new Intent(StoreDetailActivity.this, BulkActivity.class);
            i.putExtra("name", simStore.getName());
            startActivity(i);
            finish();
        } else if(simStore.getName().equals("제원칼집")) {
            Intent i = new Intent(StoreDetailActivity.this, BulkActivity.class);
            i.putExtra("name", simStore.getName());
            startActivity(i);
            finish();
        } else if(simStore.getName().equals("냄비에퐁닭（乓锅鸡）")) {
            Intent i = new Intent(StoreDetailActivity.this, BulkActivity.class);
            i.putExtra("name", simStore.getName());
            startActivity(i);
            finish();
        } else if(simStore.getName().equals("로당(炉堂)(本店)")) {
            Intent i = new Intent(StoreDetailActivity.this, BulkActivity.class);
            i.putExtra("name", simStore.getName());
            startActivity(i);
            finish();
        }

        getImageDetail(simStore);
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

}
