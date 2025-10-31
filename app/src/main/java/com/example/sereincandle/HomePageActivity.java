// File: HomePageActivity.java (Cập nhật)

package com.example.sereincandle;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Button btnViewProducts = findViewById(R.id.btnViewProducts);
        Button btnViewCart = findViewById(R.id.btnViewCart); // Ánh xạ nút mới

        // Thiết lập sự kiện nhấp chuột cho nút Xem Sản phẩm
        btnViewProducts.setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, ProductListActivity.class);
            startActivity(intent);
        });

        // Thiết lập sự kiện nhấp chuột cho nút Xem Giỏ hàng (MỚI)
        btnViewCart.setOnClickListener(v -> {
            // Chuyển sang Activity mới: CartActivity
            Intent intent = new Intent(HomePageActivity.this, CartActivity.class);
            startActivity(intent);
        });
    }
}