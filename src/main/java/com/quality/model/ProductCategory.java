package com.quality.model;

public class ProductCategory extends Entity {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;

    public ProductCategory() {}

    public ProductCategory(int id, String name) {
        super(id);
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "ProductCategory{id=" + id + ", name='" + name + "'}";
    }
}