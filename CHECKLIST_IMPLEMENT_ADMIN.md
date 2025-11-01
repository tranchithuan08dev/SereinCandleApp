# ✅ CHECKLIST IMPLEMENT LUỒNG ADMIN

## 📋 QUICK CHECKLIST - Theo dõi tiến độ

---

## 🔧 PHASE 1: SETUP CƠ BẢN (2-3 giờ)

### **Bước 1.1: Cập nhật MainActivity**
- [ ] Đọc `roleName` từ `LoginResponse.getData()`
- [ ] Lưu role vào SharedPreferences (key: `USER_ROLE`)
- [ ] Điều hướng Admin → `AdminHomeActivity`
- [ ] Điều hướng Customer → `HomePageActivity` (như cũ)
- [ ] Test: Login với Admin → Check chuyển đúng màn hình
- [ ] Test: Login với Customer → Check chuyển đúng màn hình

### **Bước 1.2: Tạo SessionManager**
- [ ] Tạo package `utils/` (nếu chưa có)
- [ ] Tạo file `SessionManager.java`
- [ ] Implement: `saveToken()`, `getToken()`
- [ ] Implement: `saveRole()`, `getRole()`
- [ ] Implement: `isAdmin()` helper method
- [ ] Implement: `logout()` method

### **Bước 1.3: Tạo AdminHomeActivity**
- [ ] Tạo layout `activity_admin_home.xml`
- [ ] Tạo file `AdminHomeActivity.java`
- [ ] Hiển thị thông tin admin (tên, email)
- [ ] Nút "Quản lý Sản phẩm" → `AdminProductListActivity`
- [ ] Nút "Quản lý Đơn hàng" → `AdminOrderListActivity`
- [ ] Nút "Quản lý Người dùng" → `AdminUserListActivity` (optional)
- [ ] Nút "Đăng xuất" → Clear session → `MainActivity`

### **Bước 1.4: Update AndroidManifest**
- [ ] Đăng ký `AdminHomeActivity` trong AndroidManifest.xml

**Kết quả Phase 1:** ✅ PASS / ❌ FAIL

---

## 📦 PHASE 2: QUẢN LÝ SẢN PHẨM (4-6 giờ)

### **Bước 2.1: Models & API**
- [ ] Tạo `ProductRequest.java` model
- [ ] Update `ApiService.java`:
  - [ ] `GET Admin/Products` (list)
  - [ ] `GET Admin/Products/{id}` (detail)
  - [ ] `POST Admin/Products` (create)
  - [ ] `PUT Admin/Products/{id}` (update)
  - [ ] `DELETE Admin/Products/{id}` (delete)

### **Bước 2.2: AdminProductListActivity**
- [ ] Tạo layout `activity_admin_product_list.xml`
- [ ] Tạo `AdminProductListActivity.java`
- [ ] Implement RecyclerView hiển thị danh sách
- [ ] Implement `fetchProducts()` → Gọi `GET Admin/Products`
- [ ] Mỗi item hiển thị: Ảnh, Tên, Giá, Trạng thái
- [ ] Nút "Sửa" trên mỗi item → `AdminProductDetailActivity`
- [ ] Nút "Xóa" trên mỗi item → Confirm dialog → `DELETE API`
- [ ] FloatingActionButton "Thêm" → `AdminProductCreateActivity`
- [ ] SearchView (optional)
- [ ] Pull to Refresh (optional)

### **Bước 2.3: AdminProductDetailActivity (Xem/Sửa)**
- [ ] Tạo layout `activity_admin_product_detail.xml`
- [ ] Tạo `AdminProductDetailActivity.java`
- [ ] Load chi tiết: `GET Admin/Products/{id}`
- [ ] Form EditText: Tên, Mô tả, Giá, SKU, Thành phần, Thời gian cháy
- [ ] Switch/CheckBox: Trạng thái Active/Inactive
- [ ] Button "Lưu thay đổi" → `PUT Admin/Products/{id}`
- [ ] Button "Xóa sản phẩm" → Confirm → `DELETE Admin/Products/{id}`
- [ ] Button "Hủy" → Back

### **Bước 2.4: AdminProductCreateActivity (Thêm mới)**
- [ ] Tạo layout `activity_admin_product_create.xml`
- [ ] Tạo `AdminProductCreateActivity.java`
- [ ] Form EditText: Tên, Mô tả, Giá, SKU, Thành phần, Thời gian cháy
- [ ] Switch: Trạng thái Active
- [ ] Button "Chọn ảnh" → Gallery/Camera picker
- [ ] ImageView preview ảnh đã chọn
- [ ] Button "Tạo sản phẩm" → Validate → `POST Admin/Products`
- [ ] Button "Hủy" → Back
- [ ] Validation: Không được bỏ trống tên, giá > 0

**Kết quả Phase 2:** ✅ PASS / ❌ FAIL

---

## 📋 PHASE 3: QUẢN LÝ ĐƠN HÀNG (3-4 giờ)

### **Bước 3.1: Models & API**
- [ ] Tạo `Order.java` model
- [ ] Tạo `OrderItem.java` model
- [ ] Tạo `OrderListResponse.java` model
- [ ] Tạo `OrderDetailResponse.java` model
- [ ] Tạo `OrderStatusRequest.java` model
- [ ] Update `ApiService.java`:
  - [ ] `GET Admin/Orders` (list với filter)
  - [ ] `GET Admin/Orders/{id}` (detail)
  - [ ] `PUT Admin/Orders/{id}/status` (update status)

### **Bước 3.2: AdminOrderListActivity**
- [ ] Tạo layout `activity_admin_order_list.xml`
- [ ] Tạo layout `item_order.xml` (RecyclerView item)
- [ ] Tạo `AdminOrderListActivity.java`
- [ ] Implement RecyclerView hiển thị danh sách đơn hàng
- [ ] Implement `fetchOrders()` → `GET Admin/Orders`
- [ ] Mỗi item hiển thị:
  - [ ] Order ID
  - [ ] Tên khách hàng
  - [ ] Số điện thoại
  - [ ] Tổng tiền
  - [ ] Trạng thái (màu sắc)
  - [ ] Ngày đặt hàng
- [ ] Spinner lọc theo trạng thái (All, Pending, Confirmed, Shipping, Delivered, Cancelled)
- [ ] SearchView tìm kiếm (optional)
- [ ] Nhấn item → `AdminOrderDetailActivity`
- [ ] Pull to Refresh

### **Bước 3.3: AdminOrderDetailActivity**
- [ ] Tạo layout `activity_admin_order_detail.xml`
- [ ] Tạo layout `item_order_product.xml` (item trong đơn)
- [ ] Tạo `OrderItemAdapter.java` (adapter cho products trong đơn)
- [ ] Tạo `AdminOrderDetailActivity.java`
- [ ] Load chi tiết: `GET Admin/Orders/{id}`
- [ ] Hiển thị:
  - [ ] Order ID, Ngày đặt
  - [ ] Thông tin khách hàng (Tên, SĐT, Địa chỉ đầy đủ)
  - [ ] RecyclerView: Danh sách sản phẩm trong đơn
  - [ ] Tổng tiền
  - [ ] Ghi chú (nếu có)
- [ ] Spinner chọn trạng thái mới
- [ ] Button "Cập nhật trạng thái" → `PUT Admin/Orders/{id}/status`

**Kết quả Phase 3:** ✅ PASS / ❌ FAIL

---

## 👥 PHASE 4: QUẢN LÝ NGƯỜI DÙNG (Optional - 2-3 giờ)

### **Bước 4.1: Models & API**
- [ ] Tạo `User.java` model
- [ ] Tạo `UserListResponse.java` model
- [ ] Update `ApiService.java`:
  - [ ] `GET Admin/Users` (list với search)

### **Bước 4.2: AdminUserListActivity**
- [ ] Tạo layout `activity_admin_user_list.xml`
- [ ] Tạo layout `item_user.xml`
- [ ] Tạo `AdminUserListActivity.java`
- [ ] Implement RecyclerView hiển thị danh sách users
- [ ] Implement `fetchUsers()` → `GET Admin/Users`
- [ ] Mỗi item hiển thị: Tên, Email, Role, Trạng thái
- [ ] SearchView tìm kiếm

**Kết quả Phase 4:** ✅ PASS / ❌ FAIL / ⏸️ SKIPPED

---

## 🧪 PHASE 5: TESTING & POLISH (2-3 giờ)

### **Testing**
- [ ] Test login với Admin → Điều hướng đúng
- [ ] Test login với Customer → Điều hướng đúng (không bị ảnh hưởng)
- [ ] Test CRUD sản phẩm:
  - [ ] Xem danh sách
  - [ ] Thêm sản phẩm mới
  - [ ] Sửa sản phẩm
  - [ ] Xóa sản phẩm
- [ ] Test quản lý đơn hàng:
  - [ ] Xem danh sách đơn hàng
  - [ ] Lọc theo trạng thái
  - [ ] Xem chi tiết đơn hàng
  - [ ] Cập nhật trạng thái đơn hàng
- [ ] Test edge cases:
  - [ ] Validation khi tạo/sửa sản phẩm
  - [ ] Xử lý lỗi (401, 403, 404, network)
  - [ ] Token hết hạn → Logout

### **Polish**
- [ ] Thêm loading indicators
- [ ] Cải thiện UI/UX
- [ ] Thêm confirm dialogs cho actions quan trọng
- [ ] Thêm error messages rõ ràng
- [ ] Update AndroidManifest.xml (đăng ký tất cả Activity mới)

### **Documentation**
- [ ] Tạo file hướng dẫn test Admin (tương tự Customer)
- [ ] Update README (nếu có)

**Kết quả Phase 5:** ✅ PASS / ❌ FAIL

---

## 📊 PROGRESS TRACKING

### **Tổng số tasks:** ~50 tasks

### **Đã hoàn thành:**
- Phase 1: [ ] 0 / 14 tasks
- Phase 2: [ ] 0 / 18 tasks
- Phase 3: [ ] 0 / 13 tasks
- Phase 4: [ ] 0 / 8 tasks
- Phase 5: [ ] 0 / 10 tasks

### **Tổng tiến độ:** 0% 

### **Ghi chú:**
_________________________________
_________________________________
_________________________________

---

## 🐛 BUGS FOUND

### Bug #1
- Mô tả: _________________________________
- File: _________________________________
- Status: [ ] Fixed [ ] Pending

### Bug #2
- Mô tả: _________________________________
- File: _________________________________
- Status: [ ] Fixed [ ] Pending

---

## 💡 NOTES & IDEAS

_________________________________
_________________________________
_________________________________

---

**Last Updated:** _____  
**Current Phase:** Phase ___

---

## 🚀 QUICK START

1. **Bắt đầu từ Phase 1:**
   ```
   - Đọc file KE_HOACH_LUONG_ADMIN.md để hiểu chi tiết
   - Update MainActivity.java
   - Tạo SessionManager.java
   - Tạo AdminHomeActivity.java
   ```

2. **Verify Backend:**
   ```
   - Đảm bảo backend có các API endpoints Admin
   - Test API bằng Postman trước khi code Android
   ```

3. **Test ngay:**
   ```
   - Sau mỗi phase, test kỹ trước khi chuyển phase tiếp theo
   - Không tích lũy bugs
   ```

---

**Good luck! 🎉**

