package com.quality.db.dao;

import com.quality.db.DatabaseConnection;
import com.quality.model.QualityCriteria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QualityCriteriaDAO {

    public List<QualityCriteria> findAll() throws SQLException {
        List<QualityCriteria> list = new ArrayList<>();
        String sql = "SELECT id, name, description, unit FROM quality_criteria ORDER BY name";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                QualityCriteria c = new QualityCriteria();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                c.setDescription(rs.getString("description"));
                c.setUnit(rs.getString("unit"));
                list.add(c);
            }
        }
        return list;
    }

    public void create(QualityCriteria c) throws SQLException {
        String sql = "INSERT INTO quality_criteria (name, description, unit) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, c.getName());
            stmt.setString(2, c.getDescription());
            stmt.setString(3, c.getUnit());
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) c.setId(keys.getInt(1));
        }
    }

    public void update(QualityCriteria c) throws SQLException {
        String sql = "UPDATE quality_criteria SET name=?, description=?, unit=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getName());
            stmt.setString(2, c.getDescription());
            stmt.setString(3, c.getUnit());
            stmt.setInt(4, c.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM quality_criteria WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}