package com.example.sereincandle;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sereincandle.utils.SessionManager;

public class HomePageActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView tvWelcome;
    private Button btnViewProducts;
    private Button btnViewCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Khởi tạo SessionManager
        sessionManager = new SessionManager(this);

        // Ánh xạ các thành phần UI
        tvWelcome = findViewById(R.id.tvWelcome);
        btnViewProducts = findViewById(R.id.btnViewProducts);
        btnViewCart = findViewById(R.id.btnViewCart);

        // Hiển thị thông tin user
        String userName = sessionManager.getUserName();
        if (userName != null && !userName.isEmpty()) {
            tvWelcome.setText("Xin chào, " + userName);
        } else {
            tvWelcome.setText("Xin chào!");
        }

        // Thiết lập sự kiện nhấp chuột cho nút Xem Sản phẩm
        btnViewProducts.setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, ProductListActivity.class);
            startActivity(intent);
        });

        // Thiết lập sự kiện nhấp chuột cho nút Xem Giỏ hàng
        btnViewCart.setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, CartActivity.class);
            startActivity(intent);
        });
    }
}