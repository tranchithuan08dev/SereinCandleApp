package com.example.sereincandle; // Đảm bảo đúng tên package của bạn

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

// Đảm bảo import đúng đường dẫn cho các lớp sau:
import com.example.sereincandle.adapter.ProductAdapter; // Sửa tên package nếu cần
import com.example.sereincandle.models.CartItemRequest;
import com.example.sereincandle.models.Product; // Sửa tên package nếu cần
import com.example.sereincandle.models.ProductListResponse; // Sửa tên package nếu cần
import com.example.sereincandle.network.ApiService;
import com.example.sereincandle.network.ServiceGenerator; // Sử dụng ServiceGenerator

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Triển khai interface OnProductClickListener
public class ProductListActivity extends AppCompatActivity
        implements ProductAdapter.OnProductClickListener,
        ProductAdapter.OnAddToCartClickListener {

    private RecyclerView rvProducts;
    private ProductAdapter adapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        rvProducts = findViewById(R.id.rvProducts);
        productList = new ArrayList<>();

        // CẬP NHẬT: Truyền 'this' (listener) cho cả hai tham số
        adapter = new ProductAdapter(this, productList, this, this);
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        rvProducts.setAdapter(adapter);

        fetchProducts();
    }
    @Override
    public void onAddToCartClick(int productId) {
        addItemToCart(productId, 1); // Thêm 1 sản phẩm
    }

    private void addItemToCart(int productId, int quantity) {
        // Tạo request body
        CartItemRequest request = new CartItemRequest(productId, quantity);

        // Khởi tạo service (sẽ tự động thêm token)
        ApiService apiService = ServiceGenerator.createService(ApiService.class);
        Call<Void> call = apiService.addItemToCart(request);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProductListActivity.this, "Đã thêm sản phẩm ID " + productId + " vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 401) {
                    Toast.makeText(ProductListActivity.this, "Lỗi: Vui lòng đăng nhập lại (Token hết hạn)!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ProductListActivity.this, "Lỗi thêm giỏ hàng: Mã " + response.code(), Toast.LENGTH_LONG).show();
                    Log.e("CART_API_ERROR", "Phản hồi lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProductListActivity.this, "Lỗi kết nối khi thêm giỏ hàng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    /**
     * Phương thức triển khai từ OnProductClickListener (Xử lý khi nhấp vào sản phẩm)
     * @param productId ID của sản phẩm được nhấp vào
     */
    @Override
    public void onProductClick(int productId) {
        // 1. Tạo Intent chuyển sang ProductDetailActivity
        Intent intent = new Intent(this, ProductDetailActivity.class);

        // 2. Đính kèm ID sản phẩm vào Intent
        intent.putExtra("PRODUCT_ID", productId);

        // 3. Khởi chạy Activity
        startActivity(intent);

        Toast.makeText(this, "Chuyển đến chi tiết sản phẩm ID: " + productId, Toast.LENGTH_SHORT).show();
    }

    /**
     * Phương thức gọi API để lấy danh sách sản phẩm
     */
    private void fetchProducts() {
        // Sử dụng ServiceGenerator đã cấu hình UnsafeOkHttpClient để vượt qua lỗi SSL
        ApiService apiService = ServiceGenerator.createService(ApiService.class);

        // Gọi endpoint sản phẩm: pageNumber=1, pageSize=0
        Call<ProductListResponse> call = apiService.getProducts(1, 0);

        call.enqueue(new Callback<ProductListResponse>() {
            @Override
            public void onResponse(Call<ProductListResponse> call, Response<ProductListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductListResponse productListResponse = response.body();
                    List<Product> fetchedProducts = productListResponse.getData();

                    if (fetchedProducts != null && !fetchedProducts.isEmpty()) {
                        // Cập nhật dữ liệu
                        productList.addAll(fetchedProducts);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(ProductListActivity.this, "Tải " + productList.size() + " sản phẩm thành công.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProductListActivity.this, "Không tìm thấy sản phẩm nào.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Xử lý lỗi HTTP
                    Toast.makeText(ProductListActivity.this, "Lỗi khi tải sản phẩm: Mã " + response.code(), Toast.LENGTH_LONG).show();
                    Log.e("API_ERROR", "Lỗi phản hồi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ProductListResponse> call, Throwable t) {
                // Xử lý lỗi kết nối mạng (thường là lỗi SSL/Mạng)
                Toast.makeText(ProductListActivity.this, "Lỗi kết nối mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("NETWORK_FAILURE", "Lỗi: " + t.getMessage(), t);
            }
        });
    }
}