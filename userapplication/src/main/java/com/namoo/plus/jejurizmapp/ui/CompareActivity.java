package com.namoo.plus.jejurizmapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.namoo.plus.jejurizmapp.R;
import com.namoo.plus.jejurizmapp.common.Constants;
import com.namoo.plus.jejurizmapp.common.Utils;
import com.namoo.plus.jejurizmapp.model.ImageInfoModel;
import com.namoo.plus.jejurizmapp.model.StoreModel;
import com.namoo.plus.jejurizmapp.network.ServiceBuilder;
import com.namoo.plus.jejurizmapp.network.model.StoreListResponse;
import com.namoo.plus.jejurizmapp.network.service.ImageService;
import com.namoo.plus.jejurizmapp.ui.view.BaseRecyclerViewAdapter;
import com.namoo.plus.jejurizmapp.ui.view.SpacesItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by HeungSun-AndBut on 2016. 7. 29..
 */
public class CompareActivity extends AppCompatActivity {

    @BindView(R.id.activity_compare_main_image)
    public ImageView mIvMainImage;

    @BindView(R.id.activity_compare_image_list)
    public RecyclerView mRvHorizonList;

    @BindView(R.id.activity_compare_progressbar)
    public ProgressBar mProgressBar;

    //@BindView(R.id.activity_compare_check)
    //public ImageView mIvNextCheck;

    private ImageAdapater mAdapter;
    private List<StoreModel> storeList = new ArrayList<>();
    private int mSelectedItem = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.activity_compare_title);

        ImageInfoModel imageInfoModel = getIntent().getParcelableExtra("data");
        mIvMainImage.setImageBitmap(Utils.decodeSampledBitmapFromResource(imageInfoModel.getFilePath(), 4));
        getSearchImage(imageInfoModel);

        setRecycler();
        //TODO The application may be doing too much work on its main thread. 어디서 나는거지!?!?

    }

    /*@OnClick(R.id.activity_compare_check)
    public void onClickNextCheck() {
        if (mSelectedItem == -1) {
            Toast.makeText(CompareActivity.this, R.string.activity_compare_no_selected_image, Toast.LENGTH_SHORT).show();
        } else {
            Intent i = new Intent(CompareActivity.this, StoreDetailActivity.class);
            i.putExtra("data", storeList.get(mSelectedItem));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
    }*/

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setRecycler() {
        LinearLayoutManager mLayoutManager_Linear = new LinearLayoutManager(this);
        mLayoutManager_Linear.setOrientation(LinearLayoutManager.HORIZONTAL);

        mRvHorizonList.setLayoutManager(mLayoutManager_Linear);
        mRvHorizonList.addItemDecoration(new SpacesItemDecoration(
                Utils.dpToPx(CompareActivity.this, 5), SpacesItemDecoration.TYPE_HORIZONTAL));
        mRvHorizonList.setHasFixedSize(true);

        mAdapter = new ImageAdapater(CompareActivity.this, storeList);
        mRvHorizonList.setAdapter(mAdapter);
    }


    public void getSearchImage(ImageInfoModel infoModel) {
        File imageFile = new File(infoModel.getFilePath());

        ImageService imageService = ServiceBuilder.createService(ImageService.class, Constants.NAMOO_PLUS_BASE_URL);
        //이미지 파일 멀티 파트형 데이터로 변환
        RequestBody requestImage = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", imageFile.getName(), requestImage);

        RequestBody lat = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(infoModel.getLatitude()));
        RequestBody lng = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(infoModel.getLongitude()));
        RequestBody light = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(infoModel.getLight()));
        RequestBody compass = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(infoModel.getDirection()));
        RequestBody ori = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(infoModel.getOrientation()));

        imageService.getSearchImageData(lat, lng, light, compass, ori, body)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<StoreListResponse>>() {
                    @Override
                    public void onNext(Response<StoreListResponse> response) {
                        if (response.isSuccessful()) {
                            if (response.code() == 200) {
                                //storeList = response.body().getData();
                                mAdapter.addItems(response.body().getData());
                                //mAdapter.notifyDataSetChanged();
                            } else if (response.code() == 204) {
                                Toast.makeText(CompareActivity.this, R.string.activity_compare_no_data, Toast.LENGTH_SHORT).show();
                            } else {
                                Log.i("HS", "error : " + response.code() + ":" + response.message());
                                Toast.makeText(CompareActivity.this, R.string.activity_compare_network_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.i("HS", "error : " + response.code() + ":" + response.message());
                            Toast.makeText(CompareActivity.this, R.string.activity_compare_network_error, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCompleted() {
                        mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("HS", "error : " + e.getMessage());
                        Toast.makeText(CompareActivity.this, R.string.activity_compare_network_error, Toast.LENGTH_SHORT).show();
                    }

                });
    }

    public class ImageAdapater extends BaseRecyclerViewAdapter<StoreModel, ImageAdapater.SelectedPhotoHolder> {


        public ImageAdapater(Activity activity, List<StoreModel> mStore) {
            super(activity, mStore);
        }

        @Override
        public void onBindView(SelectedPhotoHolder holder, final int position) {

            StoreModel store = getItem(position);

            Glide.with(getContext())
                    .load(store.getMainImage())
                    .override(100, 100)
                    .dontAnimate()
                    .centerCrop()
                    .into(holder.mImage);

        }

        @Override
        public SelectedPhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater mInflater = LayoutInflater.from(getContext());
            View view = mInflater.inflate(R.layout.listitem_recycle_list, parent, false);
            return new SelectedPhotoHolder(view);
        }


        class SelectedPhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            ImageView mImage;
            CheckBox mCheckBox;

            public SelectedPhotoHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                mImage = (ImageView) itemView.findViewById(R.id.listitem_recycle_image);
                //mCheckBox = (CheckBox) itemView.findViewById(R.id.listitem_recycle_checkbox);

                /*mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (isChecked) {
                            mSelectedItem = getAdapterPosition();
                        } else {
                            mSelectedItem = -1;
                        }
                    }
                });*/

            }

            @Override
            public void onClick(View view) {
                mSelectedItem = getAdapterPosition();
                Intent i = new Intent(CompareActivity.this, StoreDetailActivity.class);
                i.putExtra("data", storeList.get(mSelectedItem));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                /*if (mCheckBox.isChecked()) {
                    mSelectedItem = -1;
                    mCheckBox.setChecked(false);
                } else {
                    mSelectedItem = getAdapterPosition();
                    mCheckBox.setChecked(true);
                }*/
            }
        }
    }
}
