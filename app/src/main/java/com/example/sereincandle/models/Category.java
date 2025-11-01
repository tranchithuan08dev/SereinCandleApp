package com.example.sereincandle.models;

/**
 * Model cho Category
 * Mapping với SQL: Category table
 */
public class Category {
    private int categoryId;          // Category.CategoryId
    private String name;             // Category.Name
    private String slug;             // Category.Slug
    private String description;      // Category.Description
    private Integer parentCategoryId; // Category.ParentCategoryId (nullable, nested categories)

    public Category() {
    }

    public Category(int categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Integer parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    @Override
    public String toString() {
        return name; // Để hiển thị trong Spinner
    }
}
