package com.quality.service;

import com.quality.db.dao.*;
import com.quality.model.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InspectionService {

    private final InspectionDAO inspectionDAO = new InspectionDAO();
    private final InspectionResultDAO resultDAO = new InspectionResultDAO();
    private final StandardCriteriaDAO standardCriteriaDAO = new StandardCriteriaDAO();
    private final DefectDAO defectDAO = new DefectDAO();

    /**
     * Проведение комплексной инспекции:
     * 1. Создаёт инспекцию
     * 2. Сохраняет результаты по каждому критерию
     * 3. Проверяет соответствие нормативам
     * 4. Рассчитывает взвешенную оценку
     * 5. Определяет статус PASSED/FAILED
     */
    public Inspection conductInspection(Inspection inspection,
                                        List<InspectionResult> results) throws SQLException {

        // 1. Получаем нормативы стандарта
        List<StandardCriteria> criteria =
                standardCriteriaDAO.findByStandard(inspection.getStandardId());

        // 2. Проверяем каждый результат на соответствие нормативам
        boolean allPassed = true;
        for (InspectionResult result : results) {
            StandardCriteria norm = findNorm(criteria, result.getCriterionId());
            if (norm != null) {
                boolean passed = result.getActualValue() >= norm.getMinValue()
                        && result.getActualValue() <= norm.getMaxValue();
                result.setPassed(passed);
                if (!passed) allPassed = false;
            }
        }

        // 3. Рассчитываем взвешенную оценку
        double overallScore = calculateWeightedScore(results, criteria);
        inspection.setOverallScore(overallScore);

        // 4. Определяем статус
        inspection.setStatus(allPassed ? "PASSED" : "FAILED");

        // 5. Сохраняем инспекцию
        int inspectionId = inspectionDAO.create(inspection);

        // 6. Сохраняем результаты
        for (InspectionResult r : results) {
            r.setInspectionId(inspectionId);
        }
        resultDAO.createBatch(results);

        return inspection;
    }

    /**
     * Расчёт взвешенной оценки качества (0-100)
     */
    private double calculateWeightedScore(List<InspectionResult> results,
                                          List<StandardCriteria> criteria) {
        double totalWeight = 0;
        double weightedSum = 0;

        for (InspectionResult result : results) {
            StandardCriteria norm = findNorm(criteria, result.getCriterionId());
            if (norm != null) {
                double weight = norm.getWeight();
                totalWeight += weight;

                // Оценка критерия: 100 если в пределах нормы,
                // пропорционально уменьшается при отклонении
                double score;
                if (result.isPassed()) {
                    score = 100.0;
                } else {
                    double range = norm.getMaxValue() - norm.getMinValue();
                    if (range <= 0) range = 1;
                    double deviation;
                    if (result.getActualValue() < norm.getMinValue()) {
                        deviation = norm.getMinValue() - result.getActualValue();
                    } else {
                        deviation = result.getActualValue() - norm.getMaxValue();
                    }
                    score = Math.max(0, 100.0 - (deviation / range) * 100.0);
                }

                weightedSum += score * weight;
            }
        }

        return totalWeight > 0 ? Math.round(weightedSum / totalWeight * 100.0) / 100.0 : 0;
    }

    private StandardCriteria findNorm(List<StandardCriteria> criteria, int criterionId) {
        for (StandardCriteria sc : criteria) {
            if (sc.getCriterionId() == criterionId) return sc;
        }
        return null;
    }

    /**
     * Получить полные данные инспекции (с результатами и дефектами)
     */
    public List<Object> getInspectionDetails(int inspectionId) throws SQLException {
        List<Object> details = new ArrayList<>();
        details.add(resultDAO.findByInspection(inspectionId));
        details.add(defectDAO.findByInspection(inspectionId));
        return details;
    }

    /**
     * Добавить дефект к инспекции
     */
    public void addDefect(Defect defect) throws SQLException {
        defectDAO.create(defect);
    }

    /**
     * История проверок продукта
     */
    public List<Inspection> getProductHistory(int productId) throws SQLException {
        return inspectionDAO.findByProduct(productId);
    }

    /**
     * Отчёт за период
     */
    public List<Inspection> getReportByPeriod(String dateFrom, String dateTo) throws SQLException {
        return inspectionDAO.findByPeriod(dateFrom, dateTo);
    }
}