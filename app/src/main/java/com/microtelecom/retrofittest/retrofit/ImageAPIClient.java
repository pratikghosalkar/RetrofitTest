package com.microtelecom.retrofittest.retrofit;

import com.microtelecom.retrofittest.Model.PixaBayModel;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by pratik on 11/10/2017.
 */

public interface ImageAPIClient {

    @GET("/api")
    Call<PixaBayModel> getImagesByPage(@QueryMap Map<String, String> options);
}
