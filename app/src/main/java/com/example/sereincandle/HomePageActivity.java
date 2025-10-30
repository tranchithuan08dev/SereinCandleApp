package com.example.sereincandle;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
public class HomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Nhận Intent đã khởi chạy Activity này
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // Lấy token đã gửi từ MainActivity
            String authToken = extras.getString("AUTH_TOKEN");
            if (authToken != null) {
                // Hiển thị token (thường thì bạn sẽ lưu token này vào SharedPreferences)
                Toast.makeText(this, "Token đã nhận: " + authToken.substring(0, 10) + "...", Toast.LENGTH_LONG).show();
                // **TODO:** Thường bạn sẽ lưu token này bằng SharedPreferences hoặc Room.
            }
        }
    }
}