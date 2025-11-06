package com.example.sereincandle.models;

import com.google.gson.annotations.SerializedName;

/**
 * Model cho attributes của sản phẩm khi tạo/cập nhật
 */
public class ProductAttributeValue {
    @SerializedName("AttributeId")
    private int attributeId;  // required
    
    @SerializedName("Value")
    private String value;     // required

    public ProductAttributeValue() {
    }

    public ProductAttributeValue(int attributeId, String value) {
        this.attributeId = attributeId;
        this.value = value;
    }

    public int getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(int attributeId) {
        this.attributeId = attributeId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
