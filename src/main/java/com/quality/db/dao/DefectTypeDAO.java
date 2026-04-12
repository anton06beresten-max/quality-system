package com.quality.db.dao;

import com.quality.db.DatabaseConnection;
import com.quality.model.DefectType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DefectTypeDAO {

    public List<DefectType> findAll() throws SQLException {
        List<DefectType> list = new ArrayList<>();
        String sql = "SELECT id, name, description, severity FROM defect_types ORDER BY name";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                DefectType dt = new DefectType();
                dt.setId(rs.getInt("id"));
                dt.setName(rs.getString("name"));
                dt.setDescription(rs.getString("description"));
                dt.setSeverity(rs.getString("severity"));
                list.add(dt);
            }
        }
        return list;
    }

    public void create(DefectType dt) throws SQLException {
        String sql = "INSERT INTO defect_types (name, description, severity) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, dt.getName());
            stmt.setString(2, dt.getDescription());
            stmt.setString(3, dt.getSeverity());
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) dt.setId(keys.getInt(1));
        }
    }

    public void update(DefectType dt) throws SQLException {
        String sql = "UPDATE defect_types SET name=?, description=?, severity=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dt.getName());
            stmt.setString(2, dt.getDescription());
            stmt.setString(3, dt.getSeverity());
            stmt.setInt(4, dt.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM defect_types WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}