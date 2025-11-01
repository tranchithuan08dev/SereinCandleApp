package com.example.sereincandle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sereincandle.R;
import com.example.sereincandle.models.Order;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final Context context;
    private List<Order> orderList;
    private final DecimalFormat formatter = new DecimalFormat("#,### VND");
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    // Listener cho click vào item
    private final OnOrderClickListener orderClickListener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public OrderAdapter(Context context, OnOrderClickListener orderClickListener) {
        this.context = context;
        this.orderClickListener = orderClickListener;
        this.orderList = new ArrayList<>();
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList != null ? orderList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Gán dữ liệu
        holder.tvOrderCode.setText("Mã đơn: " + order.getOrderCode());
        holder.tvCustomerName.setText("Khách hàng: " + order.getCustomerFullName());
        
        // Format số điện thoại nếu có trong recipientAddress hoặc cần field riêng
        // Tạm thời dùng recipientAddress làm hint
        holder.tvCustomerPhone.setText("Địa chỉ: " + 
                (order.getRecipientAddress() != null ? order.getRecipientAddress() : "N/A"));
        
        holder.tvTotalAmount.setText(formatter.format(order.getTotalAmount()));

        // Format ngày
        if (order.getCreatedAt() != null) {
            holder.tvOrderDate.setText("Ngày đặt: " + dateFormatter.format(order.getCreatedAt()));
        } else {
            holder.tvOrderDate.setText("Ngày đặt: N/A");
        }

        // Hiển thị trạng thái với màu sắc
        String statusName = order.getStatusName();
        if (statusName == null) statusName = "Pending";
        
        holder.tvOrderStatus.setText(statusName);
        
        // Màu sắc theo trạng thái
        int statusColor;
        switch (statusName.toLowerCase()) {
            case "pending":
                statusColor = 0xFFFF9800; // Orange
                break;
            case "preparing":
                statusColor = 0xFF2196F3; // Blue
                break;
            case "shipped":
                statusColor = 0xFF4CAF50; // Green
                break;
            case "completed":
                statusColor = 0xFF4CAF50; // Green
                break;
            case "cancelled":
                statusColor = 0xFFF44336; // Red
                break;
            default:
                statusColor = 0xFF757575; // Gray
                break;
        }
        holder.tvOrderStatus.setBackgroundColor(statusColor);

        // Xử lý click vào item
        holder.itemView.setOnClickListener(v -> {
            if (orderClickListener != null) {
                orderClickListener.onOrderClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderCode, tvOrderStatus, tvCustomerName, tvCustomerPhone, 
                 tvOrderDate, tvTotalAmount;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderCode = itemView.findViewById(R.id.tvOrderCode);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvCustomerPhone = itemView.findViewById(R.id.tvCustomerPhone);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
        }
    }
}
