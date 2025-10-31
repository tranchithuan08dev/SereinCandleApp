package com.example.sereincandle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.sereincandle.R; // Đảm bảo import đúng lớp R
import com.example.sereincandle.models.Product; // Đảm bảo import đúng lớp Product

import java.text.DecimalFormat;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final Context context;
    private final List<Product> productList;
    private final DecimalFormat formatter = new DecimalFormat("#,### VND"); // Định dạng giá
    private final OnProductClickListener listener; // Biến giữ tham chiếu đến Activity/Fragment

    /**
     * Interface Listener để giao tiếp ngược lại với Activity khi một sản phẩm được nhấp vào.
     */
    public interface OnProductClickListener {
        void onProductClick(int productId);
    }

    /**
     * Constructor được cập nhật để nhận thêm OnProductClickListener.
     */
    public ProductAdapter(Context context, List<Product> productList, OnProductClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Ánh xạ layout item_product.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // 1. Gán dữ liệu Text
        holder.tvName.setText(product.getName());
        // Sử dụng getDescription() an toàn, tránh lỗi nếu trường null
        holder.tvDescription.setText(product.getDescription() != null ? product.getDescription() : "Không có mô tả");
        holder.tvPrice.setText(formatter.format(product.getPrice()));

        // 2. Tải ảnh bằng Glide
        String imageUrl = product.getPrimaryImageUrl();
        if (imageUrl != null) {
            Glide.with(context)
                    .load(imageUrl)
                    // placeholder/error nên thay bằng drawable/mipmap riêng của bạn
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.mipmap.ic_launcher);
        }

        // 3. Xử lý sự kiện nhấp chuột
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                // Gọi phương thức trong Activity và truyền ID sản phẩm
                listener.onProductClick(product.getProductId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    /**
     * ViewHolder: Chứa tham chiếu đến các View con trong item_product.xml
     */
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvDescription, tvPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivProductImage);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvDescription = itemView.findViewById(R.id.tvProductDescription);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
        }
    }
}