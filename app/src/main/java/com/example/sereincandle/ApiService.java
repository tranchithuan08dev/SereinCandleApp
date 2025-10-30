package com.example.sereincandle;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    // Đảm bảo đường dẫn này khớp với BASE_URL + "/Auth/login"
    @POST("Auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
}