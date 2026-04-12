package com.quality.db.dao;

import com.quality.db.DatabaseConnection;
import com.quality.model.InspectionResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InspectionResultDAO {

    public List<InspectionResult> findByInspection(int inspectionId) throws SQLException {
        List<InspectionResult> list = new ArrayList<>();
        String sql = "SELECT ir.inspection_id, ir.criterion_id, ir.actual_value, " +
                "ir.is_passed, ir.notes, qc.name AS criterion_name " +
                "FROM inspection_results ir " +
                "JOIN quality_criteria qc ON ir.criterion_id = qc.id " +
                "WHERE ir.inspection_id = ? ORDER BY qc.name";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, inspectionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                InspectionResult r = new InspectionResult();
                r.setInspectionId(rs.getInt("inspection_id"));
                r.setCriterionId(rs.getInt("criterion_id"));
                r.setActualValue(rs.getDouble("actual_value"));
                r.setPassed(rs.getBoolean("is_passed"));
                r.setNotes(rs.getString("notes"));
                r.setCriterionName(rs.getString("criterion_name"));
                list.add(r);
            }
        }
        return list;
    }

    public void create(InspectionResult r) throws SQLException {
        String sql = "INSERT INTO inspection_results (inspection_id, criterion_id, " +
                "actual_value, is_passed, notes) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, r.getInspectionId());
            stmt.setInt(2, r.getCriterionId());
            stmt.setDouble(3, r.getActualValue());
            stmt.setBoolean(4, r.isPassed());
            stmt.setString(5, r.getNotes());
            stmt.executeUpdate();
        }
    }

    public void createBatch(List<InspectionResult> results) throws SQLException {
        String sql = "INSERT INTO inspection_results (inspection_id, criterion_id, " +
                "actual_value, is_passed, notes) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (InspectionResult r : results) {
                stmt.setInt(1, r.getInspectionId());
                stmt.setInt(2, r.getCriterionId());
                stmt.setDouble(3, r.getActualValue());
                stmt.setBoolean(4, r.isPassed());
                stmt.setString(5, r.getNotes());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }
}