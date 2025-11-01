# ⚠️ LƯU Ý QUAN TRỌNG VỀ API THỰC TẾ

## 🔴 KHÁC BIỆT GIỮA KẾ HOẠCH BAN ĐẦU VÀ API THỰC TẾ

### **1. PRODUCT APIs - KHÔNG CÓ PREFIX "Admin/"**

**Kế hoạch ban đầu (SAI):**
- `GET /api/Admin/Products`
- `POST /api/Admin/Products`
- `PUT /api/Admin/Products/{id}`
- `DELETE /api/Admin/Products/{id}`

**API thực tế (ĐÚNG):**
- ✅ `GET /api/Product` - Dùng chung với Customer
- ✅ `POST /api/Product` - Tạo mới (multipart/form-data)
- ✅ `PUT /api/Product/{id}` - Cập nhật
- ✅ `PUT /api/Product/{id}/images` - Cập nhật ảnh (multipart/form-data)
- ✅ `DELETE /api/Product/{id}` - Soft delete

**➡️ Kết luận:** Product APIs **KHÔNG có prefix "Admin/"**, dùng chung với Customer. Phân quyền được xử lý ở backend.

---

### **2. ORDER APIs - KHÔNG CÓ PREFIX "Admin/"**

**Kế hoạch ban đầu (SAI):**
- `GET /api/Admin/Orders`
- `GET /api/Admin/Orders/{id}`
- `PUT /api/Admin/Orders/{id}/status`

**API thực tế (ĐÚNG):**
- ✅ `GET /api/Order/admin` - Lấy tất cả đơn hàng (response là `List<Order>`)
- ✅ `PUT /api/Order/{orderId}/status` - Cập nhật trạng thái
  - Request: `{ "statusId": int }`
  - Không thể thay đổi đơn hàng đã Cancelled

**➡️ Kết luận:** 
- Order list endpoint là `/api/Order/admin` (không có `/api/Admin/Orders`)
- Không có endpoint riêng để lấy chi tiết 1 order (phải filter từ list hoặc dùng data từ list)

---

### **3. MULTIPART/FORM-DATA - Tạo sản phẩm**

**API:** `POST /api/Product`

**Content-Type:** `multipart/form-data`

**Request Body:**
```
Form Field 1: "productDto" = JSON string của ProductRequest
Form Field 2+: "images" = Multiple image files
```

**Implementation trong Android:**
```java
@Multipart
@POST("Product")
Call<ProductDetailResponse> createProduct(
    @Part("productDto") RequestBody productDtoJson,  // JSON string
    @Part List<MultipartBody.Part> images
);

// Usage:
Gson gson = new Gson();
String productJson = gson.toJson(productRequest);
RequestBody productDtoJson = RequestBody.create(
    MediaType.parse("application/json"), 
    productJson
);

List<MultipartBody.Part> imageParts = new ArrayList<>();
for (File imageFile : selectedImages) {
    RequestBody imageBody = RequestBody.create(
        MediaType.parse("image/*"), 
        imageFile
    );
    MultipartBody.Part part = MultipartBody.Part.createFormData(
        "images", 
        imageFile.getName(), 
        imageBody
    );
    imageParts.add(part);
}
```

---

### **4. ORDER RESPONSE - Không có pagination**

**API:** `GET /api/Order/admin`

**Response:**
```json
[
  {
    "orderId": int,
    "orderCode": string,
    "statusName": string,
    "paymentMethodName": string,
    "customerFullName": string,
    "customerEmail": string,
    "recipientAddress": string,
    "totalAmount": decimal,
    "discountAmount": decimal,
    "shippingFee": decimal,
    "createdAt": DateTime,
    "items": [ ... ]  // Đầy đủ thông tin
  }
]
```

**➡️ Kết luận:**
- Response là `List<Order>` trực tiếp (không wrap trong object có pagination)
- Không có query params cho pagination
- Mỗi Order đã có items đầy đủ (không cần gọi API riêng)
- **Cần client-side filtering:** Theo status, search theo orderCode/customerName

---

### **5. ORDER STATUS - Dùng statusId (int)**

**API:** `PUT /api/Order/{orderId}/status`

**Request Body:**
```json
{
  "statusId": int  // 1: Pending, 2: Preparing, 3: Shipped, 4: Completed, 5: Cancelled
}
```

**➡️ Kết luận:**
- Dùng `statusId` (int), không dùng `statusName` (string)
- Mapping:
  - 1 = Pending
  - 2 = Preparing
  - 3 = Shipped
  - 4 = Completed
  - 5 = Cancelled

---

### **6. PRODUCT ATTRIBUTES - Có trong ProductRequest**

**ProductRequest phải có:**
```java
public class ProductRequest {
    // ... other fields
    private List<ProductAttributeValue> attributes;  // optional
    
    public static class ProductAttributeValue {
        private int attributeId;  // required
        private String value;     // required
    }
}
```

**Backend API:**
- `GET /api/ProductAttribute` - Lấy danh sách attributes để chọn
- Khi tạo/sửa product, truyền array attributes với attributeId + value

---

### **7. CATEGORY - Cần load để chọn khi tạo/sửa sản phẩm**

**API:**
- `GET /api/Category` - Lấy tất cả categories
- Response: `List<Category>`

**Sử dụng:**
- Load vào Spinner khi tạo/sửa product
- User chọn category → lấy `categoryId` để gửi trong ProductRequest

---

## ✅ CHECKLIST: API Endpoints đúng

### **Product Management:**
- [x] `GET /api/Product` - Lấy danh sách (chung với Customer)
- [x] `GET /api/Product/{id}` - Chi tiết (chung với Customer)
- [x] `POST /api/Product` - Tạo mới (multipart/form-data)
- [x] `PUT /api/Product/{id}` - Cập nhật
- [x] `PUT /api/Product/{id}/images` - Cập nhật ảnh (multipart)
- [x] `DELETE /api/Product/{id}` - Soft delete

### **Order Management:**
- [x] `GET /api/Order/admin` - Lấy tất cả đơn hàng
- [x] `PUT /api/Order/{orderId}/status` - Cập nhật trạng thái

### **Category (Supporting):**
- [x] `GET /api/Category` - Lấy danh sách (cho dropdown)

### **ProductAttribute (Supporting):**
- [x] `GET /api/ProductAttribute` - Lấy danh sách (cho chọn attributes)

---

## 🎯 MAPPING MODELS VỚI API RESPONSE

### **Order Model:**

Theo response từ `GET /api/Order/admin`:
```java
public class Order {
    private int orderId;
    private String orderCode;
    private String statusName;        // 'Pending', 'Preparing', 'Shipped', 'Completed', 'Cancelled'
    private String paymentMethodName;  // 'COD', 'VNPay', 'MoMo', 'ZaloPay', 'BankTransfer'
    private Integer userId;            // nullable
    private String customerFullName;
    private String customerEmail;
    private String recipientAddress;   // Full address string (không phải tách riêng city, district, ward)
    private double totalAmount;
    private double discountAmount;
    private double shippingFee;
    private Date createdAt;
    private List<OrderItem> items;     // OrderItemDetailDto - đầy đủ thông tin
}
```

**➡️ Lưu ý:** 
- `recipientAddress` là string đầy đủ, không tách riêng như trong OrderRequest
- `items` đã có đầy đủ thông tin sản phẩm (không cần gọi API riêng)

---

### **ProductRequest Model:**

Theo `InsertProductDto` từ backend:
```java
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
}

public class ProductAttributeValue {
    private int attributeId;  // required
    private String value;      // required
}
```

**➡️ Lưu ý:**
- `categoryId` là required (không nullable)
- `attributes` là optional (có thể bỏ qua trong MVP)
- Khi POST với multipart, convert ProductRequest → JSON string

---

## 🔧 IMPLEMENTATION NOTES

### **1. Multipart Upload - Tạo sản phẩm:**

```java
// 1. Convert ProductRequest to JSON
Gson gson = new Gson();
String productJson = gson.toJson(productRequest);
RequestBody productDtoJson = RequestBody.create(
    MediaType.parse("application/json"), 
    productJson
);

// 2. Prepare image parts
List<MultipartBody.Part> imageParts = new ArrayList<>();
for (Uri imageUri : selectedImageUris) {
    File imageFile = new File(getRealPathFromURI(imageUri));
    RequestBody imageBody = RequestBody.create(
        MediaType.parse("image/*"), 
        imageFile
    );
    MultipartBody.Part part = MultipartBody.Part.createFormData(
        "images", 
        imageFile.getName(), 
        imageBody
    );
    imageParts.add(part);
}

// 3. Call API
Call<ProductDetailResponse> call = apiService.createProduct(productDtoJson, imageParts);
```

---

### **2. Order List - Client-side Filtering:**

```java
// Load tất cả orders
Call<List<Order>> call = apiService.getAdminOrders();
call.enqueue(new Callback<List<Order>>() {
    @Override
    public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
        List<Order> allOrders = response.body();
        
        // Client-side filter theo status
        List<Order> filteredOrders = allOrders.stream()
            .filter(order -> selectedStatus == null || 
                            order.getStatusName().equals(selectedStatus))
            .collect(Collectors.toList());
            
        // Client-side search
        if (!searchText.isEmpty()) {
            filteredOrders = filteredOrders.stream()
                .filter(order -> 
                    order.getOrderCode().contains(searchText) ||
                    order.getCustomerFullName().contains(searchText))
                .collect(Collectors.toList());
        }
        
        adapter.updateOrders(filteredOrders);
    }
});
```

---

### **3. Order Detail - Nhận từ List:**

```java
// Trong AdminOrderListActivity
adapter.setOnItemClickListener(order -> {
    Intent intent = new Intent(this, AdminOrderDetailActivity.class);
    intent.putExtra("ORDER", gson.toJson(order));  // Serialize Order to JSON
    startActivity(intent);
});

// Trong AdminOrderDetailActivity
String orderJson = getIntent().getStringExtra("ORDER");
Gson gson = new Gson();
Order order = gson.fromJson(orderJson, Order.class);

// Hoặc reload từ API và filter
```

---

## ✅ TÓM TẮT THAY ĐỔI

| Item | Kế hoạch ban đầu | API thực tế | Action |
|------|-------------------|-------------|--------|
| Product List | `GET /api/Admin/Products` | `GET /api/Product` | ✅ Đã cập nhật |
| Product Create | `POST /api/Admin/Products` | `POST /api/Product` (multipart) | ✅ Đã cập nhật |
| Product Update | `PUT /api/Admin/Products/{id}` | `PUT /api/Product/{id}` | ✅ Đã cập nhật |
| Product Images | `POST /api/Admin/Products/{id}/images` | `PUT /api/Product/{id}/images` | ✅ Đã cập nhật |
| Order List | `GET /api/Admin/Orders` | `GET /api/Order/admin` | ✅ Đã cập nhật |
| Order Detail | `GET /api/Admin/Orders/{id}` | Dùng data từ list | ✅ Đã cập nhật |
| Order Status | `PUT /api/Admin/Orders/{id}/status` | `PUT /api/Order/{orderId}/status` | ✅ Đã cập nhật |
| Response Pagination | Có | Không có | ✅ Client-side filter |
| OrderStatusRequest | `{ "statusName": string }` | `{ "statusId": int }` | ✅ Đã cập nhật |

---

**✅ Tất cả đã được cập nhật trong KE_HOACH_LUONG_ADMIN.md**

