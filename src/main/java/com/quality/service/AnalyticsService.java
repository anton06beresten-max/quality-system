package com.quality.service;

import com.quality.db.DatabaseConnection;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalyticsService {

    /**
     * Рейтинг продукции по среднему баллу качества
     */
    public List<Map<String, Serializable>> getProductRatings() throws SQLException {
        List<Map<String, Serializable>> ratings = new ArrayList<>();
        String sql = "SELECT p.id, p.name, p.article, " +
                "ROUND(AVG(i.overall_score)::numeric, 2) AS avg_score, " +
                "COUNT(i.id) AS inspection_count " +
                "FROM products p " +
                "JOIN inspections i ON p.id = i.product_id " +
                "GROUP BY p.id, p.name, p.article " +
                "ORDER BY avg_score DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Serializable> row = new HashMap<>();
                row.put("productId", rs.getInt("id"));
                row.put("productName", rs.getString("name"));
                row.put("article", rs.getString("article"));
                row.put("avgScore", rs.getDouble("avg_score"));
                row.put("inspectionCount", rs.getInt("inspection_count"));
                ratings.add(row);
            }
        }
        return ratings;
    }

    /**
     * Статистика дефектов (по типам)
     */
    public List<Map<String, Serializable>> getDefectStatistics() throws SQLException {
        List<Map<String, Serializable>> stats = new ArrayList<>();
        String sql = "SELECT dt.name, dt.severity, " +
                "COUNT(d.id) AS defect_count, SUM(d.quantity) AS total_quantity " +
                "FROM defect_types dt " +
                "LEFT JOIN defects d ON dt.id = d.defect_type_id " +
                "GROUP BY dt.name, dt.severity " +
                "ORDER BY total_quantity DESC NULLS LAST";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Serializable> row = new HashMap<>();
                row.put("defectType", rs.getString("name"));
                row.put("severity", rs.getString("severity"));
                row.put("defectCount", rs.getInt("defect_count"));
                row.put("totalQuantity", rs.getInt("total_quantity"));
                stats.add(row);
            }
        }
        return stats;
    }

    /**
     * Тренд качества продукта (динамика оценок во времени)
     */
    public List<Map<String, Serializable>> getQualityTrend(int productId) throws SQLException {
        List<Map<String, Serializable>> trend = new ArrayList<>();
        String sql = "SELECT DATE(inspection_date) AS insp_date, " +
                "ROUND(AVG(overall_score)::numeric, 2) AS avg_score " +
                "FROM inspections WHERE product_id = ? " +
                "GROUP BY DATE(inspection_date) " +
                "ORDER BY insp_date";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Serializable> row = new HashMap<>();
                row.put("date", rs.getString("insp_date"));
                row.put("avgScore", rs.getDouble("avg_score"));
                trend.add(row);
            }
        }
        return trend;
    }

    /**
     * Сравнительный анализ качества по категориям
     */
    public List<Map<String, Serializable>> getCategoryComparison() throws SQLException {
        List<Map<String, Serializable>> comparison = new ArrayList<>();
        String sql = "SELECT pc.name AS category_name, " +
                "ROUND(AVG(i.overall_score)::numeric, 2) AS avg_score, " +
                "COUNT(DISTINCT p.id) AS product_count, " +
                "COUNT(i.id) AS inspection_count " +
                "FROM product_categories pc " +
                "JOIN products p ON pc.id = p.category_id " +
                "JOIN inspections i ON p.id = i.product_id " +
                "GROUP BY pc.name " +
                "ORDER BY avg_score DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Serializable> row = new HashMap<>();
                row.put("categoryName", rs.getString("category_name"));
                row.put("avgScore", rs.getDouble("avg_score"));
                row.put("productCount", rs.getInt("product_count"));
                row.put("inspectionCount", rs.getInt("inspection_count"));
                comparison.add(row);
            }
        }
        return comparison;
    }

    /**
     * Статистика дефектов по продукту
     */
    public List<Map<String, Serializable>> getDefectsByProduct(int productId) throws SQLException {
        List<Map<String, Serializable>> stats = new ArrayList<>();
        String sql = "SELECT dt.name, dt.severity, " +
                "COUNT(d.id) AS defect_count, SUM(d.quantity) AS total_quantity " +
                "FROM defects d " +
                "JOIN defect_types dt ON d.defect_type_id = dt.id " +
                "JOIN inspections i ON d.inspection_id = i.id " +
                "WHERE i.product_id = ? " +
                "GROUP BY dt.name, dt.severity " +
                "ORDER BY total_quantity DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Serializable> row = new HashMap<>();
                row.put("defectType", rs.getString("name"));
                row.put("severity", rs.getString("severity"));
                row.put("defectCount", rs.getInt("defect_count"));
                row.put("totalQuantity", rs.getInt("total_quantity"));
                stats.add(row);
            }
        }
        return stats;
    }
}