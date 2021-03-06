package com.namoo.plus.jejurizmapp.network.service;

/**
 * Created by HeungSun-AndBut on 2016. 7. 28..
 */

import com.namoo.plus.jejurizmapp.network.model.MuseumDetailResponse;
import com.namoo.plus.jejurizmapp.network.model.MuseumListResponse;
import com.namoo.plus.jejurizmapp.network.model.StoreDetailResponse;
import com.namoo.plus.jejurizmapp.network.model.StoreListResponse;
import com.namoo.plus.jejurizmapp.network.model.StoreMenuResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface ImageService {

    @Multipart
    @POST("/api/v1/stores")
    Observable<Response<StoreListResponse>> getSearchImageData(
            @Query("more") String more,
            @Part("lat") RequestBody lat,
            @Part("lng") RequestBody lng,
            @Part("illum") RequestBody illum,
            @Part("magnet") RequestBody magnet,
            @Part("orient") RequestBody orient,
            @Part MultipartBody.Part image);

    @Multipart
    @POST("/api/v1/locations")
    Observable<Response<MuseumListResponse>> getMuseumSearchImageData(
            @Query("more") String more,
            @Part("lat") RequestBody lat,
            @Part("lng") RequestBody lng,
            @Part("illum") RequestBody illum,
            @Part("magnet") RequestBody magnet,
            @Part("orient") RequestBody orient,
            @Part MultipartBody.Part image);

    @Headers("Content-Type: application/json")
    @GET("/api/v1/stores/{id}")
    Observable<Response<StoreDetailResponse>> getStoreForId(
            @Path("id") int id);

    @Headers("Content-Type: application/json")
    @GET("/api/v1/stores/{id}/menus")
    Observable<Response<StoreMenuResponse>> getStoreMenuForId(
            @Path("id") int id);

    @Headers("Content-Type: application/json")
    @GET("/api/v1/locations/{id}")
    Observable<Response<MuseumDetailResponse>> getMuseumForId(
            @Path("id") int id);
}