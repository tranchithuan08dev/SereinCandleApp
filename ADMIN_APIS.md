# Danh Sách API Luồng Admin

Tài liệu này liệt kê tất cả các API dành cho quản trị viên (Admin) trong hệ thống Serein Candle Backend.

---

## 1. Quản Lý Xác Thực (Auth Controller)

### 1.1. Đăng ký Nhân viên
**Endpoint:** `POST /api/Auth/register/staff`

**Mô tả:** Đăng ký tài khoản nhân viên mới (chỉ dành cho Admin)

**Request Body:**
```json
{
  "email": "string (required, email format)",
  "password": "string (required)",
  "fullName": "string (required)",
  "phone": "string (required, phone format)",
  "dateOfBirth": "DateTime (required, format: yyyy-MM-dd)",
  "employeeCode": "string (required)",
  "position": "string (required)"
}
```

**Response:**
- **201 Created:** Đăng ký thành công
- **400 Bad Request:** Email hoặc số điện thoại đã tồn tại
- **500 Internal Server Error:** Lỗi hệ thống

---

## 2. Quản Lý Đơn Hàng (Order Controller)

### 2.1. Cập nhật Trạng thái Đơn hàng
**Endpoint:** `PUT /api/Order/{orderId}/status`

**Mô tả:** Cập nhật trạng thái đơn hàng (dành cho Admin/Staff)
> **Lưu ý:** Hiện tại chưa có [Authorize(Roles = "Staff, Admin")] nhưng được dự định là API admin

**Path Parameters:**
- `orderId` (int): ID của đơn hàng

**Request Body:**
```json
{
  "statusId": "int (required)"
}
```

**Response:**
- **200 OK:** Cập nhật thành công
- **400 Bad Request:** Lỗi cập nhật (mã đơn hàng không tồn tại, statusId không hợp lệ, hoặc không thể thay đổi trạng thái đơn hàng đã hủy)
- **401 Unauthorized:** Chưa đăng nhập
- **404 Not Found:** Không tìm thấy đơn hàng

---

### 2.2. Lấy Danh sách Đơn hàng (Admin)
**Endpoint:** `GET /api/Order/admin`

**Mô tả:** Lấy tất cả đơn hàng trong hệ thống dành cho Admin

**Response:**
- **200 OK:** Danh sách đơn hàng
```json
[
  {
    "orderId": "int",
    "orderCode": "string",
    "statusName": "string",
    "paymentMethodName": "string",
    "userId": "int?",
    "customerFullName": "string",
    "customerEmail": "string",
    "recipientAddress": "string",
    "totalAmount": "decimal",
    "discountAmount": "decimal",
    "shippingFee": "decimal",
    "createdAt": "DateTime",
    "items": [
      {
        // OrderItemDetailDto
      }
    ]
  }
]
```
- **401 Unauthorized:** Chưa đăng nhập

---

## 3. Quản Lý Sản Phẩm (Product Controller)

### 3.1. Tạo Sản phẩm Mới
**Endpoint:** `POST /api/Product`

**Mô tả:** Thêm sản phẩm mới vào hệ thống

**Content-Type:** `multipart/form-data`

**Request Body (Form Data):**
- `productDto`: JSON object (InsertProductDto)
  ```json
  {
    "name": "string (required)",
    "sku": "string",
    "shortDescription": "string",
    "description": "string (required)",
    "ingredients": "string",
    "burnTime": "string",
    "price": "decimal (required)",
    "categoryId": "int (required)",
    "attributes": [
      {
        "attributeId": "int (required)",
        "value": "string (required)"
      }
    ]
  }
  ```
- `images`: IFormFileCollection (hình ảnh sản phẩm)

**Response:**
- **200 OK:** Thêm sản phẩm thành công
- **400 Bad Request:** Dữ liệu không hợp lệ hoặc thêm sản phẩm thất bại

---

### 3.2. Cập nhật Sản phẩm
**Endpoint:** `PUT /api/Product/{id}`

**Mô tả:** Cập nhật thông tin sản phẩm

**Path Parameters:**
- `id` (int): ID của sản phẩm

**Request Body:**
```json
{
  "name": "string (required)",
  "sku": "string",
  "shortDescription": "string",
  "description": "string (required)",
  "ingredients": "string",
  "burnTime": "string",
  "price": "decimal (required)",
  "categoryId": "int (required)",
  "attributes": [
    {
      "attributeId": "int (required)",
      "value": "string (required)"
    }
  ]
}
```

**Response:**
- **200 OK:** Cập nhật thành công
- **400 Bad Request:** Dữ liệu không hợp lệ
- **404 Not Found:** Không tìm thấy sản phẩm

---

### 3.3. Cập nhật Hình ảnh Sản phẩm
**Endpoint:** `PUT /api/Product/{id}/images`

**Mô tả:** Cập nhật hình ảnh của sản phẩm

**Content-Type:** `multipart/form-data`

**Path Parameters:**
- `id` (int): ID của sản phẩm

**Request Body (Form Data):**
- `images`: IFormFileCollection (hình ảnh mới)

**Response:**
- **200 OK:** Cập nhật hình ảnh thành công
- **400 Bad Request:** Không có hình ảnh được cung cấp
- **404 Not Found:** Không tìm thấy sản phẩm

---

### 3.4. Xóa Sản phẩm (Soft Delete)
**Endpoint:** `DELETE /api/Product/{id}`

**Mô tả:** Xóa mềm sản phẩm (không xóa khỏi database)

**Path Parameters:**
- `id` (int): ID của sản phẩm

**Response:**
- **200 OK:** Xóa thành công
- **404 Not Found:** Không tìm thấy sản phẩm

---

## 4. Quản Lý Danh Mục (Category Controller)

Tất cả các API Category sử dụng Generic Controller pattern.

### 4.1. Lấy Tất cả Danh mục
**Endpoint:** `GET /api/Category`

**Mô tả:** Lấy danh sách tất cả danh mục

**Response:**
- **200 OK:** Danh sách danh mục
```json
[
  {
    "categoryId": "int",
    "name": "string",
    "slug": "string",
    "description": "string?",
    "parentCategoryId": "int?"
  }
]
```

---

### 4.2. Lấy Danh mục theo ID
**Endpoint:** `GET /api/Category/{id}`

**Mô tả:** Lấy thông tin chi tiết của một danh mục

**Path Parameters:**
- `id` (int): ID của danh mục

**Response:**
- **200 OK:** Thông tin danh mục (CategoryCRUDDto)
- **404 Not Found:** Không tìm thấy danh mục

---

### 4.3. Tạo Danh mục Mới
**Endpoint:** `POST /api/Category`

**Mô tả:** Thêm danh mục mới

**Request Body:**
```json
{
  "categoryId": "int",
  "name": "string",
  "slug": "string",
  "description": "string?",
  "parentCategoryId": "int?"
}
```

**Response:**
- **201 Created:** Tạo thành công
- **400 Bad Request:** Dữ liệu không hợp lệ

---

### 4.4. Cập nhật Danh mục
**Endpoint:** `PUT /api/Category/{id}`

**Mô tả:** Cập nhật thông tin danh mục

**Path Parameters:**
- `id` (int): ID của danh mục

**Request Body:**
```json
{
  "categoryId": "int",
  "name": "string",
  "slug": "string",
  "description": "string?",
  "parentCategoryId": "int?"
}
```

**Response:**
- **204 No Content:** Cập nhật thành công
- **404 Not Found:** Không tìm thấy danh mục

---

## 5. Quản Lý Voucher (Voucher Controller)

Tất cả các API Voucher sử dụng Generic Controller pattern.

### 5.1. Lấy Tất cả Voucher
**Endpoint:** `GET /api/Voucher`

**Mô tả:** Lấy danh sách tất cả voucher

**Response:**
- **200 OK:** Danh sách voucher
```json
[
  {
    "voucherId": "int",
    "code": "string",
    "description": "string?",
    "discountPercent": "int?",
    "discountAmount": "decimal?",
    "minOrderAmount": "decimal?",
    "startDate": "DateTime?",
    "endDate": "DateTime?",
    "maxUses": "int?",
    "usedCount": "int",
    "isActive": "bool"
  }
]
```

---

### 5.2. Lấy Voucher theo ID
**Endpoint:** `GET /api/Voucher/{id}`

**Mô tả:** Lấy thông tin chi tiết của một voucher

**Path Parameters:**
- `id` (int): ID của voucher

**Response:**
- **200 OK:** Thông tin voucher (VoucherCRUDDto)
- **404 Not Found:** Không tìm thấy voucher

---

### 5.3. Tạo Voucher Mới
**Endpoint:** `POST /api/Voucher`

**Mô tả:** Thêm voucher mới

**Request Body:**
```json
{
  "voucherId": "int",
  "code": "string",
  "description": "string?",
  "discountPercent": "int?",
  "discountAmount": "decimal?",
  "minOrderAmount": "decimal?",
  "startDate": "DateTime?",
  "endDate": "DateTime?",
  "maxUses": "int?",
  "usedCount": "int",
  "isActive": "bool"
}
```

**Response:**
- **201 Created:** Tạo thành công
- **400 Bad Request:** Dữ liệu không hợp lệ

---

### 5.4. Cập nhật Voucher
**Endpoint:** `PUT /api/Voucher/{id}`

**Mô tả:** Cập nhật thông tin voucher

**Path Parameters:**
- `id` (int): ID của voucher

**Request Body:**
```json
{
  "voucherId": "int",
  "code": "string",
  "description": "string?",
  "discountPercent": "int?",
  "discountAmount": "decimal?",
  "minOrderAmount": "decimal?",
  "startDate": "DateTime?",
  "endDate": "DateTime?",
  "maxUses": "int?",
  "usedCount": "int",
  "isActive": "bool"
}
```

**Response:**
- **204 No Content:** Cập nhật thành công
- **404 Not Found:** Không tìm thấy voucher

---

## 6. Quản Lý Thuộc tính Sản phẩm (Product Attribute Controller)

Tất cả các API ProductAttribute sử dụng Generic Controller pattern.

### 6.1. Lấy Tất cả Thuộc tính Sản phẩm
**Endpoint:** `GET /api/ProductAttribute`

**Mô tả:** Lấy danh sách tất cả thuộc tính sản phẩm

**Response:**
- **200 OK:** Danh sách thuộc tính
```json
[
  {
    "attributeId": "int",
    "name": "string"
  }
]
```

---

### 6.2. Lấy Thuộc tính theo ID
**Endpoint:** `GET /api/ProductAttribute/{id}`

**Mô tả:** Lấy thông tin chi tiết của một thuộc tính

**Path Parameters:**
- `id` (int): ID của thuộc tính

**Response:**
- **200 OK:** Thông tin thuộc tính (ProductAttributeCRUDDto)
- **404 Not Found:** Không tìm thấy thuộc tính

---

### 6.3. Tạo Thuộc tính Mới
**Endpoint:** `POST /api/ProductAttribute`

**Mô tả:** Thêm thuộc tính sản phẩm mới

**Request Body:**
```json
{
  "attributeId": "int",
  "name": "string"
}
```

**Response:**
- **201 Created:** Tạo thành công
- **400 Bad Request:** Dữ liệu không hợp lệ

---

### 6.4. Cập nhật Thuộc tính
**Endpoint:** `PUT /api/ProductAttribute/{id}`

**Mô tả:** Cập nhật thông tin thuộc tính

**Path Parameters:**
- `id` (int): ID của thuộc tính

**Request Body:**
```json
{
  "attributeId": "int",
  "name": "string"
}
```

**Response:**
- **204 No Content:** Cập nhật thành công
- **404 Not Found:** Không tìm thấy thuộc tính

---

## 7. Quản Lý Loại Vai trò (Role Type Controller)

Tất cả các API RoleType sử dụng Generic Controller pattern.

### 7.1. Lấy Tất cả Loại Vai trò
**Endpoint:** `GET /api/RoleType`

**Mô tả:** Lấy danh sách tất cả loại vai trò

**Response:**
- **200 OK:** Danh sách loại vai trò
```json
[
  {
    "roleId": "int",
    "roleName": "string"
  }
]
```

---

### 7.2. Lấy Loại Vai trò theo ID
**Endpoint:** `GET /api/RoleType/{id}`

**Mô tả:** Lấy thông tin chi tiết của một loại vai trò

**Path Parameters:**
- `id` (int): ID của loại vai trò

**Response:**
- **200 OK:** Thông tin loại vai trò (RoleTypeCRUDDto)
- **404 Not Found:** Không tìm thấy loại vai trò

---

### 7.3. Tạo Loại Vai trò Mới
**Endpoint:** `POST /api/RoleType`

**Mô tả:** Thêm loại vai trò mới

**Request Body:**
```json
{
  "roleId": "int",
  "roleName": "string"
}
```

**Response:**
- **201 Created:** Tạo thành công
- **400 Bad Request:** Dữ liệu không hợp lệ

---

### 7.4. Cập nhật Loại Vai trò
**Endpoint:** `PUT /api/RoleType/{id}`

**Mô tả:** Cập nhật thông tin loại vai trò

**Path Parameters:**
- `id` (int): ID của loại vai trò

**Request Body:**
```json
{
  "roleId": "int",
  "roleName": "string"
}
```

**Response:**
- **204 No Content:** Cập nhật thành công
- **404 Not Found:** Không tìm thấy loại vai trò

---

## Tổng Kết

### Thống Kê API Admin

| Controller | Số lượng API | Endpoints |
|------------|--------------|-----------|
| Auth | 1 | `/api/Auth/register/staff` |
| Order | 2 | `/api/Order/{orderId}/status`, `/api/Order/admin` |
| Product | 4 | `/api/Product` (POST, PUT, PUT images, DELETE) |
| Category | 4 | `/api/Category` (GET all, GET by id, POST, PUT) |
| Voucher | 4 | `/api/Voucher` (GET all, GET by id, POST, PUT) |
| ProductAttribute | 4 | `/api/ProductAttribute` (GET all, GET by id, POST, PUT) |
| RoleType | 4 | `/api/RoleType` (GET all, GET by id, POST, PUT) |
| **TỔNG CỘNG** | **23** | |

### Lưu Ý

1. **Phân quyền:** Hiện tại một số API chưa có attribute `[Authorize(Roles = "Admin")]` rõ ràng, nhưng được thiết kế dành cho Admin
2. **Generic Controller:** Các controller Category, Voucher, ProductAttribute, RoleType kế thừa từ GenericController và có cùng pattern CRUD
3. **Content-Type:** API tạo/cập nhật sản phẩm và hình ảnh sử dụng `multipart/form-data`
4. **Soft Delete:** Xóa sản phẩm là soft delete (không xóa khỏi database)

---

**Ngày tạo:** 2024  
**Phiên bản:** 1.0

