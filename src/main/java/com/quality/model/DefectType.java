package com.quality.model;

public class DefectType extends Entity {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL

    public DefectType() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    @Override
    public String toString() {
        return "DefectType{id=" + id + ", name='" + name + "', severity='" + severity + "'}";
    }
}