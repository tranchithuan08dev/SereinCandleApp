package com.example.sereincandle.network;

import com.example.sereincandle.LoginRequest;
import com.example.sereincandle.LoginResponse;
import com.example.sereincandle.models.ProductDetailResponse;
import com.example.sereincandle.models.ProductListResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Đảm bảo đường dẫn này khớp với BASE_URL + "/Auth/login"
    @POST("Auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("Product")
    Call<ProductListResponse> getProducts(
            @Query("pageNumber") int pageNumber,
            @Query("pageSize") int pageSize
    );
    @GET("Product/{id}")
    Call<ProductDetailResponse> getProductDetail(@Path("id") int productId);
}