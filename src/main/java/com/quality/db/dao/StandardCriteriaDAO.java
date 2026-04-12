package com.quality.db.dao;

import com.quality.db.DatabaseConnection;
import com.quality.model.StandardCriteria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StandardCriteriaDAO {

    public List<StandardCriteria> findByStandard(int standardId) throws SQLException {
        List<StandardCriteria> list = new ArrayList<>();
        String sql = "SELECT sc.standard_id, sc.criterion_id, sc.min_value, sc.max_value, " +
                "sc.weight, qc.name AS criterion_name, qc.unit AS criterion_unit " +
                "FROM standard_criteria sc " +
                "JOIN quality_criteria qc ON sc.criterion_id = qc.id " +
                "WHERE sc.standard_id = ? ORDER BY qc.name";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, standardId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                StandardCriteria sc = new StandardCriteria();
                sc.setStandardId(rs.getInt("standard_id"));
                sc.setCriterionId(rs.getInt("criterion_id"));
                sc.setMinValue(rs.getDouble("min_value"));
                sc.setMaxValue(rs.getDouble("max_value"));
                sc.setWeight(rs.getDouble("weight"));
                sc.setCriterionName(rs.getString("criterion_name"));
                sc.setCriterionUnit(rs.getString("criterion_unit"));
                list.add(sc);
            }
        }
        return list;
    }

    public void addCriterionToStandard(StandardCriteria sc) throws SQLException {
        String sql = "INSERT INTO standard_criteria (standard_id, criterion_id, min_value, " +
                "max_value, weight) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sc.getStandardId());
            stmt.setInt(2, sc.getCriterionId());
            stmt.setDouble(3, sc.getMinValue());
            stmt.setDouble(4, sc.getMaxValue());
            stmt.setDouble(5, sc.getWeight());
            stmt.executeUpdate();
        }
    }

    public void removeCriterionFromStandard(int standardId, int criterionId) throws SQLException {
        String sql = "DELETE FROM standard_criteria WHERE standard_id = ? AND criterion_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, standardId);
            stmt.setInt(2, criterionId);
            stmt.executeUpdate();
        }
    }
}