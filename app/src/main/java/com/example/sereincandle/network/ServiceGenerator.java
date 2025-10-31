// File: ServiceGenerator.java (Cập nhật - Thêm logic Interceptor)
package com.example.sereincandle.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.sereincandle.UnsafeOkHttpClient;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static Context applicationContext; // Cần Context để đọc SharedPreferences

    // Khởi tạo context
    public static void init(Context context) {
        applicationContext = context.getApplicationContext();
    }

    private static final String BASE_URL = "https://10.0.2.2:7274/api/";

    // 1. Tạo Interceptor để thêm Token JWT
    private static Interceptor authInterceptor = chain -> {
        Request original = chain.request();
        Request.Builder requestBuilder = original.newBuilder();

        // Chỉ thêm Token nếu đã có Context và Token được lưu
        if (applicationContext != null) {
            SharedPreferences sharedPref = applicationContext.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            String token = sharedPref.getString("JWT_TOKEN", null);

            if (token != null) {
                // Thêm Header Authorization: Bearer <token>
                requestBuilder.header("Authorization", "Bearer " + token);
            }
        }

        Request request = requestBuilder.method(original.method(), original.body()).build();
        return chain.proceed(request);
    };

    // 2. Lấy OkHttpClient KHÔNG AN TOÀN và Thêm Interceptor
    private static OkHttpClient unsafeClient = UnsafeOkHttpClient.getUnsafeOkHttpClient()
            .newBuilder()
            .addInterceptor(authInterceptor) // Thêm Interceptor
            .build();

    // 3. Khởi tạo Retrofit (giữ nguyên)
    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder
            .client(unsafeClient)
            .build();

    public static <S> S createService(Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }
}