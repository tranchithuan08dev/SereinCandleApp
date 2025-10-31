package com.example.sereincandle; // Đảm bảo đúng tên package của bạn

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

// Đảm bảo import đúng đường dẫn cho các lớp sau:
import com.example.sereincandle.adapter.CartAdapter;
import com.example.sereincandle.models.CartResponse;
import com.example.sereincandle.models.CartItem;
import com.example.sereincandle.network.ApiService;
import com.example.sereincandle.network.ServiceGenerator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    private RecyclerView rvCartItems;
    private TextView tvTotalAmount;
    private CartAdapter adapter;
    private List<CartItem> cartItemList;
    // Sử dụng định dạng tiền tệ
    private final DecimalFormat formatter = new DecimalFormat("#,### VND");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // 1. Ánh xạ
        rvCartItems = findViewById(R.id.rvCartItems);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);

        // Khởi tạo danh sách
        cartItemList = new ArrayList<>();

        // 2. Thiết lập RecyclerView
        adapter = new CartAdapter(this, cartItemList);
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        rvCartItems.setAdapter(adapter);

        // 3. Tải dữ liệu giỏ hàng
        fetchCart();
    }

    /**
     * Phương thức gọi API để lấy thông tin giỏ hàng (Yêu cầu Token Bearer)
     */
    private void fetchCart() {
        // ServiceGenerator tự động thêm Token Bearer
        ApiService apiService = ServiceGenerator.createService(ApiService.class);
        Call<CartResponse> call = apiService.getCart();

        call.enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CartResponse cartResponse = response.body();

                    // Cập nhật tổng tiền
                    tvTotalAmount.setText(formatter.format(cartResponse.getTotalAmount()));

                    // Cập nhật danh sách item
                    List<CartItem> items = cartResponse.getItems();
                    if (items != null) {
                        adapter.updateItems(items);
                    }

                    Toast.makeText(CartActivity.this, "Tải giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 401) {
                    Toast.makeText(CartActivity.this, "Lỗi: Vui lòng đăng nhập lại (Token hết hạn).", Toast.LENGTH_LONG).show();
                } else {
                    // Xử lý giỏ hàng trống hoặc lỗi khác
                    Toast.makeText(CartActivity.this, "Giỏ hàng trống hoặc Lỗi: " + response.code(), Toast.LENGTH_LONG).show();
                    // Đảm bảo tổng tiền là 0 nếu giỏ hàng trống hoặc lỗi
                    tvTotalAmount.setText(formatter.format(0));
                    Log.e("CART_API_ERROR", "Lỗi phản hồi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                // Xử lý lỗi kết nối mạng
                Toast.makeText(CartActivity.this, "Lỗi kết nối mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("CART_NETWORK_FAILURE", "Lỗi: " + t.getMessage(), t);
            }
        });
    }
}