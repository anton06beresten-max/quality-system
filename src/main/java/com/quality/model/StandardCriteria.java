package com.quality.model;

public class StandardCriteria extends Entity {
    private static final long serialVersionUID = 1L;

    private int standardId;
    private int criterionId;
    private String criterionName;
    private String criterionUnit;
    private double minValue;
    private double maxValue;
    private double weight;

    public StandardCriteria() {}

    public int getStandardId() { return standardId; }
    public void setStandardId(int standardId) { this.standardId = standardId; }

    public int getCriterionId() { return criterionId; }
    public void setCriterionId(int criterionId) { this.criterionId = criterionId; }

    public String getCriterionName() { return criterionName; }
    public void setCriterionName(String criterionName) { this.criterionName = criterionName; }

    public String getCriterionUnit() { return criterionUnit; }
    public void setCriterionUnit(String criterionUnit) { this.criterionUnit = criterionUnit; }

    public double getMinValue() { return minValue; }
    public void setMinValue(double minValue) { this.minValue = minValue; }

    public double getMaxValue() { return maxValue; }
    public void setMaxValue(double maxValue) { this.maxValue = maxValue; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    @Override
    public String toString() {
        return "StandardCriteria{standardId=" + standardId +
                ", criterion='" + criterionName + "', weight=" + weight + "}";
    }
}