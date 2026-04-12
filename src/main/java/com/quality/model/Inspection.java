package com.quality.model;

import java.time.LocalDateTime;

public class Inspection extends Entity {
    private static final long serialVersionUID = 1L;

    private int productId;
    private String productName;
    private int standardId;
    private String standardName;
    private int inspectorId;
    private String inspectorName;
    private LocalDateTime inspectionDate;
    private String batchNumber;
    private double overallScore;
    private String status; // PENDING, PASSED, FAILED
    private String notes;

    public Inspection() {
        this.status = "PENDING";
        this.inspectionDate = LocalDateTime.now();
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getStandardId() { return standardId; }
    public void setStandardId(int standardId) { this.standardId = standardId; }

    public String getStandardName() { return standardName; }
    public void setStandardName(String standardName) { this.standardName = standardName; }

    public int getInspectorId() { return inspectorId; }
    public void setInspectorId(int inspectorId) { this.inspectorId = inspectorId; }

    public String getInspectorName() { return inspectorName; }
    public void setInspectorName(String inspectorName) { this.inspectorName = inspectorName; }

    public LocalDateTime getInspectionDate() { return inspectionDate; }
    public void setInspectionDate(LocalDateTime inspectionDate) { this.inspectionDate = inspectionDate; }

    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }

    public double getOverallScore() { return overallScore; }
    public void setOverallScore(double overallScore) { this.overallScore = overallScore; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "Inspection{id=" + id + ", product='" + productName +
                "', score=" + overallScore + ", status='" + status + "'}";
    }
}