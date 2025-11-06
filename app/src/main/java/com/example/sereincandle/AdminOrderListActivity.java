package com.example.sereincandle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sereincandle.adapter.OrderAdapter;
import com.example.sereincandle.models.Order;
import com.example.sereincandle.network.ApiService;
import com.example.sereincandle.network.ServiceGenerator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminOrderListActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private Spinner spFilterStatus;
    private SwipeRefreshLayout swipeRefresh;
    private OrderAdapter adapter;
    private List<Order> allOrders;
    private ApiService apiService;

    // Danh sách trạng thái để filter
    private final String[] statusOptions = {
            "Tất cả",
            "Pending",
            "Preparing",
            "Shipped",
            "Completed",
            "Cancelled"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_list);

        // Khởi tạo
        rvOrders = findViewById(R.id.rvOrders);
        spFilterStatus = findViewById(R.id.spFilterStatus);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        allOrders = new ArrayList<>();
        apiService = ServiceGenerator.createService(ApiService.class);

        // Setup Spinner filter
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                statusOptions
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFilterStatus.setAdapter(statusAdapter);

        // Setup RecyclerView
        adapter = new OrderAdapter(this, order -> {
            // Nhấn vào order → Chuyển sang AdminOrderDetailActivity
            try {
                Intent intent = new Intent(AdminOrderListActivity.this, AdminOrderDetailActivity.class);
                intent.putExtra("ORDER", order); // Pass Order object qua Intent
                Log.d("ORDER_NAVIGATION", "Passing Order: ID=" + order.getOrderId() + 
                        ", Code=" + order.getOrderCode() + 
                        ", Items=" + (order.getItems() != null ? order.getItems().size() : 0));
                startActivity(intent);
            } catch (Exception e) {
                Log.e("ORDER_NAVIGATION", "Error passing Order to detail: " + e.getMessage(), e);
                Toast.makeText(AdminOrderListActivity.this, 
                        "Lỗi khi mở chi tiết đơn hàng: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(adapter);

        // Xử lý filter
        spFilterStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterOrders(statusOptions[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Pull to refresh
        swipeRefresh.setOnRefreshListener(this::loadOrders);

        // Load danh sách đơn hàng
        loadOrders();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload khi quay lại màn hình
        loadOrders();
    }

    private void loadOrders() {
        swipeRefresh.setRefreshing(true);

        Call<List<Order>> call = apiService.getAdminOrders();
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    allOrders = response.body();
                    Log.d("API_SUCCESS", "Loaded " + allOrders.size() + " orders");
                    // Áp dụng filter hiện tại
                    String selectedStatus = statusOptions[spFilterStatus.getSelectedItemPosition()];
                    filterOrders(selectedStatus);
                } else {
                    String errorMsg = "Không thể tải danh sách đơn hàng. Mã lỗi: " + response.code();
                    Toast.makeText(AdminOrderListActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    Log.e("API_ERROR", "Error code: " + response.code());
                    
                    // Log error body nếu có
                    try {
                        if (response.errorBody() != null) {
                            try (okhttp3.ResponseBody errorBody = response.errorBody()) {
                                String errorBodyString = errorBody.string();
                                Log.e("API_ERROR", "Error body: " + errorBodyString);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("API_ERROR", "Error reading error body: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                
                // Log chi tiết lỗi
                String errorMsg = t.getMessage();
                Log.e("NETWORK_ERROR", "Error type: " + t.getClass().getSimpleName());
                Log.e("NETWORK_ERROR", "Error message: " + errorMsg);
                if (t.getCause() != null) {
                    Log.e("NETWORK_ERROR", "Cause: " + t.getCause().getMessage());
                }
                Log.e("NETWORK_ERROR", "Full stack trace:", t);
                
                // Hiển thị message ngắn gọn hơn
                String displayMsg = "Lỗi kết nối";
                if (errorMsg != null && errorMsg.contains("JsonSyntaxException")) {
                    displayMsg = "Lỗi parse dữ liệu từ server. Vui lòng kiểm tra log.";
                } else if (errorMsg != null && errorMsg.contains("IOException")) {
                    displayMsg = "Lỗi kết nối mạng. Vui lòng kiểm tra kết nối.";
                } else if (errorMsg != null) {
                    displayMsg = "Lỗi: " + errorMsg;
                }
                
                Toast.makeText(AdminOrderListActivity.this, displayMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void filterOrders(String statusFilter) {
        List<Order> filteredList = new ArrayList<>();

        if ("Tất cả".equals(statusFilter)) {
            filteredList.addAll(allOrders);
        } else {
            for (Order order : allOrders) {
                if (statusFilter.equalsIgnoreCase(order.getStatusName())) {
                    filteredList.add(order);
                }
            }
        }

        adapter.setOrderList(filteredList);
    }
}
