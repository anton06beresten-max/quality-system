package com.quality.model;

public class InspectionResult extends Entity {
    private static final long serialVersionUID = 1L;

    private int inspectionId;
    private int criterionId;
    private String criterionName;
    private double actualValue;
    private boolean passed;
    private String notes;

    public InspectionResult() {}

    public int getInspectionId() { return inspectionId; }
    public void setInspectionId(int inspectionId) { this.inspectionId = inspectionId; }

    public int getCriterionId() { return criterionId; }
    public void setCriterionId(int criterionId) { this.criterionId = criterionId; }

    public String getCriterionName() { return criterionName; }
    public void setCriterionName(String criterionName) { this.criterionName = criterionName; }

    public double getActualValue() { return actualValue; }
    public void setActualValue(double actualValue) { this.actualValue = actualValue; }

    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "InspectionResult{inspectionId=" + inspectionId +
                ", criterion='" + criterionName + "', value=" + actualValue +
                ", passed=" + passed + "}";
    }
}