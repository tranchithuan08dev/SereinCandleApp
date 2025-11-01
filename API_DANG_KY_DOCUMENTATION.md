# Tài Liệu API Đăng Ký - Serein Candle Shop

## Base URL
```
http://localhost:{port}/api/auth
```
*Lưu ý: Thay `{port}` bằng port thực tế của API (thường là 5000, 5001, 7000, 7001)*

---

## 1. Đăng Ký Tài Khoản Customer

### Endpoint
```
POST /api/auth/register
```

### Mô Tả
Đăng ký tài khoản khách hàng mới. Sau khi đăng ký thành công, user sẽ được gán role "Customer" tự động.

### Request

#### Headers
```
Content-Type: application/json
```

#### Body (JSON)
```json
{
  "email": "string (required, email format)",
  "password": "string (required)",
  "fullName": "string (required)",
  "phone": "string (required, phone format)",
  "dateOfBirth": "datetime (required, format: yyyy-MM-dd)"
}
```

#### Schema (RegisterDto)
| Field | Type | Required | Validation | Mô Tả |
|-------|------|----------|------------|-------|
| `email` | string | ✅ | Email format | Email đăng ký |
| `password` | string | ✅ | - | Mật khẩu |
| `fullName` | string | ✅ | - | Họ và tên đầy đủ |
| `phone` | string | ✅ | Phone format | Số điện thoại |
| `dateOfBirth` | DateTime | ✅ | Date format | Ngày sinh (yyyy-MM-dd) |

### Response

#### Success Response (201 Created)
```json
{
  "success": true,
  "message": "Đăng ký thành công.",
  "data": null
}
```

#### Error Response - Email/Phone đã tồn tại (400 Bad Request)
```json
{
  "success": false,
  "message": "Email hoặc số điện thoại đã tồn tại.",
  "data": null
}
```

#### Error Response - Validation Error (400 Bad Request)
```json
{
  "success": false,
  "message": "Invalid product data.",
  "data": {
    // ModelState errors từ ASP.NET Core
  }
}
```

#### Error Response - Server Error (500 Internal Server Error)
```json
{
  "success": false,
  "message": "Đã xảy ra lỗi trong quá trình xử lý.",
  "data": "Error message details"
}
```

### Example Request
```json
{
  "email": "nguyenvana@gmail.com",
  "password": "Password123!",
  "fullName": "Nguyễn Văn A",
  "phone": "0912345678",
  "dateOfBirth": "1990-05-15"
}
```

### Example cURL
```bash
curl -X POST "http://localhost:5000/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "nguyenvana@gmail.com",
    "password": "Password123!",
    "fullName": "Nguyễn Văn A",
    "phone": "0912345678",
    "dateOfBirth": "1990-05-15"
  }'
```

### Lưu Ý
- Email và Phone phải là duy nhất trong hệ thống
- Role "Customer" phải tồn tại trong database
- Mật khẩu sẽ được hash bằng BCrypt trước khi lưu vào database
- User mới tạo sẽ có `IsActive = true` và `IsGuest = false`

---

## 2. Đăng Ký Tài Khoản Staff

### Endpoint
```
POST /api/auth/register/staff
```

### Mô Tả
Đăng ký tài khoản nhân viên mới. Sau khi đăng ký thành công, user sẽ được gán role "Staff" tự động và tạo record trong bảng Staff.

### Request

#### Headers
```
Content-Type: application/json
```

#### Body (JSON)
```json
{
  "email": "string (required, email format)",
  "password": "string (required)",
  "fullName": "string (required)",
  "phone": "string (required, phone format)",
  "dateOfBirth": "datetime (required, format: yyyy-MM-dd)",
  "employeeCode": "string (required)",
  "position": "string (required)"
}
```

#### Schema (RegisterStaffDto)
| Field | Type | Required | Validation | Mô Tả |
|-------|------|----------|------------|-------|
| `email` | string | ✅ | Email format | Email đăng ký |
| `password` | string | ✅ | - | Mật khẩu |
| `fullName` | string | ✅ | - | Họ và tên đầy đủ |
| `phone` | string | ✅ | Phone format | Số điện thoại |
| `dateOfBirth` | DateTime | ✅ | Date format | Ngày sinh (yyyy-MM-dd) |
| `employeeCode` | string | ✅ | - | Mã nhân viên |
| `position` | string | ✅ | - | Chức vụ |

### Response

#### Success Response (201 Created)
```json
{
  "success": true,
  "message": "Đăng ký thành công.",
  "data": null
}
```

#### Error Response - Email/Phone đã tồn tại (400 Bad Request)
```json
{
  "success": false,
  "message": "Email hoặc số điện thoại đã tồn tại.",
  "data": null
}
```

#### Error Response - Server Error (500 Internal Server Error)
```json
{
  "success": false,
  "message": "Đã xảy ra lỗi trong quá trình xử lý.",
  "data": "Error message details"
}
```

### Example Request
```json
{
  "email": "staff001@serenecandle.com",
  "password": "Staff@123",
  "fullName": "Trần Thị B",
  "phone": "0987654321",
  "dateOfBirth": "1995-03-20",
  "employeeCode": "STF001",
  "position": "Nhân viên bán hàng"
}
```

### Example cURL
```bash
curl -X POST "http://localhost:5000/api/auth/register/staff" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "staff001@serenecandle.com",
    "password": "Staff@123",
    "fullName": "Trần Thị B",
    "phone": "0987654321",
    "dateOfBirth": "1995-03-20",
    "employeeCode": "STF001",
    "position": "Nhân viên bán hàng"
  }'
```

### Lưu Ý
- Email và Phone phải là duy nhất trong hệ thống
- Role "Staff" phải tồn tại trong database
- Sau khi tạo User, hệ thống sẽ tự động tạo record trong bảng Staff với thông tin EmployeeCode và Position
- Mật khẩu sẽ được hash bằng BCrypt trước khi lưu vào database

---

## 3. Đăng Ký Tài Khoản Admin

### Endpoint
```
POST /api/auth/register/admin
```

### Mô Tả
Đăng ký tài khoản Admin mới. **Lưu ý: API này chỉ nên dùng cho Development/Testing**. Sau khi đăng ký thành công, user sẽ được gán role "Admin" tự động và tạo record trong bảng Staff.

### Request

#### Headers
```
Content-Type: application/json
```

#### Body (JSON)
```json
{
  "email": "string (required, email format)",
  "password": "string (required)",
  "fullName": "string (required)",
  "phone": "string (required, phone format)",
  "dateOfBirth": "datetime (required, format: yyyy-MM-dd)",
  "employeeCode": "string (required)",
  "position": "string (required)"
}
```

#### Schema (RegisterStaffDto)
| Field | Type | Required | Validation | Mô Tả |
|-------|------|----------|------------|-------|
| `email` | string | ✅ | Email format | Email đăng ký |
| `password` | string | ✅ | - | Mật khẩu |
| `fullName` | string | ✅ | - | Họ và tên đầy đủ |
| `phone` | string | ✅ | Phone format | Số điện thoại |
| `dateOfBirth` | DateTime | ✅ | Date format | Ngày sinh (yyyy-MM-dd) |
| `employeeCode` | string | ✅ | - | Mã nhân viên |
| `position` | string | ✅ | - | Chức vụ |

### Response

#### Success Response (201 Created)
```json
{
  "success": true,
  "message": "Tài khoản Admin đã được tạo thành công.",
  "data": null
}
```

#### Error Response - Email/Phone đã tồn tại (400 Bad Request)
```json
{
  "success": false,
  "message": "Email hoặc số điện thoại đã tồn tại.",
  "data": null
}
```

#### Error Response - Role không tồn tại (500 Internal Server Error)
```json
{
  "success": false,
  "message": "Đã xảy ra lỗi trong quá trình xử lý. Vui lòng đảm bảo role 'Admin' đã tồn tại trong database.",
  "data": "Role 'Admin' not found."
}
```

#### Error Response - Server Error (500 Internal Server Error)
```json
{
  "success": false,
  "message": "Đã xảy ra lỗi trong quá trình xử lý.",
  "data": "Error message details"
}
```

### Example Request
```json
{
  "email": "admin@serenecandle.com",
  "password": "Admin@123",
  "fullName": "Nguyễn Admin",
  "phone": "0123456789",
  "dateOfBirth": "1990-01-01",
  "employeeCode": "ADM001",
  "position": "Quản trị viên"
}
```

### Example cURL
```bash
curl -X POST "http://localhost:5000/api/auth/register/admin" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@serenecandle.com",
    "password": "Admin@123",
    "fullName": "Nguyễn Admin",
    "phone": "0123456789",
    "dateOfBirth": "1990-01-01",
    "employeeCode": "ADM001",
    "position": "Quản trị viên"
  }'
```

### Lưu Ý
- **⚠️ CẢNH BÁO: API này chỉ nên dùng cho Development/Testing**
- Email và Phone phải là duy nhất trong hệ thống
- Role "Admin" phải tồn tại trong database, nếu không sẽ có lỗi
- Sau khi tạo User, hệ thống sẽ tự động tạo record trong bảng Staff
- Mật khẩu sẽ được hash bằng BCrypt trước khi lưu vào database

---

## Cấu Trúc Response Chung

Tất cả các API đều trả về format `ApiResponse<T>` với cấu trúc:

```typescript
interface ApiResponse<T> {
  success: boolean;      // true nếu thành công, false nếu có lỗi
  message: string;        // Thông báo kết quả
  data: T | null;         // Dữ liệu trả về (nếu có)
}
```

---

## Status Codes

| Status Code | Mô Tả |
|-------------|-------|
| 201 Created | Đăng ký thành công |
| 400 Bad Request | Dữ liệu không hợp lệ hoặc email/phone đã tồn tại |
| 500 Internal Server Error | Lỗi server (role không tồn tại, lỗi database, v.v.) |

---

## Validation Rules

### Email
- Phải đúng định dạng email
- Bắt buộc (Required)
- Phải duy nhất trong hệ thống

### Phone
- Phải đúng định dạng số điện thoại
- Bắt buộc (Required)
- Phải duy nhất trong hệ thống

### Password
- Bắt buộc (Required)
- Không có validation format ở backend (nên validate ở frontend)

### FullName
- Bắt buộc (Required)
- Không được rỗng

### DateOfBirth
- Bắt buộc (Required)
- Định dạng: `yyyy-MM-dd`
- Ví dụ: `1990-05-15`

### EmployeeCode (Staff/Admin)
- Bắt buộc (Required)
- Không được rỗng

### Position (Staff/Admin)
- Bắt buộc (Required)
- Không được rỗng

---

## Lưu Ý Quan Trọng Cho Frontend

1. **Validation**: Nên validate dữ liệu ở frontend trước khi gửi request để tăng trải nghiệm người dùng
2. **Date Format**: Ngày sinh phải gửi theo format `yyyy-MM-dd` (ví dụ: `1990-05-15`)
3. **Error Handling**: 
   - Kiểm tra `success === false` để xử lý lỗi
   - Hiển thị `message` cho người dùng
   - Với validation errors (400), có thể có `data` chứa chi tiết các lỗi
4. **Success Handling**: 
   - Status 201 nghĩa là tạo thành công
   - Có thể redirect đến trang đăng nhập sau khi đăng ký thành công
5. **Loading State**: Nên hiển thị loading indicator trong khi gửi request
6. **Toast/Notification**: Nên hiển thị thông báo thành công/lỗi cho người dùng

---

## Flow Đăng Ký

```
1. User điền form đăng ký
   ↓
2. Frontend validate dữ liệu
   ↓
3. Gửi POST request đến API
   ↓
4. Backend kiểm tra:
   - Email/Phone đã tồn tại?
   - Role có tồn tại?
   - Dữ liệu hợp lệ?
   ↓
5. Hash password và lưu vào database
   ↓
6. Trả về response (201 Created hoặc error)
   ↓
7. Frontend xử lý response:
   - Success: Hiển thị thông báo, redirect đến đăng nhập
   - Error: Hiển thị thông báo lỗi
```

---

## Example Code (JavaScript/TypeScript)

### Fetch API
```javascript
const registerCustomer = async (registerData) => {
  try {
    const response = await fetch('http://localhost:5000/api/auth/register', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        email: registerData.email,
        password: registerData.password,
        fullName: registerData.fullName,
        phone: registerData.phone,
        dateOfBirth: registerData.dateOfBirth // Format: "1990-05-15"
      })
    });

    const data = await response.json();

    if (data.success) {
      // Đăng ký thành công
      alert(data.message);
      // Redirect đến trang đăng nhập
    } else {
      // Hiển thị lỗi
      alert(data.message);
    }
  } catch (error) {
    console.error('Error:', error);
    alert('Đã xảy ra lỗi kết nối');
  }
};
```

### Axios
```javascript
import axios from 'axios';

const registerCustomer = async (registerData) => {
  try {
    const response = await axios.post(
      'http://localhost:5000/api/auth/register',
      {
        email: registerData.email,
        password: registerData.password,
        fullName: registerData.fullName,
        phone: registerData.phone,
        dateOfBirth: registerData.dateOfBirth
      }
    );

    if (response.data.success) {
      alert(response.data.message);
      // Redirect đến trang đăng nhập
    }
  } catch (error) {
    if (error.response) {
      // Server trả về error response
      alert(error.response.data.message);
    } else {
      // Lỗi network hoặc lỗi khác
      alert('Đã xảy ra lỗi kết nối');
    }
  }
};
```

---

## Changelog

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2024 | Tài liệu ban đầu |

---

**Generated by: API Documentation Tool**  
**Last Updated: 2024**

