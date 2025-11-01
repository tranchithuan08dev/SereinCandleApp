package com.example.sereincandle;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sereincandle.models.ApiResponse;
import com.example.sereincandle.network.ApiService;
import com.example.sereincandle.network.ServiceGenerator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidx.core.content.ContextCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPhone, etDateOfBirth, etPassword, etConfirmPassword;
    private Button btnRegister, btnBackToLogin;
    private TextView tvMessage;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khởi tạo
        calendar = Calendar.getInstance();
        
        // Ánh xạ views
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);
        tvMessage = findViewById(R.id.tvMessage);

        // Date picker cho DateOfBirth
        etDateOfBirth.setOnClickListener(v -> showDatePicker());

        // Nút Đăng ký
        btnRegister.setOnClickListener(v -> performRegister());

        // Nút quay lại đăng nhập
        btnBackToLogin.setOnClickListener(v -> {
            finish(); // Quay lại MainActivity
        });
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                
                @SuppressWarnings("deprecation")
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                etDateOfBirth.setText(sdf.format(calendar.getTime()));
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        // Set max date là hôm nay
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void performRegister() {
        // Lấy dữ liệu
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String dateOfBirth = etDateOfBirth.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validation
        if (fullName.isEmpty()) {
            tvMessage.setText("Vui lòng nhập họ và tên");
            return;
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tvMessage.setText("Vui lòng nhập email hợp lệ");
            return;
        }

        if (phone.isEmpty()) {
            tvMessage.setText("Vui lòng nhập số điện thoại");
            return;
        }

        if (dateOfBirth.isEmpty()) {
            tvMessage.setText("Vui lòng chọn ngày sinh");
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            tvMessage.setText("Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }

        if (!password.equals(confirmPassword)) {
            tvMessage.setText("Mật khẩu xác nhận không khớp");
            return;
        }

        // Tạo RegisterRequest
        RegisterRequest request = new RegisterRequest(email, password, fullName, phone, dateOfBirth);

        // Gọi API
        ApiService apiService = ServiceGenerator.createService(ApiService.class);
        Call<ApiResponse<Void>> call = apiService.register(request);

        tvMessage.setText("Đang đăng ký...");
        tvMessage.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        btnRegister.setEnabled(false);
        btnRegister.setText("Đang xử lý...");

        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                btnRegister.setEnabled(true);
                btnRegister.setText("ĐĂNG KÝ");

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        // Đăng ký thành công
                        tvMessage.setText("");
                        String successMessage = apiResponse.getMessage() != null ? 
                                apiResponse.getMessage() : "Đăng ký thành công! Vui lòng đăng nhập.";
                        Toast.makeText(RegisterActivity.this, successMessage, Toast.LENGTH_LONG).show();
                        
                        // Quay lại màn hình đăng nhập sau 1.5 giây
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            finish();
                        }, 1500);
                    } else {
                        // Backend trả về success=false
                        String errorMsg = apiResponse.getMessage() != null ? 
                                apiResponse.getMessage() : "Đăng ký thất bại";
                        tvMessage.setText(errorMsg);
                        tvMessage.setTextColor(ContextCompat.getColor(RegisterActivity.this, android.R.color.holo_red_dark));
                        Log.e("REGISTER_ERROR", "API returned success=false: " + errorMsg);
                    }
                } else {
                    // HTTP error (400, 500, etc.)
                    String errorMsg = "Đăng ký thất bại";
                    
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e("REGISTER_ERROR", "Error body: " + errorBody);
                            
                            // Thử parse ApiResponse từ errorBody
                            try {
                                com.google.gson.Gson gson = new com.google.gson.Gson();
                                ApiResponse<Void> errorResponse = gson.fromJson(errorBody, 
                                        new com.google.gson.reflect.TypeToken<ApiResponse<Void>>(){}.getType());
                                if (errorResponse != null && errorResponse.getMessage() != null) {
                                    errorMsg = errorResponse.getMessage();
                                }
                            } catch (Exception e) {
                                Log.e("REGISTER_ERROR", "Cannot parse error response as ApiResponse");
                            }
                        }
                        
                        // Xử lý theo status code
                        if (response.code() == 400) {
                            if (errorMsg.equals("Đăng ký thất bại")) {
                                errorMsg = "Email hoặc số điện thoại đã tồn tại";
                            }
                        } else if (response.code() == 500) {
                            if (errorMsg.equals("Đăng ký thất bại")) {
                                errorMsg = "Lỗi hệ thống. Vui lòng thử lại sau.";
                            }
                        }
                    } catch (Exception e) {
                        Log.e("REGISTER_ERROR", "Error reading error body: " + e.getMessage());
                    }
                    
                    tvMessage.setText(errorMsg);
                    tvMessage.setTextColor(ContextCompat.getColor(RegisterActivity.this, android.R.color.holo_red_dark));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                btnRegister.setEnabled(true);
                btnRegister.setText("ĐĂNG KÝ");
                String errorMsg = "Lỗi kết nối mạng";
                if (t.getMessage() != null) {
                    errorMsg += ": " + t.getMessage();
                }
                tvMessage.setText(errorMsg);
                tvMessage.setTextColor(ContextCompat.getColor(RegisterActivity.this, android.R.color.holo_red_dark));
                Log.e("NETWORK_ERROR", "Register error: " + t.getMessage());
            }
        });
    }
}
