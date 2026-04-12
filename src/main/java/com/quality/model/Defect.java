package com.quality.model;

import java.time.LocalDateTime;

public class Defect extends Entity {
    private static final long serialVersionUID = 1L;

    private int inspectionId;
    private int defectTypeId;
    private String defectTypeName;
    private String description;
    private int quantity;
    private LocalDateTime detectedAt;

    public Defect() {
        this.quantity = 1;
        this.detectedAt = LocalDateTime.now();
    }

    public int getInspectionId() { return inspectionId; }
    public void setInspectionId(int inspectionId) { this.inspectionId = inspectionId; }

    public int getDefectTypeId() { return defectTypeId; }
    public void setDefectTypeId(int defectTypeId) { this.defectTypeId = defectTypeId; }

    public String getDefectTypeName() { return defectTypeName; }
    public void setDefectTypeName(String defectTypeName) { this.defectTypeName = defectTypeName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public LocalDateTime getDetectedAt() { return detectedAt; }
    public void setDetectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; }

    @Override
    public String toString() {
        return "Defect{id=" + id + ", type='" + defectTypeName +
                "', quantity=" + quantity + "}";
    }
}