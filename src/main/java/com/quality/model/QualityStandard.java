package com.quality.model;

import java.time.LocalDate;

public class QualityStandard extends Entity {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private LocalDate effectiveDate;
    private int categoryId;
    private String categoryName;

    public QualityStandard() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    @Override
    public String toString() {
        return "QualityStandard{id=" + id + ", name='" + name + "'}";
    }
}