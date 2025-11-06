# Tài Liệu API Tạo Mới Sản Phẩm - Serein Candle Shop

## Endpoint
```
POST /api/product
```

## Content-Type
```
multipart/form-data
```

## Mô Tả
API này yêu cầu gửi dữ liệu dưới dạng **FormData** (multipart/form-data), không phải JSON. Điều này là do API cần nhận cả dữ liệu sản phẩm và file ảnh cùng lúc.

---

## Request Format

### ⚠️ QUAN TRỌNG: Phải sử dụng FormData, không phải JSON

API nhận 2 loại dữ liệu:
1. **Dữ liệu sản phẩm** (InsertProductDto) - dưới dạng form fields
2. **File ảnh** (IFormFileCollection) - dưới dạng file uploads

---

## Request Body Structure

### FormData Fields

| Field Name | Type | Required | Mô Tả | Example |
|------------|------|----------|-------|---------|
| `Name` | string | ✅ | Tên sản phẩm | "Nến thơm Lavender" |
| `SKU` | string | ❌ | Mã SKU sản phẩm | "SKU001" |
| `ShortDescription` | string | ❌ | Mô tả ngắn | "Nến thơm cao cấp" |
| `Description` | string | ✅ | Mô tả chi tiết | "Nến thơm được làm từ sáp ong tự nhiên..." |
| `Ingredients` | string | ❌ | Thành phần | "Sáp ong, tinh dầu lavender" |
| `BurnTime` | string | ❌ | Thời gian cháy | "40 giờ" |
| `Price` | decimal | ✅ | Giá sản phẩm | 250000 |
| `CategoryId` | int | ✅ | ID danh mục | 1 |
| `Attributes[0].AttributeId` | int | ✅ | ID thuộc tính (nếu có) | 1 |
| `Attributes[0].Value` | string | ✅ | Giá trị thuộc tính (nếu có) | "500g" |
| `Attributes[1].AttributeId` | int | ✅ | ID thuộc tính thứ 2 (nếu có) | 2 |
| `Attributes[1].Value` | string | ✅ | Giá trị thuộc tính thứ 2 (nếu có) | "Xanh dương" |
| `images` | File[] | ✅ | Mảng file ảnh (bắt buộc) | File objects |

### Lưu ý về Attributes:
- Attributes là một mảng, mỗi phần tử có 2 trường: `AttributeId` và `Value`
- Nếu có nhiều attributes, đánh số index: `Attributes[0]`, `Attributes[1]`, `Attributes[2]`, ...
- Nếu không có attributes, có thể bỏ qua hoặc gửi mảng rỗng

---

## Validation Rules

### Required Fields (Bắt buộc):
- ✅ `Name` - Tên sản phẩm
- ✅ `Description` - Mô tả chi tiết
- ✅ `Price` - Giá sản phẩm (phải là số)
- ✅ `CategoryId` - ID danh mục (phải là số nguyên)
- ✅ `images` - Ít nhất 1 file ảnh

### Optional Fields (Tùy chọn):
- ❌ `SKU` - Mã SKU
- ❌ `ShortDescription` - Mô tả ngắn
- ❌ `Ingredients` - Thành phần
- ❌ `BurnTime` - Thời gian cháy
- ❌ `Attributes` - Thuộc tính sản phẩm

---

## Example Request (JavaScript/TypeScript)

### Sử dụng FormData

```javascript
const createProduct = async (productData, imageFiles) => {
  // Tạo FormData object
  const formData = new FormData();

  // Thêm các trường dữ liệu sản phẩm
  formData.append('Name', productData.name);
  formData.append('SKU', productData.sku || '');
  formData.append('ShortDescription', productData.shortDescription || '');
  formData.append('Description', productData.description);
  formData.append('Ingredients', productData.ingredients || '');
  formData.append('BurnTime', productData.burnTime || '');
  formData.append('Price', productData.price.toString());
  formData.append('CategoryId', productData.categoryId.toString());

  // Thêm Attributes (nếu có)
  if (productData.attributes && productData.attributes.length > 0) {
    productData.attributes.forEach((attr, index) => {
      formData.append(`Attributes[${index}].AttributeId`, attr.attributeId.toString());
      formData.append(`Attributes[${index}].Value`, attr.value);
    });
  }

  // Thêm file ảnh (bắt buộc)
  // images phải là một mảng File objects
  imageFiles.forEach((file, index) => {
    formData.append('images', file);
  });

  try {
    const response = await fetch('http://localhost:5000/api/product', {
      method: 'POST',
      body: formData
      // KHÔNG set Content-Type header, browser sẽ tự động set với boundary
    });

    const data = await response.json();

    if (data.success) {
      console.log('Sản phẩm đã được tạo thành công!');
      return data;
    } else {
      console.error('Lỗi:', data.message);
      return null;
    }
  } catch (error) {
    console.error('Error:', error);
    return null;
  }
};

// Sử dụng
const productData = {
  name: 'Nến thơm Lavender',
  sku: 'SKU001',
  shortDescription: 'Nến thơm cao cấp',
  description: 'Nến thơm được làm từ sáp ong tự nhiên, có mùi hương lavender dịu nhẹ',
  ingredients: 'Sáp ong, tinh dầu lavender',
  burnTime: '40 giờ',
  price: 250000,
  categoryId: 1,
  attributes: [
    { attributeId: 1, value: '500g' },
    { attributeId: 2, value: 'Xanh dương' }
  ]
};

// imageFiles là mảng File objects từ input file
const imageFiles = document.getElementById('imageInput').files; // HTMLInputElement.files

createProduct(productData, imageFiles);
```

### Sử dụng Axios

```javascript
import axios from 'axios';

const createProduct = async (productData, imageFiles) => {
  const formData = new FormData();

  // Thêm các trường dữ liệu
  formData.append('Name', productData.name);
  formData.append('SKU', productData.sku || '');
  formData.append('ShortDescription', productData.shortDescription || '');
  formData.append('Description', productData.description);
  formData.append('Ingredients', productData.ingredients || '');
  formData.append('BurnTime', productData.burnTime || '');
  formData.append('Price', productData.price.toString());
  formData.append('CategoryId', productData.categoryId.toString());

  // Thêm Attributes
  if (productData.attributes && productData.attributes.length > 0) {
    productData.attributes.forEach((attr, index) => {
      formData.append(`Attributes[${index}].AttributeId`, attr.attributeId.toString());
      formData.append(`Attributes[${index}].Value`, attr.value);
    });
  }

  // Thêm file ảnh
  imageFiles.forEach((file) => {
    formData.append('images', file);
  });

  try {
    const response = await axios.post(
      'http://localhost:5000/api/product',
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }
    );

    if (response.data.success) {
      console.log('Sản phẩm đã được tạo thành công!');
      return response.data;
    }
  } catch (error) {
    if (error.response) {
      console.error('Lỗi từ server:', error.response.data);
    } else {
      console.error('Lỗi:', error.message);
    }
    return null;
  }
};
```

---

## Example Request (React Component)

```jsx
import React, { useState } from 'react';
import axios from 'axios';

const CreateProductForm = () => {
  const [formData, setFormData] = useState({
    name: '',
    sku: '',
    shortDescription: '',
    description: '',
    ingredients: '',
    burnTime: '',
    price: '',
    categoryId: '',
    attributes: []
  });
  const [images, setImages] = useState([]);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    const formDataToSend = new FormData();

    // Thêm các trường dữ liệu
    formDataToSend.append('Name', formData.name);
    formDataToSend.append('SKU', formData.sku);
    formDataToSend.append('ShortDescription', formData.shortDescription);
    formDataToSend.append('Description', formData.description);
    formDataToSend.append('Ingredients', formData.ingredients);
    formDataToSend.append('BurnTime', formData.burnTime);
    formDataToSend.append('Price', formData.price);
    formDataToSend.append('CategoryId', formData.categoryId);

    // Thêm Attributes
    formData.attributes.forEach((attr, index) => {
      formDataToSend.append(`Attributes[${index}].AttributeId`, attr.attributeId);
      formDataToSend.append(`Attributes[${index}].Value`, attr.value);
    });

    // Thêm file ảnh
    images.forEach((file) => {
      formDataToSend.append('images', file);
    });

    try {
      const response = await axios.post(
        'http://localhost:5000/api/product',
        formDataToSend,
        {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        }
      );

      if (response.data.success) {
        alert('Sản phẩm đã được tạo thành công!');
        // Reset form hoặc redirect
      }
    } catch (error) {
      if (error.response) {
        alert('Lỗi: ' + error.response.data.message);
      } else {
        alert('Đã xảy ra lỗi kết nối');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleImageChange = (e) => {
    const files = Array.from(e.target.files);
    setImages(files);
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        placeholder="Tên sản phẩm"
        value={formData.name}
        onChange={(e) => setFormData({ ...formData, name: e.target.value })}
        required
      />
      
      <textarea
        placeholder="Mô tả chi tiết"
        value={formData.description}
        onChange={(e) => setFormData({ ...formData, description: e.target.value })}
        required
      />
      
      <input
        type="number"
        placeholder="Giá"
        value={formData.price}
        onChange={(e) => setFormData({ ...formData, price: e.target.value })}
        required
      />
      
      <input
        type="number"
        placeholder="Category ID"
        value={formData.categoryId}
        onChange={(e) => setFormData({ ...formData, categoryId: e.target.value })}
        required
      />
      
      <input
        type="file"
        multiple
        accept="image/*"
        onChange={handleImageChange}
        required
      />
      
      <button type="submit" disabled={loading}>
        {loading ? 'Đang tạo...' : 'Tạo sản phẩm'}
      </button>
    </form>
  );
};

export default CreateProductForm;
```

---

## Example Request (HTML Form)

```html
<form id="productForm" enctype="multipart/form-data">
  <input type="text" name="Name" placeholder="Tên sản phẩm" required />
  <input type="text" name="SKU" placeholder="Mã SKU" />
  <textarea name="ShortDescription" placeholder="Mô tả ngắn"></textarea>
  <textarea name="Description" placeholder="Mô tả chi tiết" required></textarea>
  <input type="text" name="Ingredients" placeholder="Thành phần" />
  <input type="text" name="BurnTime" placeholder="Thời gian cháy" />
  <input type="number" name="Price" placeholder="Giá" step="0.01" required />
  <input type="number" name="CategoryId" placeholder="Category ID" required />
  
  <!-- Attributes -->
  <div id="attributes">
    <input type="number" name="Attributes[0].AttributeId" placeholder="Attribute ID" />
    <input type="text" name="Attributes[0].Value" placeholder="Giá trị" />
  </div>
  
  <!-- Images - Bắt buộc -->
  <input type="file" name="images" multiple accept="image/*" required />
  
  <button type="submit">Tạo sản phẩm</button>
</form>

<script>
document.getElementById('productForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  
  const formData = new FormData(e.target);
  
  try {
    const response = await fetch('http://localhost:5000/api/product', {
      method: 'POST',
      body: formData
    });
    
    const data = await response.json();
    
    if (data.success) {
      alert('Sản phẩm đã được tạo thành công!');
    } else {
      alert('Lỗi: ' + data.message);
    }
  } catch (error) {
    alert('Đã xảy ra lỗi kết nối');
  }
});
</script>
```

---

## Response Format

### Success Response (200 OK)
```json
{
  "success": true,
  "message": "Product added successfully.",
  "data": null
}
```

### Error Response - Validation Error (400 Bad Request)
```json
{
  "success": false,
  "message": "Invalid product data.",
  "data": {
    // ModelState errors từ ASP.NET Core
    "Name": ["The Name field is required."],
    "Price": ["The Price field is required."]
  }
}
```

### Error Response - No Images (400 Bad Request)
```json
{
  "success": false,
  "message": "Failed to add product. Please check the data and try again.",
  "data": null
}
```
*Lưu ý: Lỗi này xảy ra khi không có ảnh hoặc upload ảnh thất bại*

---

## cURL Example

```bash
curl -X POST "http://localhost:5000/api/product" \
  -F "Name=Nến thơm Lavender" \
  -F "SKU=SKU001" \
  -F "ShortDescription=Nến thơm cao cấp" \
  -F "Description=Nến thơm được làm từ sáp ong tự nhiên" \
  -F "Ingredients=Sáp ong, tinh dầu lavender" \
  -F "BurnTime=40 giờ" \
  -F "Price=250000" \
  -F "CategoryId=1" \
  -F "Attributes[0].AttributeId=1" \
  -F "Attributes[0].Value=500g" \
  -F "Attributes[1].AttributeId=2" \
  -F "Attributes[1].Value=Xanh dương" \
  -F "images=@/path/to/image1.jpg" \
  -F "images=@/path/to/image2.jpg"
```

---

## Lưu Ý Quan Trọng

### 1. Content-Type
- ✅ **PHẢI** sử dụng `multipart/form-data`
- ❌ **KHÔNG** sử dụng `application/json`
- Khi dùng FormData trong JavaScript, **KHÔNG** set header `Content-Type` thủ công, browser sẽ tự động set với boundary

### 2. File Upload
- **Bắt buộc** phải có ít nhất 1 file ảnh
- Có thể upload nhiều ảnh cùng lúc
- Tên field phải là `images` (số nhiều)
- Mỗi file được append riêng: `formData.append('images', file1)`, `formData.append('images', file2)`

### 3. Attributes Array
- Attributes là một mảng, mỗi phần tử có 2 trường
- Format: `Attributes[index].AttributeId` và `Attributes[index].Value`
- Index bắt đầu từ 0
- Nếu không có attributes, có thể bỏ qua

### 4. Data Types
- `Price`: phải là số (decimal)
- `CategoryId`: phải là số nguyên (int)
- `AttributeId`: phải là số nguyên (int)
- Các trường khác là string

### 5. Validation
- Backend sẽ validate dữ liệu
- Nếu validation fail, sẽ trả về 400 Bad Request với ModelState errors
- Frontend nên validate trước khi gửi để tăng UX

---

## Common Mistakes (Lỗi Thường Gặp)

### ❌ SAI: Gửi JSON
```javascript
// SAI - API không nhận JSON
fetch('/api/product', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ Name: 'Product', Price: 100 })
});
```

### ✅ ĐÚNG: Gửi FormData
```javascript
// ĐÚNG - Sử dụng FormData
const formData = new FormData();
formData.append('Name', 'Product');
formData.append('Price', '100');
formData.append('images', file);

fetch('/api/product', {
  method: 'POST',
  body: formData  // Không set Content-Type header
});
```

### ❌ SAI: Tên field không đúng
```javascript
// SAI - Tên field phải đúng case
formData.append('name', 'Product');  // Phải là 'Name'
formData.append('price', '100');     // Phải là 'Price'
```

### ✅ ĐÚNG: Tên field đúng
```javascript
// ĐÚNG - Tên field đúng case
formData.append('Name', 'Product');
formData.append('Price', '100');
```

### ❌ SAI: Gửi ảnh sai format
```javascript
// SAI - Phải là File object, không phải base64
formData.append('images', 'data:image/jpeg;base64,...');
```

### ✅ ĐÚNG: Gửi File object
```javascript
// ĐÚNG - Gửi File object
const fileInput = document.getElementById('imageInput');
formData.append('images', fileInput.files[0]);
```

---

## Testing với Postman

1. Chọn method: **POST**
2. URL: `http://localhost:5000/api/product`
3. Body tab → chọn **form-data**
4. Thêm các fields:
   - `Name` (Text): "Nến thơm Lavender"
   - `Description` (Text): "Mô tả sản phẩm"
   - `Price` (Text): "250000"
   - `CategoryId` (Text): "1"
   - `images` (File): Chọn file ảnh (có thể chọn nhiều)
5. Click **Send**

---

## Summary

| Aspect | Value |
|--------|-------|
| **Method** | POST |
| **Endpoint** | `/api/product` |
| **Content-Type** | `multipart/form-data` |
| **Request Format** | FormData |
| **Images Required** | ✅ Yes (ít nhất 1 ảnh) |
| **Required Fields** | Name, Description, Price, CategoryId, images |
| **Optional Fields** | SKU, ShortDescription, Ingredients, BurnTime, Attributes |

---

**Generated by: API Documentation Tool**  
**Last Updated: 2024**

