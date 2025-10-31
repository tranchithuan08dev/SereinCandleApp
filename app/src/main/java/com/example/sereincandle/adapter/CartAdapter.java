package com.example.sereincandle.adapter; // Đảm bảo đúng package của Adapter

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sereincandle.R; // Import lớp R
import com.example.sereincandle.models.CartItem; // Import model CartItem

import java.text.DecimalFormat;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final Context context;
    private List<CartItem> cartItemList;
    private final DecimalFormat formatter = new DecimalFormat("#,### VND");

    // Nếu bạn muốn thêm chức năng Xóa/Cập nhật số lượng, bạn sẽ cần một Listener ở đây.

    public CartAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
    }

    // Phương thức tiện ích để cập nhật danh sách (được gọi từ CartActivity)
    public void updateItems(List<CartItem> newItems) {
        this.cartItemList = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItemList.get(position);

        // Gán dữ liệu
        holder.tvName.setText(item.getProductName());
        holder.tvPrice.setText(formatter.format(item.getPriceAtAdd()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        // Tải ảnh bằng Glide
        String imageUrl = item.getImageUrl();
        if (imageUrl != null) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.mipmap.ic_launcher);
        }

        // Thêm logic xử lý sự kiện cho btnRemoveCartItem nếu cần thiết
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    /**
     * ViewHolder: Chứa tham chiếu đến các View con trong item_cart.xml
     */
    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvPrice, tvQuantity;
        ImageButton btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivCartItemImage);
            tvName = itemView.findViewById(R.id.tvCartItemName);
            tvPrice = itemView.findViewById(R.id.tvCartItemPrice);
            tvQuantity = itemView.findViewById(R.id.tvCartItemQuantity);
            btnRemove = itemView.findViewById(R.id.btnRemoveCartItem);
        }
    }
}