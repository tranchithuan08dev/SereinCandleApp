package com.example.sereincandle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.sereincandle.adapter.AdminProductAdapter;
import com.example.sereincandle.models.Product;
import com.example.sereincandle.models.ProductListResponse;
import com.example.sereincandle.network.ApiService;
import com.example.sereincandle.network.ServiceGenerator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminProductListActivity extends AppCompatActivity {

    private RecyclerView rvProducts;
    private FloatingActionButton fabAddProduct;
    private AdminProductAdapter adapter;
    private List<Product> productList;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_list);

        // Khởi tạo
        rvProducts = findViewById(R.id.rvProducts);
        fabAddProduct = findViewById(R.id.fabAddProduct);
        productList = new ArrayList<>();
        apiService = ServiceGenerator.createService(ApiService.class);

        // Setup RecyclerView
        adapter = new AdminProductAdapter(
                this,
                productList,
                productId -> {
                    // Nhấn Sửa → Chuyển sang AdminProductDetailActivity
                    Intent intent = new Intent(AdminProductListActivity.this, AdminProductDetailActivity.class);
                    intent.putExtra("PRODUCT_ID", productId);
                    startActivity(intent);
                },
                productId -> {
                    // Nhấn Xóa → Hiển thị confirm dialog
                    showDeleteConfirmDialog(productId);
                }
        );

        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        rvProducts.setAdapter(adapter);

        // FAB: Thêm sản phẩm mới
        fabAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(AdminProductListActivity.this, AdminProductCreateActivity.class);
            startActivity(intent);
        });

        // Load danh sách sản phẩm
        loadProducts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload danh sách khi quay lại màn hình
        loadProducts();
    }

    private void loadProducts() {
        // Load tất cả sản phẩm (có thể phân trang sau)
        // Tạm thời load page 1 với pageSize lớn
        Call<ProductListResponse> call = apiService.getProducts(1, 100);
        
        call.enqueue(new Callback<ProductListResponse>() {
            @Override
            public void onResponse(Call<ProductListResponse> call, Response<ProductListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductListResponse productListResponse = response.body();
                    productList.clear();
                    if (productListResponse.getData() != null) {
                        productList.addAll(productListResponse.getData());
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(AdminProductListActivity.this, 
                            "Không thể tải danh sách sản phẩm", Toast.LENGTH_SHORT).show();
                    Log.e("API_ERROR", "Error code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ProductListResponse> call, Throwable t) {
                Toast.makeText(AdminProductListActivity.this, 
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("NETWORK_ERROR", "Error: " + t.getMessage());
            }
        });
    }

    private void showDeleteConfirmDialog(int productId) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteProduct(productId);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteProduct(int productId) {
        Call<Void> call = apiService.deleteProduct(productId);
        
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminProductListActivity.this, 
                            "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    // Reload danh sách
                    loadProducts();
                } else {
                    Toast.makeText(AdminProductListActivity.this, 
                            "Không thể xóa sản phẩm", Toast.LENGTH_SHORT).show();
                    Log.e("API_ERROR", "Error code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminProductListActivity.this, 
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("NETWORK_ERROR", "Error: " + t.getMessage());
            }
        });
    }
}
