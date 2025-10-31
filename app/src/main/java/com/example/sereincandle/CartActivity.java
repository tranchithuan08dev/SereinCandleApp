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

// Triển khai interface xóa
public class CartActivity extends AppCompatActivity
        implements CartAdapter.OnRemoveItemClickListener {

    private RecyclerView rvCartItems;
    private TextView tvTotalAmount;
    private CartAdapter adapter;
    private List<CartItem> cartItemList;
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
        // Truyền 'this' (listener xóa) vào Adapter
        adapter = new CartAdapter(this, cartItemList, this);
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        rvCartItems.setAdapter(adapter);

        // 3. Tải dữ liệu giỏ hàng
        fetchCart();
    }

    /**
     * Phương thức triển khai từ OnRemoveItemClickListener (Xử lý khi nhấp nút Xóa)
     * @param productId ID của sản phẩm cần xóa
     */
    @Override
    public void onRemoveItemClick(int productId) {
        deleteItemFromCart(productId);
    }

    private void deleteItemFromCart(int productId) {
        ApiService apiService = ServiceGenerator.createService(ApiService.class);

        // Gọi API DELETE bằng productId
        Call<Void> call = apiService.deleteCartItem(productId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CartActivity.this, "Đã xóa sản phẩm khỏi giỏ hàng.", Toast.LENGTH_SHORT).show();
                    // 💥 QUAN TRỌNG: Tải lại giỏ hàng để cập nhật giao diện
                    fetchCart();
                } else if (response.code() == 401) {
                    Toast.makeText(CartActivity.this, "Lỗi: Vui lòng đăng nhập lại (Token hết hạn).", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(CartActivity.this, "Lỗi xóa giỏ hàng: Mã " + response.code(), Toast.LENGTH_LONG).show();
                    Log.e("CART_DELETE_ERROR", "Phản hồi lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Lỗi kết nối khi xóa: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    /**
     * Phương thức gọi API để lấy thông tin giỏ hàng
     */
    private void fetchCart() {
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
                } else {
                    // Xử lý giỏ hàng trống hoặc lỗi khác
                    tvTotalAmount.setText(formatter.format(0));
                    adapter.updateItems(new ArrayList<>()); // Xóa danh sách hiện tại
                    Toast.makeText(CartActivity.this, "Giỏ hàng trống hoặc Lỗi: " + response.code(), Toast.LENGTH_LONG).show();
                    Log.e("CART_API_ERROR", "Lỗi phản hồi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                // Xử lý lỗi kết nối mạng
                tvTotalAmount.setText(formatter.format(0));
                Toast.makeText(CartActivity.this, "Lỗi kết nối mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("CART_NETWORK_FAILURE", "Lỗi: " + t.getMessage(), t);
            }
        });
    }
}