package com.example.sereincandle.network;

import com.example.sereincandle.UnsafeOkHttpClient;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    // 💡 Đảm bảo thay thế bằng IP thực tế của bạn
    private static final String BASE_URL = "https://10.0.2.2:7274/api/";

    // 1. Lấy OkHttpClient KHÔNG AN TOÀN
    private static OkHttpClient unsafeClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();

    // 2. Sử dụng client này để khởi tạo Retrofit
    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder
            // 💥 Kích hoạt client đã bỏ qua xác thực chứng chỉ
            .client(unsafeClient)
            .build();

    // Phương thức để tạo ra các Service API (Interface)
    public static <S> S createService(Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }
}