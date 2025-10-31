// File: ProductDetailActivity.java
package com.example.sereincandle; // Đảm bảo đúng package

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sereincandle.models.Product;
import com.example.sereincandle.models.ProductDetailResponse;
import com.example.sereincandle.network.ApiService;
import com.example.sereincandle.network.ServiceGenerator;
import java.text.DecimalFormat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView ivDetailImage;
    private TextView tvDetailName, tvDetailPrice, tvDetailDescription;
    private final DecimalFormat formatter = new DecimalFormat("#,### VND");
    private int productId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // 1. Ánh xạ View
        ivDetailImage = findViewById(R.id.ivDetailImage);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailPrice = findViewById(R.id.tvDetailPrice);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);

        // 2. Lấy ID sản phẩm từ Intent
        if (getIntent().getExtras() != null) {
            productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        }

        if (productId != -1) {
            fetchProductDetail(productId);
        } else {
            Toast.makeText(this, "Không tìm thấy ID sản phẩm.", Toast.LENGTH_LONG).show();
            finish(); // Đóng Activity nếu không có ID
        }
    }

    private void fetchProductDetail(int id) {
        ApiService apiService = ServiceGenerator.createService(ApiService.class);

        Call<ProductDetailResponse> call = apiService.getProductDetail(id);

        call.enqueue(new Callback<ProductDetailResponse>() {
            @Override
            public void onResponse(Call<ProductDetailResponse> call, Response<ProductDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    Product product = response.body().getData();
                    displayProductDetail(product);
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Lỗi tải chi tiết: Mã " + response.code(), Toast.LENGTH_LONG).show();
                    Log.e("DETAIL_API_ERROR", "Lỗi phản hồi chi tiết: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ProductDetailResponse> call, Throwable t) {
                Toast.makeText(ProductDetailActivity.this, "Lỗi kết nối mạng chi tiết: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("DETAIL_NETWORK_ERROR", "Lỗi: " + t.getMessage(), t);
            }
        });
    }

    private void displayProductDetail(Product product) {
        // Hiển thị dữ liệu lên giao diện
        tvDetailName.setText(product.getName());
        tvDetailDescription.setText(product.getDescription());
        tvDetailPrice.setText(formatter.format(product.getPrice()));

        // Tải ảnh
        String imageUrl = product.getPrimaryImageUrl();
        if (imageUrl != null) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background) // Thay bằng drawable tạm thời của bạn
                    .error(R.drawable.ic_launcher_background)
                    .into(ivDetailImage);
        }

        // Đặt tiêu đề Activity là tên sản phẩm
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(product.getName());
        }
    }
}