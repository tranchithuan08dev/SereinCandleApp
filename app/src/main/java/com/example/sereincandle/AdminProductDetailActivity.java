package com.example.sereincandle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sereincandle.models.Category;
import com.example.sereincandle.models.Product;
import com.example.sereincandle.models.ProductDetailResponse;
import com.example.sereincandle.models.ProductRequest;
import com.example.sereincandle.network.ApiService;
import com.example.sereincandle.network.ServiceGenerator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminProductDetailActivity extends AppCompatActivity {

    private ImageView ivProductImage;
    private EditText etProductName, etSku, etShortDescription, etDescription, 
                     etIngredients, etBurnTime, etPrice;
    private Spinner spCategory;
    private CheckBox cbIsActive;
    private Button btnSave, btnDelete;

    private int productId = -1;
    private Product currentProduct;
    private List<Category> categoryList;
    private ArrayAdapter<Category> categoryAdapter;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_detail);

        // Lấy productId từ Intent
        if (getIntent().getExtras() != null) {
            productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        }

        if (productId == -1) {
            Toast.makeText(this, "Không tìm thấy ID sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo
        apiService = ServiceGenerator.createService(ApiService.class);
        categoryList = new ArrayList<>();

        // Ánh xạ views
        ivProductImage = findViewById(R.id.ivProductImage);
        etProductName = findViewById(R.id.etProductName);
        etSku = findViewById(R.id.etSku);
        etShortDescription = findViewById(R.id.etShortDescription);
        etDescription = findViewById(R.id.etDescription);
        etIngredients = findViewById(R.id.etIngredients);
        etBurnTime = findViewById(R.id.etBurnTime);
        etPrice = findViewById(R.id.etPrice);
        spCategory = findViewById(R.id.spCategory);
        cbIsActive = findViewById(R.id.cbIsActive);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        // Setup Spinner Category
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryAdapter);

        // Load danh sách Category
        loadCategories();

        // Load chi tiết sản phẩm
        loadProductDetail();

        // Xử lý nút Lưu
        btnSave.setOnClickListener(v -> {
            if (validateForm()) {
                updateProduct();
            }
        });

        // Xử lý nút Xóa
        btnDelete.setOnClickListener(v -> showDeleteConfirmDialog());
    }

    private void loadCategories() {
        Call<List<Category>> call = apiService.getAllCategories();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();
                    categoryList.addAll(response.body());
                    categoryAdapter.notifyDataSetChanged();
                    
                    // Sau khi load xong categories, set lại selected category trong spinner
                    if (currentProduct != null) {
                        setSelectedCategory(currentProduct);
                    }
                } else {
                    Log.e("API_ERROR", "Error loading categories: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e("NETWORK_ERROR", "Error loading categories: " + t.getMessage());
            }
        });
    }

    private void loadProductDetail() {
        Call<ProductDetailResponse> call = apiService.getProductDetail(productId);
        call.enqueue(new Callback<ProductDetailResponse>() {
            @Override
            public void onResponse(Call<ProductDetailResponse> call, Response<ProductDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    currentProduct = response.body().getData();
                    displayProductDetail(currentProduct);
                } else {
                    Toast.makeText(AdminProductDetailActivity.this, 
                            "Không thể tải chi tiết sản phẩm", Toast.LENGTH_SHORT).show();
                    Log.e("API_ERROR", "Error code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ProductDetailResponse> call, Throwable t) {
                Toast.makeText(AdminProductDetailActivity.this, 
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("NETWORK_ERROR", "Error: " + t.getMessage());
            }
        });
    }

    private void displayProductDetail(Product product) {
        // Hiển thị ảnh
        String imageUrl = product.getPrimaryImageUrl();
        if (imageUrl != null) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(ivProductImage);
        }

        // Điền các field
        etProductName.setText(product.getName());
        etSku.setText(product.getSku() != null ? product.getSku() : "");
        etShortDescription.setText(""); // Product model chưa có shortDescription
        etDescription.setText(product.getDescription());
        etIngredients.setText(product.getIngredients() != null ? product.getIngredients() : "");
        etBurnTime.setText(product.getBurnTime() != null ? product.getBurnTime() : "");
        etPrice.setText(String.valueOf(product.getPrice()));
        cbIsActive.setChecked(product.isActive());

        // Set selected category trong spinner (sau khi categories đã load)
        setSelectedCategory(product);
    }

    private void setSelectedCategory(Product product) {
        // Tìm category match với categoryName của product
        if (product.getCategoryName() != null && categoryList.size() > 0) {
            for (int i = 0; i < categoryList.size(); i++) {
                if (product.getCategoryName().equals(categoryList.get(i).getName())) {
                    spCategory.setSelection(i);
                    break;
                }
            }
        }
    }

    private boolean validateForm() {
        if (etProductName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên sản phẩm", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etDescription.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mô tả", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etPrice.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập giá", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            double price = Double.parseDouble(etPrice.getText().toString().trim());
            if (price < 0) {
                Toast.makeText(this, "Giá không được âm", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (spCategory.getSelectedItem() == null) {
            Toast.makeText(this, "Vui lòng chọn danh mục", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void updateProduct() {
        // Tạo ProductRequest từ form
        ProductRequest request = new ProductRequest();
        request.setName(etProductName.getText().toString().trim());
        request.setSku(etSku.getText().toString().trim());
        request.setShortDescription(etShortDescription.getText().toString().trim());
        request.setDescription(etDescription.getText().toString().trim());
        request.setIngredients(etIngredients.getText().toString().trim());
        request.setBurnTime(etBurnTime.getText().toString().trim());
        request.setPrice(Double.parseDouble(etPrice.getText().toString().trim()));
        
        Category selectedCategory = (Category) spCategory.getSelectedItem();
        request.setCategoryId(selectedCategory.getCategoryId());

        // Gọi API cập nhật
        Call<ProductDetailResponse> call = apiService.updateProduct(productId, request);
        call.enqueue(new Callback<ProductDetailResponse>() {
            @Override
            public void onResponse(Call<ProductDetailResponse> call, Response<ProductDetailResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminProductDetailActivity.this, 
                            "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại danh sách
                } else {
                    Toast.makeText(AdminProductDetailActivity.this, 
                            "Không thể cập nhật sản phẩm", Toast.LENGTH_SHORT).show();
                    Log.e("API_ERROR", "Error code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ProductDetailResponse> call, Throwable t) {
                Toast.makeText(AdminProductDetailActivity.this, 
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("NETWORK_ERROR", "Error: " + t.getMessage());
            }
        });
    }

    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteProduct();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteProduct() {
        Call<Void> call = apiService.deleteProduct(productId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminProductDetailActivity.this, 
                            "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại danh sách
                } else {
                    Toast.makeText(AdminProductDetailActivity.this, 
                            "Không thể xóa sản phẩm", Toast.LENGTH_SHORT).show();
                    Log.e("API_ERROR", "Error code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminProductDetailActivity.this, 
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("NETWORK_ERROR", "Error: " + t.getMessage());
            }
        });
    }
}
