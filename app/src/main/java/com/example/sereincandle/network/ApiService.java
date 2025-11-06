package com.example.sereincandle.network;

import com.example.sereincandle.LoginRequest;
import com.example.sereincandle.LoginResponse;
import com.example.sereincandle.RegisterRequest;
import com.example.sereincandle.models.CartItemRequest;
import com.example.sereincandle.models.CartResponse;
import com.example.sereincandle.models.Category;
import com.example.sereincandle.models.ApiResponse;
import com.example.sereincandle.models.Order;
import com.example.sereincandle.models.OrderRequest;
import com.example.sereincandle.models.OrderStatusRequest;
import com.example.sereincandle.models.ProductDetailResponse;
import com.example.sereincandle.models.ProductListResponse;
import com.example.sereincandle.models.ProductRequest;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // ========== AUTH ENDPOINTS ==========

    /**
     * Đăng nhập
     */
    @POST("Auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    /**
     * Đăng ký Customer
     * Endpoint: POST /api/auth/register
     * Request Body: RegisterRequest (email, password, fullName, phone, dateOfBirth)
     * Response: ApiResponse với success, message, data
     * Status: 201 Created (thành công), 400 Bad Request (validation/email/phone đã tồn tại), 500 Server Error
     */
    @POST("auth/register")
    Call<ApiResponse<Void>> register(@Body RegisterRequest request);

    @GET("Product")
    Call<ProductListResponse> getProducts(
            @Query("pageNumber") int pageNumber,
            @Query("pageSize") int pageSize
    );
    @GET("Product/{id}")
    Call<ProductDetailResponse> getProductDetail(@Path("id") int productId);

    @POST("carts/items")
    Call<Void> addItemToCart(@Body CartItemRequest request);
    @GET("carts")
    Call<CartResponse> getCart();

    @DELETE("carts/items/{itemId}")
    Call<Void> deleteCartItem(@Path("itemId") int itemId);
    @POST("Order")
    Call<Void> placeOrder(@Body OrderRequest request);

    // ========== ADMIN ENDPOINTS - PRODUCT MANAGEMENT ==========

    /**
     * Tạo sản phẩm mới (Admin only - multipart/form-data)
     * Content-Type: multipart/form-data
     * Form fields (theo tài liệu BE):
     *   - Name, SKU, ShortDescription, Description, Ingredients, BurnTime, Price, CategoryId
     *   - Attributes[0].AttributeId, Attributes[0].Value (nếu có)
     *   - images: Multiple image files
     */
    @Multipart
    @POST("Product")
    Call<ProductDetailResponse> createProduct(
            @Part("Name") RequestBody name,
            @Part("Description") RequestBody description,
            @Part("Price") RequestBody price,
            @Part("CategoryId") RequestBody categoryId,
            @Part("SKU") RequestBody sku,
            @Part("ShortDescription") RequestBody shortDescription,
            @Part("Ingredients") RequestBody ingredients,
            @Part("BurnTime") RequestBody burnTime,
            @Part List<MultipartBody.Part> images  // Multiple images
    );

    /**
     * Cập nhật sản phẩm
     */
    @PUT("Product/{id}")
    Call<ProductDetailResponse> updateProduct(
            @Path("id") int productId,
            @Body ProductRequest request
    );

    /**
     * Cập nhật hình ảnh sản phẩm (multipart/form-data)
     */
    @Multipart
    @PUT("Product/{id}/images")
    Call<Void> updateProductImages(
            @Path("id") int productId,
            @Part List<MultipartBody.Part> images
    );

    /**
     * Xóa sản phẩm (soft delete)
     */
    @DELETE("Product/{id}")
    Call<Void> deleteProduct(@Path("id") int productId);

    // ========== ADMIN ENDPOINTS - CATEGORY MANAGEMENT ==========

    /**
     * Lấy tất cả danh mục (dùng cho Spinner khi tạo/sửa sản phẩm)
     */
    @GET("Category")
    Call<List<Category>> getAllCategories();

    // ========== ADMIN ENDPOINTS - ORDER MANAGEMENT ==========

    /**
     * Lấy danh sách đơn hàng (Admin)
     * ⚠️ Response trả về List<Order> trực tiếp, không có pagination
     */
    @GET("Order/admin")
    Call<List<Order>> getAdminOrders();

    /**
     * Cập nhật trạng thái đơn hàng
     * Request body: { "statusId": int }
     * Values: 1: Pending, 2: Preparing, 3: Shipped, 4: Completed, 5: Cancelled
     * ⚠️ Lưu ý: Không thể thay đổi trạng thái đơn hàng đã hủy
     */
    @PUT("Order/{orderId}/status")
    Call<Void> updateOrderStatus(
            @Path("orderId") int orderId,
            @Body OrderStatusRequest request
    );
}