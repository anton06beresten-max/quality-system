package com.quality.model;

public class QualityCriteria extends Entity {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private String unit;

    public QualityCriteria() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    @Override
    public String toString() {
        return "QualityCriteria{id=" + id + ", name='" + name + "', unit='" + unit + "'}";
    }
}