package com.example.sereincandle;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

// 💡 CẦN THIẾT: Import các lớp hỗ trợ kết nối và bỏ qua SSL/TLS
import com.example.sereincandle.network.ApiService;

import okhttp3.OkHttpClient;



import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvMessage;

    // BASE_URL CHÍNH XÁC: Đã thêm /api/ và sử dụng HTTPS/IP 10.0.2.2 (cho Android Emulator)
    private static final String BASE_URL = "https://10.0.2.2:7274/api/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Ánh xạ các thành phần UI
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvMessage = findViewById(R.id.tvMessage);

        // 2. Thiết lập sự kiện lắng nghe cho nút Đăng nhập
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
    }

    /**
     * Phương thức xử lý việc lấy dữ liệu và gọi API, sử dụng UnsafeOkHttpClient
     */
    private void performLogin() {
        // Lấy dữ liệu từ các trường nhập liệu
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // 3. Kiểm tra dữ liệu đầu vào cơ bản
        if (email.isEmpty() || password.isEmpty()) {
            tvMessage.setText("Vui lòng nhập đầy đủ Email và Mật khẩu.");
            return;
        }

        // Khởi tạo đối tượng yêu cầu
        // LƯU Ý: Đảm bảo class LoginRequest tồn tại
        LoginRequest loginRequest = new LoginRequest(email, password);

        // 4. Khởi tạo Retrofit

        // 💥 BƯỚC QUAN TRỌNG: Lấy OkHttpClient đã bỏ qua kiểm tra chứng chỉ (Unsafe)
        OkHttpClient unsafeClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                // 💥 Áp dụng client không an toàn để bỏ qua lỗi chứng chỉ SSL/TLS
                .client(unsafeClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // LƯU Ý: Đảm bảo interface ApiService tồn tại
        ApiService apiService = retrofit.create(ApiService.class);

        // 5. Thực hiện cuộc gọi API
        // LƯU Ý: Đảm bảo class LoginResponse tồn tại
        Call<LoginResponse> call = apiService.login(loginRequest);

        // Hiển thị thông báo đang xử lý
        tvMessage.setText("Đang đăng nhập...");

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                // Kiểm tra mã trạng thái HTTP (ví dụ: 200 OK, 401 Unauthorized,...)
                if (response.isSuccessful() && response.body() != null) {
                    // Đăng nhập THÀNH CÔNG (Mã 2xx)
                    LoginResponse loginResponse = response.body();

                    // Lấy token thông qua hàm tiện ích getToken() trong LoginResponse
                    String token = loginResponse.getToken();

                    // Sử dụng isSuccess() hoặc kiểm tra Token
                    if (token != null && !token.isEmpty()) {
                        // **THÀNH CÔNG:** Lưu Token, chuyển sang màn hình khác, v.v.
                        tvMessage.setText(""); // Xóa thông báo lỗi

                        // LƯU Ý: Đảm bảo class HomePageActivity tồn tại
                        Intent intent = new Intent(MainActivity.this, HomePageActivity.class);

                        // 2. Tùy chọn: Gửi dữ liệu (ví dụ: token) sang Activity mới
                        intent.putExtra("AUTH_TOKEN", token);

                        // 3. Khởi chạy Activity mới
                        startActivity(intent);

                        // 4. Quan trọng: Kết thúc màn hình đăng nhập
                        finish();
                    } else if (loginResponse.getMessage() != null) {
                        // THẤT BẠI: Server trả về phản hồi thành công (200) nhưng có thông báo lỗi
                        // Đây là trường hợp ít gặp nhưng có thể xảy ra tùy vào thiết kế API
                        tvMessage.setText("Đăng nhập thất bại: " + loginResponse.getMessage());
                    }

                } else {
                    // Đăng nhập THẤT BẠI (Mã 4xx, 5xx,...)
                    try {
                        // Cố gắng đọc nội dung lỗi từ phản hồi
                        String errorBody = response.errorBody().string();
                        tvMessage.setText("Đăng nhập thất bại. Mã lỗi: " + response.code() + ". Chi tiết: " + errorBody);
                        Log.e("API_ERROR", "Error body: " + errorBody);
                    } catch (Exception e) {
                        tvMessage.setText("Đăng nhập thất bại. Mã lỗi: " + response.code());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Xảy ra lỗi kết nối (Không có internet, server không chạy, sai BASE_URL,...)
                tvMessage.setText("Lỗi kết nối mạng: " + t.getMessage());
                Log.e("NETWORK_ERROR", "Lỗi: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }
}