# 📋 KẾ HOẠCH XÂY DỰNG LUỒNG ADMIN

## 🎯 MỤC TIÊU

Xây dựng luồng Admin để quản lý:

- ✅ Sản phẩm (CRUD: Create, Read, Update, Delete)
- ✅ Đơn hàng (Xem danh sách, cập nhật trạng thái)
- ✅ Người dùng (Xem danh sách, quản lý)
- ✅ Thống kê/Báo cáo (tùy chọn)

---

## 📊 PHÂN TÍCH LUỒNG CUSTOMER HIỆN TẠI

### **Kiến trúc Customer:**

```
MainActivity (Login)
    ↓ (check token only)
HomePageActivity
    ├─► ProductListActivity (Xem sản phẩm)
    │   ├─► ProductDetailActivity (Xem chi tiết)
    │   └─► [Thêm vào giỏ]
    │
    └─► CartActivity (Giỏ hàng)
        └─► CheckoutActivity (Đặt hàng)
```

### **Điểm cần cải thiện:**

1. **MainActivity** - Hiện tại chỉ lưu token, chưa check role
2. **HomePageActivity** - Chỉ có nút cho Customer
3. Cần thêm logic phân biệt Customer vs Admin sau khi login

---

## 🏗️ THIẾT KẾ LUỒNG ADMIN

### **Sơ đồ luồng Admin:**

```
MainActivity (Login)
    ↓ (check token + roleName)
    ↓
    ├─► [roleName == "Customer"]
    │   └─► HomePageActivity (Customer) [Đã có]
    │
    └─► [roleName == "Admin"]
        └─► AdminHomeActivity (Màn hình Admin)
            │
            ├─► [Nút: Quản lý Sản phẩm]
            │   └─► AdminProductListActivity
            │       ├─► AdminProductDetailActivity (Xem/Sửa)
            │       └─► AdminProductCreateActivity (Thêm mới)
            │
            ├─► [Nút: Quản lý Đơn hàng]
            │   └─► AdminOrderListActivity
            │       └─► AdminOrderDetailActivity (Xem/Cập nhật trạng thái)
            │
            └─► [Nút: Quản lý Người dùng] (Optional)
                └─► AdminUserListActivity
```

---

## 📝 CHI TIẾT CÁC MÀN HÌNH ADMIN

### **1. AdminHomeActivity (Trang chủ Admin)**

**Chức năng:**

- Dashboard Admin
- Hiển thị thông tin admin (tên, email)
- Menu điều hướng đến các chức năng quản lý

**UI Components:**

- TextView: "Xin chào, [Admin Name]"
- Button: "Quản lý Sản phẩm"
- Button: "Quản lý Đơn hàng"
- Button: "Quản lý Người dùng" (optional)
- Button: "Đăng xuất"

**Navigation:**

- Quản lý Sản phẩm → `AdminProductListActivity`
- Quản lý Đơn hàng → `AdminOrderListActivity`
- Quản lý Người dùng → `AdminUserListActivity`
- Đăng xuất → Clear token → `MainActivity`

---

### **2. AdminProductListActivity (Danh sách sản phẩm Admin)**

**Chức năng:**

- Hiển thị tất cả sản phẩm
- Tìm kiếm/Lọc sản phẩm
- Thêm sản phẩm mới
- Xem/Sửa/Xóa sản phẩm

**UI Components:**

- RecyclerView: Danh sách sản phẩm
- FloatingActionButton: Thêm sản phẩm mới
- SearchView: Tìm kiếm sản phẩm
- Mỗi item có: Ảnh, Tên, Giá, Trạng thái (Active/Inactive), [Nút Sửa], [Nút Xóa]

**Actions:**

- Nhấn item → Xem/Sửa (`AdminProductDetailActivity`)
- Nhấn FAB → Thêm mới (`AdminProductCreateActivity`)
- Nhấn "Xóa" → Xóa sản phẩm (có confirm dialog)

**API:**

- `GET /api/Product` - Lấy danh sách (dùng chung với Customer, có pagination)
- `DELETE /api/Product/{id}` - Xóa sản phẩm (soft delete)

---

### **3. AdminProductDetailActivity (Chi tiết/Sửa sản phẩm)**

**Chức năng:**

- Xem chi tiết sản phẩm
- Chỉnh sửa thông tin sản phẩm
- Upload/Cập nhật ảnh sản phẩm

**UI Components:**

- ImageView: Ảnh sản phẩm
- EditText: Tên, Mô tả, Giá, SKU, Thành phần, Thời gian cháy...
- CheckBox/Switch: Trạng thái Active/Inactive
- Spinner: Chọn danh mục
- Button: "Lưu thay đổi"
- Button: "Xóa sản phẩm"

**Actions:**

- Load sản phẩm hiện tại
- Cho phép sửa tất cả fields
- Nhấn "Lưu" → `PUT /api/Admin/Products/{id}`
- Nhấn "Xóa" → Confirm → `DELETE /api/Admin/Products/{id}`

**API:**

- `GET /api/Product/{id}` - Lấy chi tiết (dùng chung với Customer)
- `PUT /api/Product/{id}` - Cập nhật thông tin sản phẩm
- `PUT /api/Product/{id}/images` - Cập nhật hình ảnh (multipart/form-data)
- `DELETE /api/Product/{id}` - Xóa sản phẩm (soft delete)

---

### **4. AdminProductCreateActivity (Thêm sản phẩm mới)**

**Chức năng:**

- Tạo sản phẩm mới
- Upload ảnh sản phẩm

**UI Components:**

- ImageView: Preview ảnh
- Button: "Chọn ảnh" (File picker hoặc Camera)
- EditText: Tên, Mô tả, Giá, SKU, Thành phần, Thời gian cháy
- Spinner: Chọn danh mục
- CheckBox: Trạng thái Active
- Button: "Tạo sản phẩm"
- Button: "Hủy"

**Actions:**

- Nhấn "Chọn ảnh" → Chọn từ gallery hoặc chụp ảnh
- Nhấn "Tạo sản phẩm" → Validate → `POST /api/Admin/Products`
- Nhấn "Hủy" → Back về danh sách

**API:**

- `POST /api/Product` - Tạo mới (Content-Type: multipart/form-data)
  - Form field: `productDto` (JSON string)
  - Form field: `images` (IFormFileCollection - multiple images)
- ⚠️ **Lưu ý:** Cần dùng `@Multipart` và `@Part` trong Retrofit

---

### **5. AdminOrderListActivity (Danh sách đơn hàng)**

**Chức năng:**

- Hiển thị tất cả đơn hàng
- Lọc theo trạng thái (Pending, Confirmed, Shipping, Delivered, Cancelled)
- Tìm kiếm đơn hàng (theo ID, customer name, phone)

**UI Components:**

- Spinner: Lọc theo trạng thái
- SearchView: Tìm kiếm
- RecyclerView: Danh sách đơn hàng
- Mỗi item hiển thị:
  - Order ID
  - Tên khách hàng
  - Số điện thoại
  - Tổng tiền
  - Trạng thái (màu sắc khác nhau)
  - Ngày đặt hàng

**Actions:**

- Nhấn item → Xem chi tiết (`AdminOrderDetailActivity`)
- Pull to refresh: Reload danh sách

**API:**

- `GET /api/Order/admin` - Lấy tất cả đơn hàng trong hệ thống (dành cho Admin)
- ⚠️ **Lưu ý:** Response trả về array trực tiếp, không có pagination trong response

---

### **6. AdminOrderDetailActivity (Chi tiết đơn hàng)**

**Chức năng:**

- Xem chi tiết đơn hàng
- Cập nhật trạng thái đơn hàng
- Xem thông tin khách hàng
- Xem danh sách sản phẩm trong đơn

**UI Components:**

- TextView: Order ID, Ngày đặt
- TextView: Thông tin khách hàng (Tên, SĐT, Địa chỉ)
- RecyclerView: Danh sách sản phẩm trong đơn
- TextView: Tổng tiền
- Spinner: Trạng thái đơn hàng
- Button: "Cập nhật trạng thái"
- TextView: Ghi chú (nếu có)

**Actions:**

- Load chi tiết đơn hàng
- Chọn trạng thái mới từ Spinner
- Nhấn "Cập nhật" → `PUT /api/Admin/Orders/{id}/status`

**API:**

- `GET /api/Order/admin` - Lấy danh sách (mỗi order có items đầy đủ)
- `PUT /api/Order/{orderId}/status` - Cập nhật trạng thái đơn hàng
  - Request Body: `{ "statusId": int }`
  - ⚠️ **Lưu ý:** Không thể thay đổi trạng thái đơn hàng đã hủy

---

### **7. AdminUserListActivity (Danh sách người dùng - Optional)**

**Chức năng:**

- Xem danh sách người dùng
- Tìm kiếm người dùng
- Xem chi tiết người dùng

**UI Components:**

- SearchView: Tìm kiếm
- RecyclerView: Danh sách users
- Mỗi item: Tên, Email, Role, Trạng thái

**Actions:**

- Nhấn item → Xem chi tiết (có thể mở dialog hoặc activity mới)

**API:**

- `GET /api/Admin/Users` - Lấy danh sách

---

## 🔧 CÁC API ENDPOINTS CẦN THÊM

### **Product Management:**

```java
// Thêm vào ApiService.java
// ⚠️ LƯU Ý: API endpoints KHÔNG có prefix "Admin/", dùng chung với Customer

// Lấy danh sách sản phẩm (dùng chung với Customer)
@GET("Product")
Call<ProductListResponse> getProducts(
    @Query("pageNumber") int pageNumber,
    @Query("pageSize") int pageSize
);

// Lấy chi tiết sản phẩm (dùng chung với Customer)
@GET("Product/{id}")
Call<ProductDetailResponse> getProductDetail(@Path("id") int productId);

// Tạo sản phẩm mới (Admin only - multipart/form-data)
@Multipart
@POST("Product")
Call<ProductDetailResponse> createProduct(
    @Part("productDto") RequestBody productDtoJson,  // JSON string của ProductRequest
    @Part List<MultipartBody.Part> images              // Multiple images
);

// Cập nhật sản phẩm
@PUT("Product/{id}")
Call<ProductDetailResponse> updateProduct(
    @Path("id") int productId,
    @Body ProductRequest request
);

// Cập nhật hình ảnh sản phẩm
@Multipart
@PUT("Product/{id}/images")
Call<Void> updateProductImages(
    @Path("id") int productId,
    @Part List<MultipartBody.Part> images
);

// Xóa sản phẩm (soft delete)
@DELETE("Product/{id}")
Call<Void> deleteProduct(@Path("id") int productId);
```

### **Order Management:**

```java
// Lấy danh sách đơn hàng (Admin)
// ⚠️ Response trả về List<Order> trực tiếp, không có pagination
@GET("Order/admin")
Call<List<Order>> getAdminOrders();

// Cập nhật trạng thái đơn hàng
@PUT("Order/{orderId}/status")
Call<Void> updateOrderStatus(
    @Path("orderId") int orderId,
    @Body OrderStatusRequest request
);
```

### **Category Management (Cho Admin quản lý sản phẩm):**

```java
// Generic Controller pattern - CRUD đầy đủ

// Lấy tất cả danh mục
@GET("Category")
Call<List<Category>> getAllCategories();

// Lấy danh mục theo ID
@GET("Category/{id}")
Call<Category> getCategoryById(@Path("id") int categoryId);

// Tạo danh mục mới
@POST("Category")
Call<Category> createCategory(@Body Category category);

// Cập nhật danh mục
@PUT("Category/{id}")
Call<Void> updateCategory(@Path("id") int categoryId, @Body Category category);
```

### **ProductAttribute Management (Cho Admin quản lý thuộc tính):**

```java
// Generic Controller pattern

@GET("ProductAttribute")
Call<List<ProductAttribute>> getAllProductAttributes();

@GET("ProductAttribute/{id}")
Call<ProductAttribute> getProductAttributeById(@Path("id") int id);

@POST("ProductAttribute")
Call<ProductAttribute> createProductAttribute(@Body ProductAttribute attribute);

@PUT("ProductAttribute/{id}")
Call<Void> updateProductAttribute(@Path("id") int id, @Body ProductAttribute attribute);
```

### **Voucher Management (Optional - Future):**

```java
// Generic Controller pattern

@GET("Voucher")
Call<List<Voucher>> getAllVouchers();

@POST("Voucher")
Call<Voucher> createVoucher(@Body Voucher voucher);

@PUT("Voucher/{id}")
Call<Void> updateVoucher(@Path("id") int id, @Body Voucher voucher);
```

### **Auth - Đăng ký nhân viên (Optional):**

```java
@POST("Auth/register/staff")
Call<Void> registerStaff(@Body StaffRegisterRequest request);

// StaffRegisterRequest:
// {
//   "email": string (required),
//   "password": string (required),
//   "fullName": string (required),
//   "phone": string (required),
//   "dateOfBirth": string (required, yyyy-MM-dd),
//   "employeeCode": string (required),
//   "position": string (required)
// }
```

---

## 📦 MODELS CẦN TẠO MỚI

### **1. ProductRequest.java / InsertProductDto.java**

```java
// Model để tạo/cập nhật sản phẩm
// Mapping với SQL: Product table
// Mapping với Backend: InsertProductDto
public class ProductRequest {
    private String name;              // required
    private String sku;               // optional
    private String shortDescription;  // optional
    private String description;       // required
    private String ingredients;       // optional
    private String burnTime;          // optional
    private double price;             // required (decimal)
    private int categoryId;           // required
    private List<ProductAttributeValue> attributes;  // optional
    // ... getters, setters
}

// Model cho attributes
public class ProductAttributeValue {
    private int attributeId;  // required
    private String value;     // required
}
```

### **2. OrderListResponse.java**

```java
// Response danh sách đơn hàng
public class OrderListResponse {
    private List<Order> data;
    private int totalCount;
    private int pageNumber;
    private int pageSize;
    // ... getters
}
```

### **3. Order.java (Model đơn hàng)**

```java
// Mapping với Backend response: GET /api/Order/admin
public class Order {
    private int orderId;
    private String orderCode;
    private String statusName;        // 'Pending', 'Preparing', 'Shipped', 'Completed', 'Cancelled'
    private String paymentMethodName; // 'COD', 'VNPay', 'MoMo', 'ZaloPay', 'BankTransfer'
    private Integer userId;            // nullable
    private String customerFullName;
    private String customerEmail;
    private String recipientAddress;  // Địa chỉ nhận hàng (full address string)
    private double totalAmount;
    private double discountAmount;
    private double shippingFee;
    private Date createdAt;            // DateTime
    private List<OrderItem> items;     // OrderItemDetailDto (có đầy đủ thông tin)
    // ... getters
}
```

### **4. OrderItem.java**

```java
// Mapping với SQL: OrderItem + Product (JOIN)
public class OrderItem {
    private int orderItemId;        // OrderItem.OrderItemId
    private int productId;         // OrderItem.ProductId
    private String productName;    // Product.Name (JOIN)
    private String productSku;     // Product.SKU (JOIN)
    private int quantity;          // OrderItem.Quantity
    private double unitPrice;      // OrderItem.UnitPrice
    private double totalPrice;     // OrderItem.TotalPrice (calculated: Quantity * UnitPrice)
    private String productImageUrl; // ProductImage.ImageUrl (JOIN, primary image)
    // ... getters
}
```

### **5. OrderDetailResponse.java**

```java
public class OrderDetailResponse {
    private boolean success;
    private String message;
    private Order data;  // Order đầy đủ với items
    // ... getters
}
```

### **5b. Category.java (Cho Admin quản lý sản phẩm)**

```java
// Mapping với SQL: Category table
public class Category {
    private int categoryId;          // Category.CategoryId
    private String name;             // Category.Name
    private String slug;             // Category.Slug
    private String description;      // Category.Description
    private Integer parentCategoryId; // Category.ParentCategoryId (nullable, nested categories)
    // ... getters
}
```

### **5c. OrderStatus.java (Enum/List)**

```java
// Mapping với SQL: OrderStatus table
public class OrderStatus {
    private int statusId;            // OrderStatus.StatusId
    private String statusName;      // OrderStatus.StatusName
    // Values: 'Pending', 'Preparing', 'Shipped', 'Completed', 'Cancelled'
    // ... getters
}
```

### **6. OrderStatusRequest.java**

```java
// Request để cập nhật trạng thái đơn hàng
// Mapping với Backend: PUT /api/Order/{orderId}/status
public class OrderStatusRequest {
    private int statusId;  // OrderStatus.StatusId (required)
    // Values: 1: Pending, 2: Preparing, 3: Shipped, 4: Completed, 5: Cancelled

    public OrderStatusRequest(int statusId) {
        this.statusId = statusId;
    }
}
```

### **7. UserListResponse.java (Optional)**

```java
public class UserListResponse {
    private List<User> data;
    private int totalCount;
    // ... getters
}
```

### **8. User.java (Optional)**

```java
public class User {
    private int userId;
    private String email;
    private String fullName;
    private String roleName;
    private boolean isActive;
    // ... getters
}
```

---

## 🔄 CẬP NHẬT CODE HIỆN TẠI

### **1. Cập nhật MainActivity.java**

**Hiện tại:**

```java
// Sau khi login thành công, chỉ lưu token và chuyển sang HomePageActivity
Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
startActivity(intent);
```

**Cần sửa thành:**

```java
// Sau khi login thành công:
LoginResponse loginResponse = response.body();
String token = loginResponse.getToken();
UserData userData = loginResponse.getData();

// Lưu token
SharedPreferences sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
SharedPreferences.Editor editor = sharedPref.edit();
editor.putString("JWT_TOKEN", token);

// Lưu role để dùng sau này
if (userData != null) {
    String roleName = userData.getRoleName();
    editor.putString("USER_ROLE", roleName); // Lưu role
    editor.putString("USER_NAME", userData.getFullName()); // Lưu tên (optional)
    editor.putString("USER_EMAIL", userData.getEmail()); // Lưu email (optional)
}

editor.apply();

// Điều hướng theo role
if (userData != null && "Admin".equalsIgnoreCase(userData.getRoleName())) {
    // Admin → AdminHomeActivity
    Intent intent = new Intent(MainActivity.this, AdminHomeActivity.class);
    startActivity(intent);
} else {
    // Customer → HomePageActivity (như cũ)
    Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
    startActivity(intent);
}

finish();
```

---

### **2. Tạo Utility Class: SessionManager.java**

**Chức năng:** Quản lý session (token, role, user info)

```java
package com.example.sereincandle.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "AppPrefs";
    private static final String KEY_TOKEN = "JWT_TOKEN";
    private static final String KEY_ROLE = "USER_ROLE";
    private static final String KEY_USER_NAME = "USER_NAME";
    private static final String KEY_USER_EMAIL = "USER_EMAIL";

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public void saveToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public String getToken() {
        return sharedPref.getString(KEY_TOKEN, null);
    }

    public void saveRole(String role) {
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    public String getRole() {
        return sharedPref.getString(KEY_ROLE, null);
    }

    public boolean isAdmin() {
        return "Admin".equalsIgnoreCase(getRole());
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }

    // ... các methods khác
}
```

---

## 📋 KẾ HOẠCH IMPLEMENT THEO TỪNG GIAI ĐOẠN

### **GIAI ĐOẠN 1: Setup cơ bản (2-3 giờ)**

#### **Bước 1.1: Cập nhật MainActivity**

- [ ] Cập nhật logic login để check role
- [ ] Lưu role vào SharedPreferences
- [ ] Điều hướng Admin → AdminHomeActivity
- [ ] Điều hướng Customer → HomePageActivity (như cũ)

#### **Bước 1.2: Tạo SessionManager**

- [ ] Tạo file `utils/SessionManager.java`
- [ ] Implement các methods cơ bản
- [ ] Test lưu/đọc token, role

#### **Bước 1.3: Tạo AdminHomeActivity**

- [ ] Tạo layout `activity_admin_home.xml`
- [ ] Tạo `AdminHomeActivity.java`
- [ ] Hiển thị thông tin admin
- [ ] Thêm các nút điều hướng
- [ ] Implement nút đăng xuất

**File cần tạo:**

```
app/src/main/
├── java/com/example/sereincandle/
│   ├── utils/
│   │   └── SessionManager.java (NEW)
│   └── AdminHomeActivity.java (NEW)
└── res/layout/
    └── activity_admin_home.xml (NEW)
```

**Test:**

- [ ] Đăng nhập với tài khoản Admin → Chuyển sang AdminHomeActivity
- [ ] Đăng nhập với tài khoản Customer → Chuyển sang HomePageActivity (như cũ)

---

### **GIAI ĐOẠN 2: Quản lý Sản phẩm (4-6 giờ)**

#### **Bước 2.1: Tạo Models**

- [ ] Tạo `ProductRequest.java` (với `List<ProductAttributeValue> attributes`)
- [ ] Tạo `ProductAttributeValue.java`
- [ ] Tạo `Category.java` (cho dropdown khi tạo/sửa sản phẩm)
- [ ] Update `ApiService.java` (thêm endpoints Product Admin - dùng multipart cho POST)

#### **Bước 2.2: AdminProductListActivity**

- [ ] Tạo layout `activity_admin_product_list.xml`
- [ ] Tạo `AdminProductListActivity.java`
- [ ] Implement RecyclerView hiển thị danh sách
- [ ] Implement FAB "Thêm sản phẩm"
- [ ] Implement nút "Xóa" trên mỗi item
- [ ] Implement SearchView (optional)

**File cần tạo:**

```
├── models/
│   └── ProductRequest.java (NEW)
└── AdminProductListActivity.java (NEW)
└── res/layout/
    └── activity_admin_product_list.xml (NEW)
```

#### **Bước 2.3: AdminProductDetailActivity (Xem/Sửa)**

- [ ] Tạo layout `activity_admin_product_detail.xml`
- [ ] Tạo `AdminProductDetailActivity.java`
- [ ] Load chi tiết sản phẩm
- [ ] Hiển thị form chỉnh sửa
- [ ] Implement "Lưu thay đổi" → PUT API
- [ ] Implement "Xóa sản phẩm" → DELETE API (có confirm dialog)

**File cần tạo:**

```
└── AdminProductDetailActivity.java (NEW)
└── res/layout/
    └── activity_admin_product_detail.xml (NEW)
```

#### **Bước 2.4: AdminProductCreateActivity (Thêm mới)**

- [ ] Tạo layout `activity_admin_product_create.xml`
- [ ] Tạo `AdminProductCreateActivity.java`
- [ ] Form nhập thông tin sản phẩm:
  - [ ] Name, SKU, ShortDescription, Description, Ingredients, BurnTime
  - [ ] Price (decimal input)
  - [ ] CategoryId (Spinner - load từ GET /api/Category)
  - [ ] Attributes (optional - có thể skip trong MVP)
- [ ] Implement chọn nhiều ảnh (gallery hoặc camera)
- [ ] Implement "Tạo sản phẩm" → POST /api/Product với multipart/form-data
  - [ ] Convert ProductRequest → JSON string
  - [ ] Upload multiple images
- [ ] Validation form (name, description, price, categoryId required)

**File cần tạo:**

```
└── AdminProductCreateActivity.java (NEW)
└── res/layout/
    └── activity_admin_product_create.xml (NEW)
```

**Test:**

- [ ] Xem danh sách sản phẩm
- [ ] Thêm sản phẩm mới
- [ ] Sửa sản phẩm
- [ ] Xóa sản phẩm

---

### **GIAI ĐOẠN 3: Quản lý Đơn hàng (3-4 giờ)**

#### **Bước 3.1: Tạo Models**

- [ ] Tạo `Order.java` (theo response từ GET /api/Order/admin)
- [ ] Tạo `OrderItem.java` (theo OrderItemDetailDto trong response)
- [ ] Tạo `OrderStatusRequest.java` ({ "statusId": int })
- [ ] ⚠️ **Lưu ý:** Không cần OrderListResponse (response là List<Order> trực tiếp)
- [ ] ⚠️ **Lưu ý:** Không cần OrderDetailResponse (GET /api/Order/admin đã trả về đầy đủ thông tin)
- [ ] Update `ApiService.java` (thêm endpoints Order Admin)

#### **Bước 3.2: AdminOrderListActivity**

- [ ] Tạo layout `activity_admin_order_list.xml`
- [ ] Tạo `AdminOrderListActivity.java`
- [ ] Implement RecyclerView hiển thị danh sách đơn hàng
- [ ] Implement Spinner lọc theo trạng thái
- [ ] Implement SearchView (optional)
- [ ] Implement Pull to Refresh

**File cần tạo:**

```
├── models/
│   ├── Order.java (NEW)
│   ├── OrderItem.java (NEW)
│   ├── OrderListResponse.java (NEW)
│   ├── OrderDetailResponse.java (NEW)
│   └── OrderStatusRequest.java (NEW)
└── AdminOrderListActivity.java (NEW)
└── res/layout/
    └── activity_admin_order_list.xml (NEW)
└── res/layout/
    └── item_order.xml (NEW) // Item trong RecyclerView
```

#### **Bước 3.3: AdminOrderDetailActivity**

- [ ] Tạo layout `activity_admin_order_detail.xml`
- [ ] Tạo `AdminOrderDetailActivity.java`
- [ ] Nhận Order object từ Intent (từ AdminOrderListActivity)
- [ ] Hoặc reload từ GET /api/Order/admin và filter theo orderId
- [ ] Hiển thị:
  - [ ] Order Code, Ngày đặt
  - [ ] Thông tin khách hàng (customerFullName, customerEmail, recipientAddress)
  - [ ] RecyclerView: Danh sách sản phẩm (items)
  - [ ] Tổng tiền, Phí vận chuyển, Giảm giá
  - [ ] Phương thức thanh toán
  - [ ] Spinner chọn trạng thái mới (1-5: Pending, Preparing, Shipped, Completed, Cancelled)
  - [ ] Disable nút nếu order đã Cancelled
- [ ] Implement "Cập nhật trạng thái" → `PUT /api/Order/{orderId}/status`
  - [ ] Request body: `{ "statusId": int }`

**File cần tạo:**

```
└── AdminOrderDetailActivity.java (NEW)
└── res/layout/
    └── activity_admin_order_detail.xml (NEW)
└── adapter/
    └── OrderItemAdapter.java (NEW) // Adapter cho items trong đơn
```

**Test:**

- [ ] Xem danh sách đơn hàng
- [ ] Lọc đơn hàng theo trạng thái
- [ ] Xem chi tiết đơn hàng
- [ ] Cập nhật trạng thái đơn hàng

---

### **GIAI ĐOẠN 4: Quản lý Người dùng (Optional - 2-3 giờ)**

#### **Bước 4.1: Optional - Quản lý Voucher**

- [ ] Tạo `Voucher.java` model
- [ ] Update `ApiService.java` (thêm endpoints Voucher - Generic Controller)
- [ ] Tạo `AdminVoucherListActivity.java` (optional - future enhancement)

#### **Bước 4.2: Optional - Đăng ký nhân viên**

- [ ] Tạo `StaffRegisterRequest.java` model
- [ ] Update `ApiService.java` (thêm POST /api/Auth/register/staff)
- [ ] Tạo `AdminRegisterStaffActivity.java` (optional)

#### **Bước 4.2: AdminUserListActivity**

- [ ] Tạo layout `activity_admin_user_list.xml`
- [ ] Tạo `AdminUserListActivity.java`
- [ ] Implement RecyclerView hiển thị danh sách users
- [ ] Implement SearchView

**File cần tạo:**

```
├── models/
│   ├── User.java (NEW)
│   └── UserListResponse.java (NEW)
└── AdminUserListActivity.java (NEW)
└── res/layout/
    └── activity_admin_user_list.xml (NEW)
```

**Test:**

- [ ] Xem danh sách người dùng
- [ ] Tìm kiếm người dùng

---

### **GIAI ĐOẠN 5: Testing & Refinement (2-3 giờ)**

#### **Bước 5.1: Test toàn bộ luồng Admin**

- [ ] Test login với Admin account
- [ ] Test tất cả màn hình Admin
- [ ] Test CRUD sản phẩm
- [ ] Test quản lý đơn hàng
- [ ] Test edge cases (validation, errors)

#### **Bước 5.2: Fix bugs**

- [ ] Fix các bugs phát hiện khi test
- [ ] Cải thiện UI/UX
- [ ] Thêm loading indicators
- [ ] Thêm error handling

#### **Bước 5.3: Documentation**

- [ ] Update AndroidManifest.xml (đăng ký các Activity mới)
- [ ] Tạo file hướng dẫn test Admin (tương tự Customer)

---

## 📂 CẤU TRÚC THƯ MỤC SAU KHI HOÀN THÀNH

```
app/src/main/java/com/example/sereincandle/
│
├── adapter/
│   ├── CartAdapter.java (Customer)
│   ├── ProductAdapter.java (Customer)
│   ├── OrderItemAdapter.java (NEW - Admin)
│   └── AdminProductAdapter.java (NEW - Admin, optional)
│
├── models/
│   ├── Product.java
│   ├── CartItem.java (Customer)
│   ├── OrderRequest.java (Customer)
│   ├── ProductRequest.java (NEW - Admin)
│   ├── Order.java (NEW - Admin)
│   ├── OrderItem.java (NEW - Admin)
│   ├── OrderListResponse.java (NEW - Admin)
│   ├── OrderDetailResponse.java (NEW - Admin)
│   ├── OrderStatusRequest.java (NEW - Admin)
│   └── User.java (NEW - Admin, optional)
│
├── network/
│   ├── ApiService.java (UPDATE - thêm endpoints Admin)
│   └── ServiceGenerator.java
│
├── utils/
│   └── SessionManager.java (NEW)
│
├── MainActivity.java (UPDATE - check role)
├── HomePageActivity.java (Customer)
│
├── AdminHomeActivity.java (NEW)
├── AdminProductListActivity.java (NEW)
├── AdminProductDetailActivity.java (NEW)
├── AdminProductCreateActivity.java (NEW)
├── AdminOrderListActivity.java (NEW)
├── AdminOrderDetailActivity.java (NEW)
└── AdminUserListActivity.java (NEW - optional)
```

---

## ✅ CHECKLIST TỔNG QUAN

### **Phase 1: Setup**

- [ ] Cập nhật MainActivity để check role
- [ ] Tạo SessionManager
- [ ] Tạo AdminHomeActivity
- [ ] Test điều hướng Admin vs Customer

### **Phase 2: Product Management**

- [ ] Tạo ProductRequest model (với attributes)
- [ ] Tạo ProductAttributeValue model
- [ ] Tạo Category model
- [ ] Thêm API endpoints Product (multipart cho POST)
- [ ] Thêm API endpoints Category (GET để load dropdown)
- [ ] Tạo AdminProductListActivity
- [ ] Tạo AdminProductDetailActivity
- [ ] Tạo AdminProductCreateActivity (với upload multiple images)
- [ ] Test CRUD sản phẩm

### **Phase 3: Order Management**

- [ ] Tạo Order model (theo response thực tế)
- [ ] Tạo OrderItem model (OrderItemDetailDto)
- [ ] Tạo OrderStatusRequest model
- [ ] Thêm API endpoints: GET /api/Order/admin, PUT /api/Order/{orderId}/status
- [ ] Tạo AdminOrderListActivity (client-side filter, search)
- [ ] Tạo AdminOrderDetailActivity (nhận Order từ Intent)
- [ ] Implement validation: không cho sửa đơn đã hủy
- [ ] Test quản lý đơn hàng

### **Phase 4: User Management (Optional)**

- [ ] Tạo User models
- [ ] Thêm API endpoints User Admin
- [ ] Tạo AdminUserListActivity
- [ ] Test quản lý users

### **Phase 5: Testing & Polish**

- [ ] Test toàn bộ luồng
- [ ] Fix bugs
- [ ] Cải thiện UI/UX
- [ ] Documentation

---

## 🔐 SECURITY CONSIDERATIONS

1. **Role-based Access Control:**

   - Backend phải verify role trong mọi API Admin
   - Frontend chỉ hiển thị menu Admin nếu role = Admin
   - Nếu token hết hạn, force logout và quay về MainActivity

2. **Validation:**

   - Validate input khi tạo/sửa sản phẩm
   - Validate số tiền, số lượng (không được âm)
   - Validate format email, phone (nếu có)

3. **Error Handling:**
   - Hiển thị message rõ ràng khi lỗi
   - Handle 401 (Unauthorized) → Logout
   - Handle 403 (Forbidden) → Báo không có quyền

---

## 📊 ƯỚC TÍNH THỜI GIAN

| Giai đoạn                   | Thời gian     | Priority             |
| --------------------------- | ------------- | -------------------- |
| Phase 1: Setup              | 2-3 giờ       | 🔴 High              |
| Phase 2: Product Management | 4-6 giờ       | 🔴 High              |
| Phase 3: Order Management   | 3-4 giờ       | 🔴 High              |
| Phase 4: User Management    | 2-3 giờ       | 🟡 Medium (Optional) |
| Phase 5: Testing & Polish   | 2-3 giờ       | 🔴 High              |
| **TỔNG CỘNG**               | **13-19 giờ** |                      |

---

## 💡 BEST PRACTICES

1. **Tận dụng code Customer:**

   - Reuse `ProductAdapter` hoặc tạo `AdminProductAdapter` với thêm nút Sửa/Xóa
   - Reuse các models như `Product`, `ProductDetailResponse`
   - Reuse `ServiceGenerator`, `UnsafeOkHttpClient`

2. **Code Organization:**

   - Tách riêng Admin activities với prefix "Admin"
   - Dùng package `admin/` hoặc prefix để dễ quản lý

3. **UI/UX:**

   - Admin UI có thể đơn giản hơn Customer (focus vào functionality)
   - Thêm loading indicators khi gọi API
   - Thêm confirm dialogs cho các actions quan trọng (xóa, cập nhật)

4. **Testing:**
   - Test từng chức năng riêng biệt
   - Test với nhiều dữ liệu khác nhau
   - Test error cases

---

## 🚀 NEXT STEPS

1. **Bắt đầu với Phase 1:**

   - Cập nhật `MainActivity.java`
   - Tạo `SessionManager.java`
   - Tạo `AdminHomeActivity.java`

2. **Đảm bảo Backend sẵn sàng:**

   - Verify các API endpoints Admin đã được implement
   - Test API bằng Postman trước khi code Android

3. **Setup development environment:**
   - Có tài khoản Admin để test
   - Có dữ liệu test trong database

---

**Chúc bạn implement thành công! 🎉**
