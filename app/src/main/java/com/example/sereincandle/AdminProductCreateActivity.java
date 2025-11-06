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
import android.widget.ProgressBar;
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
    private ProgressBar progressBar;

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

        // Setup nút Back
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

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
        progressBar = findViewById(R.id.progressBar);

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
        boolean isValid = true;
        
        // Xóa tất cả lỗi cũ
        clearAllErrors();
        
        // 1. Kiểm tra Tên sản phẩm (Bắt buộc)
        String productName = etProductName.getText().toString().trim();
        if (productName.isEmpty()) {
            etProductName.setError("Tên sản phẩm không được để trống");
            etProductName.requestFocus();
            isValid = false;
        } else if (productName.length() < 3) {
            etProductName.setError("Tên sản phẩm phải có ít nhất 3 ký tự");
            etProductName.requestFocus();
            isValid = false;
        } else if (productName.length() > 200) {
            etProductName.setError("Tên sản phẩm không được vượt quá 200 ký tự");
            etProductName.requestFocus();
            isValid = false;
        }
        
        // 2. Kiểm tra Mô tả chi tiết (Bắt buộc)
        String description = etDescription.getText().toString().trim();
        if (description.isEmpty()) {
            etDescription.setError("Mô tả chi tiết không được để trống");
            if (isValid) {
                etDescription.requestFocus();
            }
            isValid = false;
        } else if (description.length() < 10) {
            etDescription.setError("Mô tả chi tiết phải có ít nhất 10 ký tự");
            if (isValid) {
                etDescription.requestFocus();
            }
            isValid = false;
        }
        
        // 3. Kiểm tra Giá (Bắt buộc)
        String priceText = etPrice.getText().toString().trim();
        if (priceText.isEmpty()) {
            etPrice.setError("Giá không được để trống");
            if (isValid) {
                etPrice.requestFocus();
            }
            isValid = false;
        } else {
            try {
                double price = Double.parseDouble(priceText);
                if (price < 0) {
                    etPrice.setError("Giá không được âm");
                    if (isValid) {
                        etPrice.requestFocus();
                    }
                    isValid = false;
                } else if (price == 0) {
                    etPrice.setError("Giá phải lớn hơn 0");
                    if (isValid) {
                        etPrice.requestFocus();
                    }
                    isValid = false;
                } else if (price > 100000000) {
                    etPrice.setError("Giá không được vượt quá 100,000,000 VNĐ");
                    if (isValid) {
                        etPrice.requestFocus();
                    }
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                etPrice.setError("Giá không hợp lệ. Vui lòng nhập số (ví dụ: 250000)");
                if (isValid) {
                    etPrice.requestFocus();
                }
                isValid = false;
            }
        }
        
        // 4. Kiểm tra Danh mục (Bắt buộc)
        if (spCategory.getSelectedItem() == null) {
            Toast.makeText(this, "Vui lòng chọn danh mục sản phẩm", Toast.LENGTH_LONG).show();
            isValid = false;
        }
        
        // 5. Cảnh báo về ảnh (không bắt buộc)
        if (selectedImageUris.isEmpty()) {
            // Chỉ cảnh báo, không chặn
            Toast.makeText(this, "⚠️ Cảnh báo: Sản phẩm chưa có ảnh. Bạn có thể thêm ảnh sau.", 
                    Toast.LENGTH_LONG).show();
        }
        
        // Hiển thị tổng hợp lỗi nếu có
        if (!isValid) {
            Toast.makeText(this, "Vui lòng sửa các lỗi được đánh dấu ở trên", Toast.LENGTH_LONG).show();
        }
        
        return isValid;
    }
    
    /**
     * Xóa tất cả lỗi hiển thị trên các trường input
     */
    private void clearAllErrors() {
        etProductName.setError(null);
        etSku.setError(null);
        etShortDescription.setError(null);
        etDescription.setError(null);
        etIngredients.setError(null);
        etBurnTime.setError(null);
        etPrice.setError(null);
    }

    private void createProduct() {
        // Disable button và hiển thị loading
        btnCreateProduct.setEnabled(false);
        btnCancel.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        
        // Tạo ProductRequest từ form
        ProductRequest request = new ProductRequest();
        request.setName(etProductName.getText().toString().trim());
        
        // Chỉ set các field optional nếu có giá trị
        String sku = etSku.getText().toString().trim();
        if (!sku.isEmpty()) {
            request.setSku(sku);
        }
        
        String shortDesc = etShortDescription.getText().toString().trim();
        if (!shortDesc.isEmpty()) {
            request.setShortDescription(shortDesc);
        }
        
        request.setDescription(etDescription.getText().toString().trim());
        
        String ingredients = etIngredients.getText().toString().trim();
        if (!ingredients.isEmpty()) {
            request.setIngredients(ingredients);
        }
        
        String burnTime = etBurnTime.getText().toString().trim();
        if (!burnTime.isEmpty()) {
            request.setBurnTime(burnTime);
        }
        
        request.setPrice(Double.parseDouble(etPrice.getText().toString().trim()));
        
        Category selectedCategory = (Category) spCategory.getSelectedItem();
        request.setCategoryId(selectedCategory.getCategoryId());
        
        Log.d("CREATE_PRODUCT", "Creating product: " + request.getName());

        // Tạo RequestBody cho từng field riêng lẻ (theo tài liệu BE - FormData)
        // Backend C# nhận FormData với các field riêng lẻ, không phải JSON string
        
        // Required fields
        RequestBody nameBody = RequestBody.create(
                MediaType.parse("text/plain"),
                request.getName() != null ? request.getName() : ""
        );
        
        RequestBody descriptionBody = RequestBody.create(
                MediaType.parse("text/plain"),
                request.getDescription() != null ? request.getDescription() : ""
        );
        
        RequestBody priceBody = RequestBody.create(
                MediaType.parse("text/plain"),
                String.valueOf(request.getPrice())
        );
        
        RequestBody categoryIdBody = RequestBody.create(
                MediaType.parse("text/plain"),
                String.valueOf(request.getCategoryId())
        );
        
        // Optional fields - luôn gửi, dùng empty string nếu không có giá trị
        // Retrofit @Part không hỗ trợ required = false, nên phải luôn gửi
        RequestBody skuBody = RequestBody.create(
                MediaType.parse("text/plain"),
                request.getSku() != null ? request.getSku() : ""
        );
        
        RequestBody shortDescBody = RequestBody.create(
                MediaType.parse("text/plain"),
                request.getShortDescription() != null ? request.getShortDescription() : ""
        );
        
        RequestBody ingredientsBody = RequestBody.create(
                MediaType.parse("text/plain"),
                request.getIngredients() != null ? request.getIngredients() : ""
        );
        
        RequestBody burnTimeBody = RequestBody.create(
                MediaType.parse("text/plain"),
                request.getBurnTime() != null ? request.getBurnTime() : ""
        );
        
        Log.d("CREATE_PRODUCT", "Form fields: Name=" + request.getName() + 
                ", Description=" + request.getDescription() + 
                ", Price=" + request.getPrice() + 
                ", CategoryId=" + request.getCategoryId());

        // Tạo MultipartBody.Part từ các Uri ảnh
        List<MultipartBody.Part> imageParts = new ArrayList<>();
        Log.d("CREATE_PRODUCT", "Processing " + selectedImageUris.size() + " images");
        
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
                    Log.d("CREATE_PRODUCT", "Added image: " + tempFile.getName());
                }
            } catch (Exception e) {
                Log.e("IMAGE_ERROR", "Error processing image: " + e.getMessage(), e);
                Toast.makeText(this, "Lỗi xử lý ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        // Nếu không có ảnh, vẫn có thể tạo sản phẩm (backend sẽ xử lý)
        if (imageParts.isEmpty()) {
            Log.w("IMAGE_WARNING", "Creating product without images");
        }

        // Gọi API với các form fields riêng lẻ
        Log.d("CREATE_PRODUCT", "Calling API with " + imageParts.size() + " images");
        Call<ProductDetailResponse> call = apiService.createProduct(
                nameBody,
                descriptionBody,
                priceBody,
                categoryIdBody,
                skuBody,
                shortDescBody,
                ingredientsBody,
                burnTimeBody,
                imageParts
        );
        call.enqueue(new Callback<ProductDetailResponse>() {
            @Override
            public void onResponse(Call<ProductDetailResponse> call, Response<ProductDetailResponse> response) {
                // Enable lại button và ẩn loading
                btnCreateProduct.setEnabled(true);
                btnCancel.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    ProductDetailResponse productResponse = response.body();
                    if (productResponse.getData() != null) {
                        Log.d("CREATE_PRODUCT", "Product created successfully: ID=" + productResponse.getData().getProductId() + ", Name=" + productResponse.getData().getName());
                    } else {
                        Log.d("CREATE_PRODUCT", "Product created successfully (no data in response)");
                    }
                    Toast.makeText(AdminProductCreateActivity.this, 
                            "Tạo sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại danh sách
                } else {
                    String errorMessage = "Không thể tạo sản phẩm";
                    Log.e("API_ERROR", "Error code: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e("API_ERROR", "Error body: " + errorBody);
                            
                            // Cố gắng parse error message từ JSON
                            if (errorBody.contains("message") || errorBody.contains("Message")) {
                                errorMessage = "Lỗi: " + errorBody;
                            }
                        }
                    } catch (Exception e) {
                        Log.e("API_ERROR", "Error reading error body: " + e.getMessage());
                    }
                    
                    // Hiển thị thông báo lỗi chi tiết hơn
                    if (response.code() == 400) {
                        errorMessage = "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại.";
                    } else if (response.code() == 401) {
                        errorMessage = "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.";
                    } else if (response.code() == 403) {
                        errorMessage = "Bạn không có quyền thực hiện thao tác này.";
                    } else if (response.code() >= 500) {
                        errorMessage = "Lỗi server. Vui lòng thử lại sau.";
                    }
                    
                    Toast.makeText(AdminProductCreateActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ProductDetailResponse> call, Throwable t) {
                // Enable lại button và ẩn loading
                btnCreateProduct.setEnabled(true);
                btnCancel.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                
                String errorMsg = "Lỗi kết nối";
                if (t.getMessage() != null) {
                    errorMsg += ": " + t.getMessage();
                }
                Toast.makeText(AdminProductCreateActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                Log.e("NETWORK_ERROR", "Error creating product", t);
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
