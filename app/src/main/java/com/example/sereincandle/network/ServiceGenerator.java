// File: ServiceGenerator.java (Cập nhật - Thêm logic Interceptor)
package com.example.sereincandle.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.sereincandle.UnsafeOkHttpClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    // 3. Tạo custom Date deserializer để parse nhiều format date
    private static JsonDeserializer<Date> dateDeserializer = (json, typeOfT, context) -> {
        try {
            String dateString = json.getAsString();
            if (dateString == null || dateString.isEmpty()) {
                return null;
            }
            
            // Thử parse với nhiều format khác nhau
            String[] dateFormats = {
                    "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS",  // 2025-11-01T07:10:56.8574998
                    "yyyy-MM-dd'T'HH:mm:ss.SSS",       // 2025-11-01T07:10:56.857
                    "yyyy-MM-dd'T'HH:mm:ss",           // 2025-11-01T07:10:56
                    "yyyy-MM-dd HH:mm:ss",             // 2025-11-01 07:10:56
                    "yyyy-MM-dd"                       // 2025-11-01
            };
            
            for (String format : dateFormats) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
                    return sdf.parse(dateString);
                } catch (ParseException e) {
                    // Tiếp tục thử format tiếp theo
                }
            }
            
            // Nếu không parse được, throw exception
            throw new JsonParseException("Unparseable date: " + dateString);
        } catch (Exception e) {
            throw new JsonParseException("Error parsing date", e);
        }
    };

    // 4. Tạo Gson với custom date deserializer
    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, dateDeserializer)
            .create();

    // 5. Khởi tạo Retrofit với custom Gson
    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson));

    private static Retrofit retrofit = builder
            .client(unsafeClient)
            .build();

    public static <S> S createService(Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }
}