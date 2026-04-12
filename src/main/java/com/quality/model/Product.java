package com.quality.model;

public class Product extends Entity {
    private static final long serialVersionUID = 1L;

    private String name;
    private String article;
    private int categoryId;
    private String categoryName;
    private String description;

    public Product() {}

    public Product(String name, String article) {
        this.name = name;
        this.article = article;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getArticle() { return article; }
    public void setArticle(String article) { this.article = article; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "Product{id=" + id + ", name='" + name + "', article='" + article + "'}";
    }
}