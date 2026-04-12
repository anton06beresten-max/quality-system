package com.quality.db.dao;

import com.quality.db.DatabaseConnection;
import com.quality.model.Inspection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InspectionDAO {

    public List<Inspection> findAll() throws SQLException {
        List<Inspection> list = new ArrayList<>();
        String sql = "SELECT i.id, i.product_id, i.standard_id, i.inspector_id, " +
                "i.inspection_date, i.batch_number, i.overall_score, i.status, i.notes, " +
                "p.name AS product_name, s.name AS standard_name, u.full_name AS inspector_name " +
                "FROM inspections i " +
                "JOIN products p ON i.product_id = p.id " +
                "JOIN quality_standards s ON i.standard_id = s.id " +
                "JOIN users u ON i.inspector_id = u.id " +
                "ORDER BY i.inspection_date DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapInspection(rs));
            }
        }
        return list;
    }

    public List<Inspection> findByInspector(int inspectorId) throws SQLException {
        List<Inspection> list = new ArrayList<>();
        String sql = "SELECT i.id, i.product_id, i.standard_id, i.inspector_id, " +
                "i.inspection_date, i.batch_number, i.overall_score, i.status, i.notes, " +
                "p.name AS product_name, s.name AS standard_name, u.full_name AS inspector_name " +
                "FROM inspections i " +
                "JOIN products p ON i.product_id = p.id " +
                "JOIN quality_standards s ON i.standard_id = s.id " +
                "JOIN users u ON i.inspector_id = u.id " +
                "WHERE i.inspector_id = ? ORDER BY i.inspection_date DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, inspectorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapInspection(rs));
            }
        }
        return list;
    }

    public List<Inspection> findByProduct(int productId) throws SQLException {
        List<Inspection> list = new ArrayList<>();
        String sql = "SELECT i.id, i.product_id, i.standard_id, i.inspector_id, " +
                "i.inspection_date, i.batch_number, i.overall_score, i.status, i.notes, " +
                "p.name AS product_name, s.name AS standard_name, u.full_name AS inspector_name " +
                "FROM inspections i " +
                "JOIN products p ON i.product_id = p.id " +
                "JOIN quality_standards s ON i.standard_id = s.id " +
                "JOIN users u ON i.inspector_id = u.id " +
                "WHERE i.product_id = ? ORDER BY i.inspection_date DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapInspection(rs));
            }
        }
        return list;
    }

    public List<Inspection> findByPeriod(String dateFrom, String dateTo) throws SQLException {
        List<Inspection> list = new ArrayList<>();
        String sql = "SELECT i.id, i.product_id, i.standard_id, i.inspector_id, " +
                "i.inspection_date, i.batch_number, i.overall_score, i.status, i.notes, " +
                "p.name AS product_name, s.name AS standard_name, u.full_name AS inspector_name " +
                "FROM inspections i " +
                "JOIN products p ON i.product_id = p.id " +
                "JOIN quality_standards s ON i.standard_id = s.id " +
                "JOIN users u ON i.inspector_id = u.id " +
                "WHERE i.inspection_date BETWEEN ?::timestamp AND ?::timestamp " +
                "ORDER BY i.inspection_date DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dateFrom);
            stmt.setString(2, dateTo);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapInspection(rs));
            }
        }
        return list;
    }

    public int create(Inspection insp) throws SQLException {
        String sql = "INSERT INTO inspections (product_id, standard_id, inspector_id, " +
                "inspection_date, batch_number, overall_score, status, notes) " +
                "VALUES (?, ?, ?, NOW(), ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, insp.getProductId());
            stmt.setInt(2, insp.getStandardId());
            stmt.setInt(3, insp.getInspectorId());
            stmt.setString(4, insp.getBatchNumber());
            stmt.setDouble(5, insp.getOverallScore());
            stmt.setString(6, insp.getStatus());
            stmt.setString(7, insp.getNotes());
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                insp.setId(keys.getInt(1));
                return insp.getId();
            }
        }
        return -1;
    }

    public void updateScore(int inspectionId, double score, String status) throws SQLException {
        String sql = "UPDATE inspections SET overall_score = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, score);
            stmt.setString(2, status);
            stmt.setInt(3, inspectionId);
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM inspections WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Inspection mapInspection(ResultSet rs) throws SQLException {
        Inspection i = new Inspection();
        i.setId(rs.getInt("id"));
        i.setProductId(rs.getInt("product_id"));
        i.setStandardId(rs.getInt("standard_id"));
        i.setInspectorId(rs.getInt("inspector_id"));
        Timestamp ts = rs.getTimestamp("inspection_date");
        if (ts != null) i.setInspectionDate(ts.toLocalDateTime());
        i.setBatchNumber(rs.getString("batch_number"));
        i.setOverallScore(rs.getDouble("overall_score"));
        i.setStatus(rs.getString("status"));
        i.setNotes(rs.getString("notes"));
        i.setProductName(rs.getString("product_name"));
        i.setStandardName(rs.getString("standard_name"));
        i.setInspectorName(rs.getString("inspector_name"));
        return i;
    }
}