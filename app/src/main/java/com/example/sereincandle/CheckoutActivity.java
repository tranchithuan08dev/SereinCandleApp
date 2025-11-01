// File: CheckoutActivity.java
package com.example.sereincandle;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sereincandle.models.CartResponse;
import com.example.sereincandle.models.CartItem;
import com.example.sereincandle.models.OrderItemRequest;
import com.example.sereincandle.models.OrderRequest;
import com.example.sereincandle.network.ApiService;
import com.example.sereincandle.network.ServiceGenerator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {

    private EditText etFullName, etPhone, etAddressLine, etCity, etDistrict, etWard;
    private Button btnPlaceOrder;
    private TextView tvOrderSummary;
    private List<CartItem> currentCartItems = new ArrayList<>();
    private final DecimalFormat formatter = new DecimalFormat("#,### VND");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Setup nút Back
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // 1. Ánh xạ
        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etAddressLine = findViewById(R.id.etAddressLine);
        etCity = findViewById(R.id.etCity);
        etDistrict = findViewById(R.id.etDistrict);
        etWard = findViewById(R.id.etWard);
        tvOrderSummary = findViewById(R.id.tvOrderSummary);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        // 2. Tải Giỏ hàng để lấy danh sách sản phẩm (items)
        fetchCartAndDisplaySummary();

        // 3. Xử lý Đặt hàng
        btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    private void fetchCartAndDisplaySummary() {
        ApiService apiService = ServiceGenerator.createService(ApiService.class);
        Call<CartResponse> call = apiService.getCart();

        call.enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CartResponse cartResponse = response.body();
                    currentCartItems = cartResponse.getItems();

                    // Cập nhật tóm tắt
                    int totalQuantity = 0;
                    if (currentCartItems != null) {
                        for (CartItem item : currentCartItems) {
                            totalQuantity += item.getQuantity();
                        }
                    }
                    tvOrderSummary.setText("Tổng tiền: " + formatter.format(cartResponse.getTotalAmount()) +
                            " (" + totalQuantity + " sản phẩm)");
                } else {
                    Toast.makeText(CheckoutActivity.this, "Không thể tải giỏ hàng để đặt. Mã: " + response.code(), Toast.LENGTH_LONG).show();
                    tvOrderSummary.setText("Lỗi tải giỏ hàng.");
                    currentCartItems = new ArrayList<>();
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                Toast.makeText(CheckoutActivity.this, "Lỗi mạng khi tải giỏ hàng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void placeOrder() {
        if (currentCartItems == null || currentCartItems.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống! Vui lòng thêm sản phẩm.", Toast.LENGTH_LONG).show();
            return;
        }

        // 1. Lấy dữ liệu người dùng
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String addressLine = etAddressLine.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String district = etDistrict.getText().toString().trim();
        String ward = etWard.getText().toString().trim();

        if (fullName.isEmpty() || phone.isEmpty() || addressLine.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin nhận hàng.", Toast.LENGTH_LONG).show();
            return;
        }

        // 2. Chuyển đổi CartItem thành OrderItemRequest
        List<OrderItemRequest> orderItems = new ArrayList<>();
        for (CartItem cartItem : currentCartItems) {
            orderItems.add(new OrderItemRequest(cartItem.getProductId(), cartItem.getQuantity()));
        }

        // 3. Tạo OrderRequest
        OrderRequest orderRequest = new OrderRequest(
                fullName, phone, addressLine, city, district, ward, orderItems
        );

        // 4. Gọi API Đặt hàng
        ApiService apiService = ServiceGenerator.createService(ApiService.class);
        Call<Void> call = apiService.placeOrder(orderRequest);

        btnPlaceOrder.setEnabled(false); // Vô hiệu hóa nút để tránh nhấp đúp

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                btnPlaceOrder.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(CheckoutActivity.this, "ĐẶT HÀNG THÀNH CÔNG!", Toast.LENGTH_LONG).show();
                    // Chuyển về màn hình chính hoặc màn hình thông báo thành công
                    // finish();
                } else if (response.code() == 401) {
                    Toast.makeText(CheckoutActivity.this, "Lỗi: Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(CheckoutActivity.this, "ĐẶT HÀNG THẤT BẠI. Mã: " + response.code(), Toast.LENGTH_LONG).show();
                    Log.e("ORDER_API_ERROR", "Lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                btnPlaceOrder.setEnabled(true);
                Toast.makeText(CheckoutActivity.this, "Lỗi kết nối khi đặt hàng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}