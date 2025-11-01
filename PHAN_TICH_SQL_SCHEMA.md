# 📊 PHÂN TÍCH SQL SCHEMA & MAPPING VỚI ANDROID

## 🎯 TỔNG QUAN DATABASE

**Database:** `CandleShopDB` (SQL Server)

**Schema bao gồm:**
- ✅ User Management (User, Role, Staff)
- ✅ Product Management (Product, ProductImage, ProductAttribute, Inventory)
- ✅ Cart & Wishlist
- ✅ Order Management (Order, OrderItem, OrderStatus)
- ✅ Payment (Payment, PaymentMethod)
- ✅ Voucher/Promotion
- ✅ Reviews
- ✅ Audit Log

---

## 📋 MAPPING: SQL TABLES ↔ ANDROID MODELS

### **1. PRODUCT MANAGEMENT**

#### **SQL Table: `Product`**
```sql
CREATE TABLE Product (
    ProductId INT PRIMARY KEY,
    CategoryId INT,
    SKU NVARCHAR(100),
    Name NVARCHAR(300),
    Slug NVARCHAR(300),
    ShortDescription NVARCHAR(1000),
    Description NVARCHAR(MAX),
    Ingredients NVARCHAR(1000),
    BurnTime NVARCHAR(100),
    Price DECIMAL(12,2),
    IsActive BIT,
    CreatedAt DATETIME2,
    UpdatedAt DATETIME2
)
```

#### **Android Model: `Product.java`**
```java
public class Product {
    private int productId;        ✅ → ProductId
    private String name;           ✅ → Name
    private String sku;            ✅ → SKU
    private String description;    ✅ → Description (hoặc ShortDescription)
    private String ingredients;    ✅ → Ingredients
    private String burnTime;       ✅ → BurnTime
    private int price;             ✅ → Price (nhưng SQL là DECIMAL, Android dùng int - cần check)
    private boolean isActive;      ✅ → IsActive
    private String categoryName;   ⚠️ → Không có trong Product table (cần JOIN với Category)
    private List<ProductAttribute> attributes; ✅ → Từ ProductAttributeValue
    private List<ProductImage> images; ✅ → Từ ProductImage table
}
```

**Mapping Status:**
- ✅ **Khớp:** productId, name, sku, description, ingredients, burnTime, isActive
- ⚠️ **Thiếu trong Android:** CategoryId, Slug, ShortDescription, CreatedAt, UpdatedAt
- ⚠️ **Khác biệt:** Price (SQL: DECIMAL, Android: int) - Có thể cần đổi thành double

---

#### **SQL Table: `ProductImage`**
```sql
CREATE TABLE ProductImage (
    ProductImageId INT PRIMARY KEY,
    ProductId INT,
    ImageUrl NVARCHAR(1000),
    IsPrimary BIT,
    SortOrder INT
)
```

#### **Android Model: `ProductImage.java`**
```java
public class ProductImage {
    private String imageUrl;      ✅ → ImageUrl
    private boolean isPrimary;     ✅ → IsPrimary
}
```

**Mapping Status:**
- ✅ **Khớp:** imageUrl, isPrimary
- ⚠️ **Thiếu:** ProductImageId, SortOrder (có thể không cần trong Android)

---

#### **SQL Table: `ProductAttribute` & `ProductAttributeValue`**
```sql
CREATE TABLE ProductAttribute (
    AttributeId INT PRIMARY KEY,
    Name NVARCHAR(150)  -- e.g. 'Scent', 'WaxType'
)

CREATE TABLE ProductAttributeValue (
    ProductId INT,
    AttributeId INT,
    Value NVARCHAR(200),
    PRIMARY KEY (ProductId, AttributeId)
)
```

#### **Android Model: `ProductAttribute.java`**
```java
public class ProductAttribute {
    private String attributeName;  ⚠️ → Tương ứng với ProductAttribute.Name
    private String value;          ✅ → ProductAttributeValue.Value
}
```

**Mapping Status:**
- ⚠️ **Cần mapping:** AttributeId trong SQL nhưng Android chỉ có name + value
- ✅ **Logic hợp lý:** Android model đơn giản hóa, backend sẽ JOIN và gửi name + value

---

### **2. CART MANAGEMENT**

#### **SQL Tables: `Cart` & `CartItem`**
```sql
CREATE TABLE Cart (
    CartId INT PRIMARY KEY,
    UserId INT,
    CreatedAt DATETIME2,
    UpdatedAt DATETIME2
)

CREATE TABLE CartItem (
    CartItemId INT PRIMARY KEY,
    CartId INT,
    ProductId INT,
    Quantity INT,
    PriceAtAdd DECIMAL(12,2)  -- Giá tại thời điểm thêm vào giỏ
)
```

#### **Android Models:**

**`CartResponse.java`:**
```java
public class CartResponse {
    private int cartId;           ✅ → Cart.CartId
    private int userId;            ✅ → Cart.UserId
    private double totalAmount;    ⚠️ → Tính từ CartItem (không có trong Cart table)
    private List<CartItem> items; ✅ → Từ CartItem table
}
```

**`CartItem.java`:**
```java
public class CartItem {
    private int productId;        ✅ → CartItem.ProductId
    private String productName;   ⚠️ → Không có trong CartItem (cần JOIN Product)
    private String productSku;    ⚠️ → Không có trong CartItem (cần JOIN Product)
    private int quantity;         ✅ → CartItem.Quantity
    private double priceAtAdd;    ✅ → CartItem.PriceAtAdd
    private String imageUrl;      ⚠️ → Không có trong CartItem (cần JOIN ProductImage)
}
```

**`CartItemRequest.java`:**
```java
public class CartItemRequest {
    private int productId;        ✅ → CartItem.ProductId
    private int quantity;         ✅ → CartItem.Quantity
}
```

**Mapping Status:**
- ✅ **Khớp:** cartId, userId, productId, quantity, priceAtAdd
- ⚠️ **Backend phải JOIN:** productName, productSku, imageUrl từ Product/ProductImage
- ⚠️ **Backend phải tính:** totalAmount từ tổng các CartItem

---

### **3. ORDER MANAGEMENT**

#### **SQL Tables: `Order` & `OrderItem` & `CustomerAddress`**
```sql
CREATE TABLE CustomerAddress (
    AddressId INT PRIMARY KEY,
    UserId INT,
    FullName NVARCHAR(200),
    Phone NVARCHAR(20),
    AddressLine NVARCHAR(500),
    City NVARCHAR(200),
    District NVARCHAR(200),
    Ward NVARCHAR(200),
    IsDefault BIT
)

CREATE TABLE [Order] (
    OrderId INT PRIMARY KEY,
    UserId INT,
    OrderCode NVARCHAR(50),
    StatusId INT,  -- FK to OrderStatus
    ShippingAddressId INT,  -- FK to CustomerAddress
    TotalAmount DECIMAL(12,2),
    DiscountAmount DECIMAL(12,2),
    ShippingFee DECIMAL(12,2),
    PaymentMethodId INT,
    TransactionRef NVARCHAR(200),
    Note NVARCHAR(1000),
    CreatedAt DATETIME2,
    UpdatedAt DATETIME2
)

CREATE TABLE OrderItem (
    OrderItemId INT PRIMARY KEY,
    OrderId INT,
    ProductId INT,
    Quantity INT,
    UnitPrice DECIMAL(12,2),
    TotalPrice AS (Quantity * UnitPrice) PERSISTED
)
```

#### **Android Models:**

**`OrderRequest.java`:**
```java
public class OrderRequest {
    private String fullName;      ✅ → CustomerAddress.FullName (hoặc Order address)
    private String phone;         ✅ → CustomerAddress.Phone
    private String addressLine;   ✅ → CustomerAddress.AddressLine
    private String city;          ✅ → CustomerAddress.City
    private String district;      ✅ → CustomerAddress.District
    private String ward;          ✅ → CustomerAddress.Ward
    private List<OrderItemRequest> items; ✅ → OrderItem
    private int paymentMethodId;  ✅ → Order.PaymentMethodId
    private String note;          ✅ → Order.Note
    private String voucherCode;   ⚠️ → Từ Voucher table (không trực tiếp trong Order)
}
```

**`OrderItemRequest.java`:**
```java
public class OrderItemRequest {
    private int productId;       ✅ → OrderItem.ProductId
    private int quantity;        ✅ → OrderItem.Quantity
    // ⚠️ Backend sẽ tự tính UnitPrice từ Product.Price tại thời điểm đặt hàng
}
```

**Mapping Status:**
- ✅ **Khớp:** Hầu hết fields đều có
- ⚠️ **Backend phải xử lý:**
  - Tạo CustomerAddress từ OrderRequest
  - Tính TotalAmount từ OrderItem
  - Tạo OrderCode tự động
  - Map StatusId (mặc định = 1: Pending)
  - Xử lý voucherCode → tính DiscountAmount

---

### **4. USER & AUTHENTICATION**

#### **SQL Tables: `User` & `RoleType`**
```sql
CREATE TABLE RoleType (
    RoleId INT PRIMARY KEY,
    RoleName NVARCHAR(50)  -- 'Customer', 'Staff', 'Admin'
)

CREATE TABLE [User] (
    UserId INT PRIMARY KEY,
    RoleId INT,  -- FK to RoleType
    Email NVARCHAR(255),
    Phone NVARCHAR(20),
    FullName NVARCHAR(200),
    PasswordHash NVARCHAR(500),
    IsGuest BIT,
    Dob DATE,
    CreatedAt DATETIME2,
    UpdatedAt DATETIME2,
    IsActive BIT
)
```

#### **Android Models:**

**`LoginRequest.java`:**
```java
public class LoginRequest {
    private String email;         ✅ → User.Email
    private String password;      ⚠️ → Backend phải hash và so với User.PasswordHash
}
```

**`LoginResponse.java` & `UserData.java`:**
```java
public class UserData {
    private String token;         ⚠️ → JWT token (không lưu trong DB, backend tạo)
    private String email;         ✅ → User.Email
    private String fullName;      ✅ → User.FullName
    private String roleName;      ✅ → RoleType.RoleName (JOIN)
}
```

**Mapping Status:**
- ✅ **Khớp:** email, fullName, roleName
- ⚠️ **Backend xử lý:** 
  - Hash password và so với PasswordHash
  - Tạo JWT token (không lưu trong DB)
  - JOIN với RoleType để lấy RoleName

---

## 🔍 PHÂN TÍCH CHI TIẾT

### **A. ORDER STATUS**

#### **SQL:**
```sql
CREATE TABLE OrderStatus (
    StatusId INT PRIMARY KEY,
    StatusName NVARCHAR(50)  -- 'Pending', 'Preparing', 'Shipped', 'Completed', 'Cancelled'
)
```

#### **Android (Chưa có model):**
```java
// Cần tạo cho Admin luồng
public class OrderStatus {
    private int statusId;
    private String statusName;
}
```

**Status Values:**
- 1: Pending (Chờ xác nhận)
- 2: Preparing (Chuẩn bị hàng)
- 3: Shipped (Đang giao)
- 4: Completed (Hoàn tất)
- 5: Cancelled (Đã hủy)

---

### **B. PAYMENT METHOD**

#### **SQL:**
```sql
CREATE TABLE PaymentMethod (
    PaymentMethodId INT PRIMARY KEY,
    MethodName NVARCHAR(50)  -- 'COD', 'VNPay', 'MoMo', 'ZaloPay', 'BankTransfer'
)
```

#### **Android:**
- `OrderRequest.paymentMethodId` → ✅ Đã có
- Mặc định: `paymentMethodId = 1` → Có thể là 'COD'

**Payment Methods:**
- 1: COD (Thanh toán khi nhận hàng)
- 2: VNPay
- 3: MoMo
- 4: ZaloPay
- 5: BankTransfer

---

### **C. CATEGORY**

#### **SQL:**
```sql
CREATE TABLE Category (
    CategoryId INT PRIMARY KEY,
    Name NVARCHAR(150),
    Slug NVARCHAR(150),
    Description NVARCHAR(1000),
    ParentCategoryId INT  -- Support nested categories
)
```

#### **Android:**
- `Product.categoryName` → ✅ Có (từ JOIN)
- ⚠️ Chưa có Category model riêng (có thể cần cho Admin)

---

### **D. INVENTORY (Chưa có trong Android)**

#### **SQL:**
```sql
CREATE TABLE Inventory (
    InventoryId INT PRIMARY KEY,
    ProductId INT UNIQUE,
    Quantity INT,
    ReorderThreshold INT,
    LastUpdated DATETIME2
)
```

#### **Android:**
- ❌ Chưa có model
- 💡 **Có thể cần cho Admin:** Hiển thị số lượng tồn kho

---

### **E. VOUCHER (Chưa có trong Android)**

#### **SQL:**
```sql
CREATE TABLE Voucher (
    VoucherId INT PRIMARY KEY,
    Code NVARCHAR(50),
    Description NVARCHAR(500),
    DiscountPercent INT,
    DiscountAmount DECIMAL(12,2),
    MinOrderAmount DECIMAL(12,2),
    StartDate DATETIME2,
    EndDate DATETIME2,
    MaxUses INT,
    UsedCount INT,
    IsActive BIT
)
```

#### **Android:**
- `OrderRequest.voucherCode` → ✅ Có field nhưng chưa sử dụng
- 💡 **Future enhancement:** Có thể thêm màn hình nhập voucher code

---

## ⚠️ CÁC VẤN ĐỀ CẦN LƯU Ý

### **1. Data Type Mismatch**

#### **Price:**
- SQL: `DECIMAL(12,2)` (có thể có số thập phân)
- Android: `int price` (chỉ số nguyên)
- 💡 **Nên đổi:** `int price` → `double price` hoặc `BigDecimal price`

#### **TotalAmount:**
- SQL: `DECIMAL(12,2)`
- Android: `double totalAmount` → ✅ OK

---

### **2. Missing Fields trong Android Models**

#### **Product:**
- ❌ `categoryId` - Cần nếu Admin muốn sửa category
- ❌ `slug` - Có thể không cần trong app
- ❌ `shortDescription` - Có thể dùng description
- ❌ `createdAt`, `updatedAt` - Không cần cho Customer, có thể cần cho Admin

#### **Order (chưa có model trong Customer flow):**
- ❌ `orderId` - Cần cho Admin xem chi tiết
- ❌ `orderCode` - Mã đơn hàng để hiển thị
- ❌ `statusId`, `statusName` - Cần cho Admin
- ❌ `createdAt` - Ngày đặt hàng

---

### **3. Backend Responsibilities**

Backend phải:
1. **JOIN tables** để gửi đầy đủ data:
   - Product + Category → categoryName
   - CartItem + Product → productName, productSku
   - CartItem + ProductImage → imageUrl (primary image)
   - User + RoleType → roleName

2. **Calculate:**
   - Cart.totalAmount = SUM(CartItem.Quantity * CartItem.PriceAtAdd)
   - Order.totalAmount = SUM(OrderItem.Quantity * OrderItem.UnitPrice)

3. **Auto-generate:**
   - Order.OrderCode (unique)
   - Order.CreatedAt, Order.UpdatedAt

4. **Validate:**
   - Voucher code (nếu có)
   - Inventory quantity (đủ hàng không)

---

## 📋 CẬP NHẬT KẾ HOẠCH ADMIN

### **Models cần tạo mới:**

1. **Order.java** (cho Admin):
```java
public class Order {
    private int orderId;
    private String orderCode;
    private int statusId;
    private String statusName;  // 'Pending', 'Preparing'...
    private String fullName;
    private String phone;
    private String addressLine;
    private String city;
    private String district;
    private String ward;
    private double totalAmount;
    private double discountAmount;
    private double shippingFee;
    private int paymentMethodId;
    private String paymentMethodName;
    private String note;
    private Date createdAt;
    private List<OrderItem> items;
}
```

2. **OrderItem.java** (chi tiết trong đơn):
```java
public class OrderItem {
    private int orderItemId;
    private int productId;
    private String productName;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
    private String productImageUrl;
}
```

3. **OrderStatus.java** (enum/list):
```java
public class OrderStatus {
    private int statusId;
    private String statusName;
}
```

4. **Category.java** (cho Admin quản lý):
```java
public class Category {
    private int categoryId;
    private String name;
    private String slug;
    private String description;
    private Integer parentCategoryId;
}
```

5. **Inventory.java** (optional, cho Admin):
```java
public class Inventory {
    private int inventoryId;
    private int productId;
    private int quantity;
    private int reorderThreshold;
}
```

---

## ✅ CHECKLIST: CẦN CẬP NHẬT

### **Models cần sửa:**

- [ ] **Product.java:**
  - [ ] Đổi `int price` → `double price` (hoặc BigDecimal)
  - [ ] Thêm `categoryId` (optional, cho Admin)

### **Models cần tạo:**

- [ ] **Order.java** - Model đơn hàng đầy đủ
- [ ] **OrderItem.java** - Item trong đơn hàng
- [ ] **OrderStatus.java** - Trạng thái đơn hàng
- [ ] **Category.java** - Danh mục sản phẩm
- [ ] **Inventory.java** - Tồn kho (optional)
- [ ] **Voucher.java** - Mã giảm giá (future)

### **API Endpoints cần thêm:**

- [ ] `GET /api/Admin/Orders` - Lấy danh sách đơn hàng
- [ ] `GET /api/Admin/Orders/{id}` - Chi tiết đơn hàng
- [ ] `PUT /api/Admin/Orders/{id}/status` - Cập nhật trạng thái
- [ ] `GET /api/Admin/Categories` - Lấy danh sách categories (cho Admin tạo/sửa product)
- [ ] `GET /api/Admin/OrderStatus` - Lấy danh sách trạng thái (cho dropdown)

---

## 🎯 TÓM TẮT

### **Mapping Status:**

| Entity | SQL Table | Android Model | Status |
|--------|-----------|---------------|--------|
| Product | `Product` | `Product.java` | ✅ Khớp (nhưng price nên đổi thành double) |
| ProductImage | `ProductImage` | `ProductImage.java` | ✅ Khớp |
| Cart | `Cart` | `CartResponse.java` | ✅ Khớp (backend tính totalAmount) |
| CartItem | `CartItem` | `CartItem.java` | ✅ Khớp (backend JOIN product info) |
| Order | `Order` | ❌ Chưa có | ⚠️ Cần tạo cho Admin |
| OrderItem | `OrderItem` | `OrderItemRequest.java` | ⚠️ Cần tạo OrderItem.java (response) |
| User | `User` | `UserData.java` | ✅ Khớp (backend JOIN role) |
| Category | `Category` | ❌ Chưa có | ⚠️ Cần tạo cho Admin |

---

**Kết luận:** Database schema khá đầy đủ và khớp với Android code hiện tại. Cần bổ sung một số models cho Admin luồng và có thể cần điều chỉnh data types (price).

