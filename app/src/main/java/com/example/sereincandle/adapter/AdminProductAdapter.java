package com.example.sereincandle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import com.example.sereincandle.R;
import com.example.sereincandle.models.Product;

import java.text.DecimalFormat;
import java.util.List;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ProductViewHolder> {

    private final Context context;
    private final List<Product> productList;
    private final DecimalFormat formatter = new DecimalFormat("#,### VND");

    // Listeners
    private final OnEditClickListener editClickListener;
    private final OnDeleteClickListener deleteClickListener;

    public interface OnEditClickListener {
        void onEditClick(int productId);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int productId);
    }

    public AdminProductAdapter(Context context, List<Product> productList,
                               OnEditClickListener editClickListener,
                               OnDeleteClickListener deleteClickListener) {
        this.context = context;
        this.productList = productList;
        this.editClickListener = editClickListener;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Gán dữ liệu
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(formatter.format(product.getPrice()));
        
        // Hiển thị trạng thái
        String status = product.isActive() ? "Hoạt động" : "Ngừng bán";
        int statusColor = product.isActive() ? 
            android.graphics.Color.parseColor("#4CAF50") : 
            android.graphics.Color.parseColor("#F44336");
        holder.tvStatus.setText(status);
        holder.tvStatus.setTextColor(statusColor);

        // Load ảnh
        String imageUrl = product.getPrimaryImageUrl();
        if (imageUrl != null) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.mipmap.ic_launcher);
        }

        // Xử lý sự kiện nhấp chuột nút Sửa
        holder.btnEdit.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onEditClick(product.getProductId());
            }
        });

        // Xử lý sự kiện nhấp chuột nút Xóa
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(product.getProductId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvPrice, tvStatus;
        Button btnEdit, btnDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivProductImage);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            tvStatus = itemView.findViewById(R.id.tvProductStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
