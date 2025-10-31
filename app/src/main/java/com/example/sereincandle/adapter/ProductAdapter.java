package com.example.sereincandle.adapter; // Đảm bảo đúng package của Adapter

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton; // Thêm ImageButton
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
    private final DecimalFormat formatter = new DecimalFormat("#,### VND");

    // Listeners cho hai hành động khác nhau
    private final OnProductClickListener itemClickListener;
    private final OnAddToCartClickListener cartClickListener;

    /**
     * Interface Listener 1: Xử lý khi nhấp vào toàn bộ item sản phẩm (Xem chi tiết).
     */
    public interface OnProductClickListener {
        void onProductClick(int productId);
    }

    /**
     * Interface Listener 2: Xử lý khi nhấp vào nút Thêm vào Giỏ hàng.
     */
    public interface OnAddToCartClickListener {
        void onAddToCartClick(int productId);
    }

    /**
     * Constructor được cập nhật để nhận cả hai listener.
     */
    public ProductAdapter(Context context, List<Product> productList,
                          OnProductClickListener itemClickListener,
                          OnAddToCartClickListener cartClickListener) {
        this.context = context;
        this.productList = productList;
        this.itemClickListener = itemClickListener;
        this.cartClickListener = cartClickListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // 1. Gán dữ liệu Text và Image
        holder.tvName.setText(product.getName());
        holder.tvDescription.setText(product.getDescription() != null ? product.getDescription() : "Đang cập nhật");
        holder.tvPrice.setText(formatter.format(product.getPrice()));

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

        // 2. Xử lý sự kiện nhấp chuột Item (Xem chi tiết)
        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onProductClick(product.getProductId());
            }
        });

        // 3. Xử lý sự kiện nhấp chuột NÚT GIỎ HÀNG
        holder.btnAddToCart.setOnClickListener(v -> {
            if (cartClickListener != null) {
                cartClickListener.onAddToCartClick(product.getProductId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    /**
     * ViewHolder: Chứa tham chiếu đến các View con
     */
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvDescription, tvPrice;
        ImageButton btnAddToCart; // Tham chiếu đến nút giỏ hàng

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivProductImage);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvDescription = itemView.findViewById(R.id.tvProductDescription);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            btnAddToCart = itemView.findViewById(R.id.btnAddProductToCart);
        }
    }
}