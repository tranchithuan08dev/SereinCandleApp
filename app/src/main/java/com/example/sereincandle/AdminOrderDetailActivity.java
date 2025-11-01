package com.example.sereincandle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sereincandle.adapter.OrderItemAdapter;
import com.example.sereincandle.models.Order;
import com.example.sereincandle.models.OrderItem;
import com.example.sereincandle.models.OrderStatusRequest;
import com.example.sereincandle.network.ApiService;
import com.example.sereincandle.network.ServiceGenerator;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("deprecation")
public class AdminOrderDetailActivity extends AppCompatActivity {

    private TextView tvOrderCode, tvOrderDate, tvCustomerName, tvCustomerEmail, 
                     tvRecipientAddress, tvSubtotal, tvDiscount, tvShippingFee, 
                     tvTotalAmount, tvPaymentMethod;
    private RecyclerView rvOrderItems;
    private Spinner spOrderStatus;
    private Button btnUpdateStatus;

    private Order currentOrder;
    private OrderItemAdapter itemAdapter;
    private ApiService apiService;
    private final DecimalFormat formatter = new DecimalFormat("#,### VND");
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    // Danh sách trạng thái
    private final String[] statusNames = {
            "Pending",
            "Preparing",
            "Shipped",
            "Completed",
            "Cancelled"
    };

    private final int[] statusIds = {1, 2, 3, 4, 5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_detail);

        // Setup nút Back
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Lấy Order từ Intent
        Order orderFromIntent = null;
        try {
            orderFromIntent = (Order) getIntent().getSerializableExtra("ORDER");
        } catch (Exception e) {
            Log.e("INTENT_ERROR", "Error getting Order from Intent: " + e.getMessage());
            e.printStackTrace();
        }
        
        if (orderFromIntent == null) {
            Toast.makeText(this, "Không tìm thấy thông tin đơn hàng", Toast.LENGTH_SHORT).show();
            Log.e("ORDER_ERROR", "Order from Intent is null");
            finish();
            return;
        }

        currentOrder = orderFromIntent;
        apiService = ServiceGenerator.createService(ApiService.class);
        
        // Log để debug
        Log.d("ORDER_DETAIL", "Order loaded: ID=" + currentOrder.getOrderId() + 
                ", Code=" + currentOrder.getOrderCode() + 
                ", Items count=" + (currentOrder.getItems() != null ? currentOrder.getItems().size() : 0));

        // Ánh xạ views
        tvOrderCode = findViewById(R.id.tvOrderCode);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvCustomerEmail = findViewById(R.id.tvCustomerEmail);
        tvRecipientAddress = findViewById(R.id.tvRecipientAddress);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvShippingFee = findViewById(R.id.tvShippingFee);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        rvOrderItems = findViewById(R.id.rvOrderItems);
        spOrderStatus = findViewById(R.id.spOrderStatus);
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus);

        // Setup RecyclerView
        itemAdapter = new OrderItemAdapter(this, 
                currentOrder.getItems() != null ? currentOrder.getItems() : new ArrayList<>());
        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
        rvOrderItems.setAdapter(itemAdapter);

        // Setup Spinner
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                statusNames
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spOrderStatus.setAdapter(statusAdapter);

        // Set selected status
        String currentStatus = currentOrder.getStatusName();
        if (currentStatus != null) {
            for (int i = 0; i < statusNames.length; i++) {
                if (statusNames[i].equalsIgnoreCase(currentStatus)) {
                    spOrderStatus.setSelection(i);
                    break;
                }
            }
        }

        // Disable nút nếu order đã Cancelled
        if (currentOrder.isCancelled()) {
            btnUpdateStatus.setEnabled(false);
            btnUpdateStatus.setText("Đơn hàng đã hủy - Không thể cập nhật");
            spOrderStatus.setEnabled(false);
        }

        // Xử lý nút Cập nhật trạng thái
        btnUpdateStatus.setOnClickListener(v -> {
            updateOrderStatus();
        });

        // Hiển thị dữ liệu
        displayOrderDetail();
    }

    private void displayOrderDetail() {
        try {
            // Thông tin đơn hàng
            tvOrderCode.setText("Mã đơn: " + (currentOrder.getOrderCode() != null ? currentOrder.getOrderCode() : "N/A"));
            
            if (currentOrder.getCreatedAt() != null) {
                try {
                    tvOrderDate.setText("Ngày đặt: " + dateFormatter.format(currentOrder.getCreatedAt()));
                } catch (Exception e) {
                    Log.e("DATE_FORMAT", "Error formatting date: " + e.getMessage());
                    tvOrderDate.setText("Ngày đặt: " + currentOrder.getCreatedAt().toString());
                }
            } else {
                tvOrderDate.setText("Ngày đặt: N/A");
            }

            // Thông tin khách hàng
            tvCustomerName.setText("Tên: " + (currentOrder.getCustomerFullName() != null ? currentOrder.getCustomerFullName() : "N/A"));
            tvCustomerEmail.setText("Email: " + 
                    (currentOrder.getCustomerEmail() != null ? currentOrder.getCustomerEmail() : "N/A"));
            tvRecipientAddress.setText("Địa chỉ: " + 
                    (currentOrder.getRecipientAddress() != null ? currentOrder.getRecipientAddress() : "N/A"));

            // Tính tổng tiền sản phẩm (subtotal)
            double subtotal = 0;
            if (currentOrder.getItems() != null && !currentOrder.getItems().isEmpty()) {
                for (OrderItem item : currentOrder.getItems()) {
                    if (item != null) {
                        subtotal += item.getTotalPrice();
                    }
                }
            }
            tvSubtotal.setText(formatter.format(subtotal));

            // Giảm giá, phí vận chuyển, tổng cộng
            tvDiscount.setText("-" + formatter.format(currentOrder.getDiscountAmount()));
            tvShippingFee.setText(formatter.format(currentOrder.getShippingFee()));
            tvTotalAmount.setText(formatter.format(currentOrder.getTotalAmount()));

            // Phương thức thanh toán
            tvPaymentMethod.setText(currentOrder.getPaymentMethodName() != null ? 
                    currentOrder.getPaymentMethodName() : "N/A");

            // Update RecyclerView
            if (itemAdapter != null) {
                itemAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Log.e("DISPLAY_ERROR", "Error displaying order detail: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Lỗi hiển thị chi tiết đơn hàng: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void updateOrderStatus() {
        int selectedPosition = spOrderStatus.getSelectedItemPosition();
        if (selectedPosition < 0 || selectedPosition >= statusIds.length) {
            Toast.makeText(this, "Vui lòng chọn trạng thái", Toast.LENGTH_SHORT).show();
            return;
        }

        int newStatusId = statusIds[selectedPosition];
        String newStatusName = statusNames[selectedPosition];

        // Nếu đã là status hiện tại, không cần update
        if (newStatusName.equalsIgnoreCase(currentOrder.getStatusName())) {
            Toast.makeText(this, "Trạng thái không thay đổi", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gọi API
        OrderStatusRequest request = new OrderStatusRequest(newStatusId);
        Call<Void> call = apiService.updateOrderStatus(currentOrder.getOrderId(), request);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminOrderDetailActivity.this,
                            "Cập nhật trạng thái thành công", Toast.LENGTH_SHORT).show();
                    // Cập nhật order hiện tại
                    currentOrder.setStatusName(newStatusName);
                    // Disable nếu đã cancelled
                    if (currentOrder.isCancelled()) {
                        btnUpdateStatus.setEnabled(false);
                        btnUpdateStatus.setText("Đơn hàng đã hủy - Không thể cập nhật");
                        spOrderStatus.setEnabled(false);
                    }
                    // Quay lại danh sách
                    finish();
                } else {
                    String errorMsg = "Không thể cập nhật trạng thái";
                    if (response.code() == 401) {
                        errorMsg = "Lỗi xác thực (401). Vui lòng đăng nhập lại.";
                        // Có thể force logout nếu token hết hạn
                    } else if (response.code() == 403) {
                        errorMsg = "Không có quyền thực hiện hành động này.";
                    } else if (response.code() == 404) {
                        errorMsg = "Không tìm thấy đơn hàng.";
                    }
                    
                    Toast.makeText(AdminOrderDetailActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    Log.e("API_ERROR", "Error code: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e("API_ERROR", "Error body: " + errorBody);
                        }
                    } catch (Exception e) {
                        Log.e("API_ERROR", "Error reading error body: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminOrderDetailActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("NETWORK_ERROR", "Error: " + t.getMessage());
            }
        });
    }
}
