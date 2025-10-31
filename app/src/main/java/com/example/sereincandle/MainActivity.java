package com.example.sereincandle; // Đảm bảo đúng tên package của bạn

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences; // Cần import SharedPreferences
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sereincandle.network.ApiService;
import com.example.sereincandle.network.ServiceGenerator; // Cần import ServiceGenerator
import com.example.sereincandle.LoginRequest; // Đảm bảo import model LoginRequest
import com.example.sereincandle.LoginResponse; // Đảm bảo import model LoginResponse

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// LƯU Ý: Đã loại bỏ các import Retrofit/Gson/OkHttpClient không cần thiết
// vì chúng ta sẽ sử dụng ServiceGenerator

public class MainActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 🔑 BƯỚC QUAN TRỌNG: Khởi tạo ServiceGenerator với Context của ứng dụng
        // Điều này cho phép ServiceGenerator đọc Token từ SharedPreferences
        ServiceGenerator.init(getApplicationContext());

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
     * Phương thức xử lý việc lấy dữ liệu và gọi API, sử dụng ServiceGenerator.
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
        LoginRequest loginRequest = new LoginRequest(email, password);

        // 4. Khởi tạo ApiService thông qua ServiceGenerator
        // ServiceGenerator tự động tạo Retrofit và OkHttpClient (Unsafe)
        ApiService apiService = ServiceGenerator.createService(ApiService.class);

        // 5. Thực hiện cuộc gọi API
        Call<LoginResponse> call = apiService.login(loginRequest);

        // Hiển thị thông báo đang xử lý
        tvMessage.setText("Đang đăng nhập...");

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                // Kiểm tra mã trạng thái HTTP (ví dụ: 200 OK, 401 Unauthorized,...)
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    // Giả sử hàm lấy Token là getToken() hoặc getAccessToken()
                    // Dựa trên mã trước đó, tôi dùng getToken()
                    String token = loginResponse.getToken();

                    if (token != null && !token.isEmpty()) {
                        // **THÀNH CÔNG:** Lưu Token JWT vào SharedPreferences
                        SharedPreferences sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("JWT_TOKEN", token);
                        editor.apply();

                        tvMessage.setText(""); // Xóa thông báo

                        // Chuyển sang Activity Trang chủ
                        Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
                        startActivity(intent);

                        // Kết thúc màn hình đăng nhập
                        finish();
                    } else {
                        // THẤT BẠI: Phản hồi 200 nhưng không có token (ví dụ: lỗi nghiệp vụ)
                        tvMessage.setText("Tên đăng nhập hoặc mật khẩu không đúng.");
                    }

                } else {
                    // Đăng nhập THẤT BẠI (Mã 4xx, 5xx,...)
                    try {
                        String errorBody = response.errorBody().string();
                        tvMessage.setText("Đăng nhập thất bại. Chi tiết: " + errorBody);
                        Log.e("API_ERROR", "Error body: " + errorBody);
                    } catch (Exception e) {
                        tvMessage.setText("Đăng nhập thất bại. Mã lỗi: " + response.code());
                        Log.e("API_ERROR", "Error reading body: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Xảy ra lỗi kết nối mạng (SSL/Network)
                tvMessage.setText("Lỗi kết nối mạng: " + t.getMessage());
                Log.e("NETWORK_ERROR", "Lỗi: " + t.getMessage());
            }
        });
    }
}