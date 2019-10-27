package com.corptia.bringero.Remote.Retrofit;


import com.corptia.bringero.Remote.Retrofit.Image.UploadImageResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface WebServicesAPI {

    @Multipart
    @POST("upload")
    Call<UploadImageResponse> postImage(@Part MultipartBody.Part file, @Part("name") RequestBody requestBody);

}
