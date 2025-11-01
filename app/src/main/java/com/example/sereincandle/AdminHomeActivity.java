package com.example.sereincandle;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sereincandle.utils.SessionManager;

public class AdminHomeActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView tvWelcome;
    private Button btnManageProducts;
    private Button btnManageOrders;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        // Khởi tạo SessionManager
        sessionManager = new SessionManager(this);

        // Ánh xạ các thành phần UI
        tvWelcome = findViewById(R.id.tvWelcome);
        btnManageProducts = findViewById(R.id.btnManageProducts);
        btnManageOrders = findViewById(R.id.btnManageOrders);
        btnLogout = findViewById(R.id.btnLogout);

        // Hiển thị thông tin admin
        String userName = sessionManager.getUserName();
        if (userName != null && !userName.isEmpty()) {
            tvWelcome.setText("Xin chào, " + userName);
        } else {
            tvWelcome.setText("Xin chào, Admin");
        }

        // Thiết lập sự kiện nhấp chuột cho nút Quản lý Sản phẩm
        btnManageProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminProductListActivity.class);
                startActivity(intent);
            }
        });

        // Thiết lập sự kiện nhấp chuột cho nút Quản lý Đơn hàng
        btnManageOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminOrderListActivity.class);
                startActivity(intent);
            }
        });

        // Thiết lập sự kiện nhấp chuột cho nút Đăng xuất
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xóa session
                sessionManager.logout();

                // Chuyển về MainActivity
                Intent intent = new Intent(AdminHomeActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}
