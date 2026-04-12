package com.quality.db.dao;

import com.quality.db.DatabaseConnection;
import com.quality.model.Defect;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DefectDAO {

    public List<Defect> findByInspection(int inspectionId) throws SQLException {
        List<Defect> list = new ArrayList<>();
        String sql = "SELECT d.id, d.inspection_id, d.defect_type_id, d.description, " +
                "d.quantity, d.detected_at, dt.name AS defect_type_name " +
                "FROM defects d " +
                "JOIN defect_types dt ON d.defect_type_id = dt.id " +
                "WHERE d.inspection_id = ? ORDER BY d.detected_at";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, inspectionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapDefect(rs));
            }
        }
        return list;
    }

    public void create(Defect d) throws SQLException {
        String sql = "INSERT INTO defects (inspection_id, defect_type_id, description, quantity) " +
                "VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, d.getInspectionId());
            stmt.setInt(2, d.getDefectTypeId());
            stmt.setString(3, d.getDescription());
            stmt.setInt(4, d.getQuantity());
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) d.setId(keys.getInt(1));
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM defects WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Defect mapDefect(ResultSet rs) throws SQLException {
        Defect d = new Defect();
        d.setId(rs.getInt("id"));
        d.setInspectionId(rs.getInt("inspection_id"));
        d.setDefectTypeId(rs.getInt("defect_type_id"));
        d.setDescription(rs.getString("description"));
        d.setQuantity(rs.getInt("quantity"));
        Timestamp ts = rs.getTimestamp("detected_at");
        if (ts != null) d.setDetectedAt(ts.toLocalDateTime());
        d.setDefectTypeName(rs.getString("defect_type_name"));
        return d;
    }
}