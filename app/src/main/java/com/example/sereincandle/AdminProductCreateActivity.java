package com.example.sereincandle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sereincandle.models.Category;
import com.example.sereincandle.models.ProductDetailResponse;
import com.example.sereincandle.models.ProductRequest;
import com.example.sereincandle.network.ApiService;
import com.example.sereincandle.network.ServiceGenerator;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminProductCreateActivity extends AppCompatActivity {

    private Button btnSelectImages, btnUsePlaceholder, btnCreateProduct, btnCancel;
    private EditText etProductName, etSku, etShortDescription, etDescription, 
                     etIngredients, etBurnTime, etPrice;
    private Spinner spCategory;
    private CheckBox cbIsActive;
    private RecyclerView rvSelectedImages;

    private List<Uri> selectedImageUris;
    private SelectedImagesAdapter imagesAdapter;
    private List<Category> categoryList;
    private ArrayAdapter<Category> categoryAdapter;
    private ApiService apiService;
    private Gson gson;

    // Activity Result Launcher để chọn ảnh từ gallery
    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_create);

        // Khởi tạo
        selectedImageUris = new ArrayList<>();
        apiService = ServiceGenerator.createService(ApiService.class);
        gson = new Gson();
        categoryList = new ArrayList<>();

        // Ánh xạ views
        btnSelectImages = findViewById(R.id.btnSelectImages);
        btnUsePlaceholder = findViewById(R.id.btnUsePlaceholder);
        btnCreateProduct = findViewById(R.id.btnCreateProduct);
        btnCancel = findViewById(R.id.btnCancel);
        etProductName = findViewById(R.id.etProductName);
        etSku = findViewById(R.id.etSku);
        etShortDescription = findViewById(R.id.etShortDescription);
        etDescription = findViewById(R.id.etDescription);
        etIngredients = findViewById(R.id.etIngredients);
        etBurnTime = findViewById(R.id.etBurnTime);
        etPrice = findViewById(R.id.etPrice);
        spCategory = findViewById(R.id.spCategory);
        cbIsActive = findViewById(R.id.cbIsActive);
        rvSelectedImages = findViewById(R.id.rvSelectedImages);

        // Setup RecyclerView cho selected images
        imagesAdapter = new SelectedImagesAdapter(selectedImageUris, this::removeImage);
        rvSelectedImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvSelectedImages.setAdapter(imagesAdapter);

        // Setup Spinner Category
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryAdapter);

        // Load danh sách Category
        loadCategories();

        // Setup Activity Result Launcher cho chọn ảnh
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetMultipleContents(),
                uris -> {
                    if (uris != null && !uris.isEmpty()) {
                        selectedImageUris.addAll(uris);
                        imagesAdapter.notifyDataSetChanged();
                    }
                }
        );

        // Xử lý nút Chọn ảnh
        btnSelectImages.setOnClickListener(v -> {
            pickImageLauncher.launch("image/*");
        });

        // Xử lý nút Sử dụng ảnh mẫu (cho test khi không có ảnh trong gallery)
        btnUsePlaceholder.setOnClickListener(v -> {
            usePlaceholderImage();
        });

        // Xử lý nút Tạo sản phẩm
        btnCreateProduct.setOnClickListener(v -> {
            if (validateForm()) {
                createProduct();
            }
        });

        // Xử lý nút Hủy
        btnCancel.setOnClickListener(v -> finish());
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
        // Ảnh không bắt buộc - có thể để trống để test
        // if (selectedImageUris.isEmpty()) {
        //     Toast.makeText(this, "Vui lòng chọn ít nhất một ảnh", Toast.LENGTH_SHORT).show();
        //     return false;
        // }
        if (selectedImageUris.isEmpty()) {
            // Chỉ cảnh báo, không chặn
            Toast.makeText(this, "Cảnh báo: Sản phẩm chưa có ảnh. Bạn có thể thêm ảnh sau.", 
                    Toast.LENGTH_LONG).show();
        }
        return true;
    }

    private void createProduct() {
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

        // Convert ProductRequest thành JSON string
        String productDtoJson = gson.toJson(request);
        RequestBody productDtoBody = RequestBody.create(
                MediaType.parse("application/json"),
                productDtoJson
        );

        // Tạo MultipartBody.Part từ các Uri ảnh
        List<MultipartBody.Part> imageParts = new ArrayList<>();
        for (Uri imageUri : selectedImageUris) {
            try {
                // Đọc file từ Uri
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                if (inputStream != null) {
                    // Tạo file tạm thời
                    File tempFile = File.createTempFile("image", ".jpg", getCacheDir());
                    FileOutputStream outputStream = new FileOutputStream(tempFile);
                    
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    inputStream.close();
                    outputStream.close();

                    // Tạo RequestBody từ file
                    RequestBody requestFile = RequestBody.create(
                            MediaType.parse("image/jpeg"),
                            tempFile
                    );

                    // Tạo MultipartBody.Part
                    MultipartBody.Part imagePart = MultipartBody.Part.createFormData(
                            "images",
                            tempFile.getName(),
                            requestFile
                    );
                    imageParts.add(imagePart);
                }
            } catch (Exception e) {
                Log.e("IMAGE_ERROR", "Error processing image: " + e.getMessage());
            }
        }

        // Nếu không có ảnh, vẫn có thể tạo sản phẩm (backend sẽ xử lý)
        // Hoặc có thể tạo một empty part để đảm bảo API không lỗi
        if (imageParts.isEmpty()) {
            Log.w("IMAGE_WARNING", "Creating product without images");
            // Có thể thêm một empty part nếu backend yêu cầu
            // Hoặc để trống và để backend xử lý
        }

        // Gọi API
        Call<ProductDetailResponse> call = apiService.createProduct(productDtoBody, imageParts);
        call.enqueue(new Callback<ProductDetailResponse>() {
            @Override
            public void onResponse(Call<ProductDetailResponse> call, Response<ProductDetailResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminProductCreateActivity.this, 
                            "Tạo sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại danh sách
                } else {
                    Toast.makeText(AdminProductCreateActivity.this, 
                            "Không thể tạo sản phẩm", Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<ProductDetailResponse> call, Throwable t) {
                Toast.makeText(AdminProductCreateActivity.this, 
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("NETWORK_ERROR", "Error: " + t.getMessage());
            }
        });
    }

    private void removeImage(Uri uri) {
        selectedImageUris.remove(uri);
        imagesAdapter.notifyDataSetChanged();
    }

    /**
     * Tạo ảnh placeholder từ drawable resources để test khi không có ảnh trong gallery
     */
    private void usePlaceholderImage() {
        try {
            // Tạo file tạm thời
            File tempFile = File.createTempFile("placeholder_image", ".png", getCacheDir());
            
            // Convert drawable thành bitmap
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            if (bitmap == null) {
                // Fallback: tạo bitmap đơn giản nếu không load được
                bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
                bitmap.eraseColor(0xFFE0E0E0); // Màu xám
            }
            
            // Lưu bitmap vào file
            java.io.FileOutputStream fos = new java.io.FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            
            // Tạo Uri từ file
            Uri placeholderUri = Uri.fromFile(tempFile);
            selectedImageUris.add(placeholderUri);
            imagesAdapter.notifyDataSetChanged();
            
            Toast.makeText(this, "Đã thêm ảnh mẫu", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("PLACEHOLDER_ERROR", "Error creating placeholder: " + e.getMessage());
            Toast.makeText(this, "Không thể tạo ảnh mẫu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Adapter để hiển thị danh sách ảnh đã chọn
    private static class SelectedImagesAdapter extends RecyclerView.Adapter<SelectedImagesAdapter.ImageViewHolder> {
        private List<Uri> imageUris;
        private OnImageRemoveListener removeListener;

        interface OnImageRemoveListener {
            void onRemove(Uri uri);
        }

        public SelectedImagesAdapter(List<Uri> imageUris, OnImageRemoveListener removeListener) {
            this.imageUris = imageUris;
            this.removeListener = removeListener;
        }

        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_selected_image, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ImageViewHolder holder, int position) {
            Uri imageUri = imageUris.get(position);
            Glide.with(holder.itemView.getContext())
                    .load(imageUri)
                    .centerCrop()
                    .into(holder.ivImage);

            holder.btnRemove.setOnClickListener(v -> {
                if (removeListener != null) {
                    removeListener.onRemove(imageUri);
                }
            });
        }

        @Override
        public int getItemCount() {
            return imageUris.size();
        }

        static class ImageViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImage;
            Button btnRemove;

            ImageViewHolder(View itemView) {
                super(itemView);
                ivImage = itemView.findViewById(R.id.ivImage);
                btnRemove = itemView.findViewById(R.id.btnRemove);
            }
        }
    }
}
