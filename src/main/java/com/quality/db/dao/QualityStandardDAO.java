package com.quality.db.dao;

import com.quality.db.DatabaseConnection;
import com.quality.model.QualityStandard;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QualityStandardDAO {

    public List<QualityStandard> findAll() throws SQLException {
        List<QualityStandard> list = new ArrayList<>();
        String sql = "SELECT s.id, s.name, s.description, s.effective_date, s.category_id, " +
                "c.name AS category_name FROM quality_standards s " +
                "JOIN product_categories c ON s.category_id = c.id ORDER BY s.name";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapStandard(rs));
            }
        }
        return list;
    }

    public List<QualityStandard> findByCategory(int categoryId) throws SQLException {
        List<QualityStandard> list = new ArrayList<>();
        String sql = "SELECT s.id, s.name, s.description, s.effective_date, s.category_id, " +
                "c.name AS category_name FROM quality_standards s " +
                "JOIN product_categories c ON s.category_id = c.id " +
                "WHERE s.category_id = ? ORDER BY s.name";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapStandard(rs));
            }
        }
        return list;
    }

    public void create(QualityStandard s) throws SQLException {
        String sql = "INSERT INTO quality_standards (name, description, effective_date, category_id) " +
                "VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, s.getName());
            stmt.setString(2, s.getDescription());
            stmt.setDate(3, s.getEffectiveDate() != null ? Date.valueOf(s.getEffectiveDate()) : null);
            stmt.setInt(4, s.getCategoryId());
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) s.setId(keys.getInt(1));
        }
    }

    public void update(QualityStandard s) throws SQLException {
        String sql = "UPDATE quality_standards SET name=?, description=?, effective_date=?, " +
                "category_id=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, s.getName());
            stmt.setString(2, s.getDescription());
            stmt.setDate(3, s.getEffectiveDate() != null ? Date.valueOf(s.getEffectiveDate()) : null);
            stmt.setInt(4, s.getCategoryId());
            stmt.setInt(5, s.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM quality_standards WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private QualityStandard mapStandard(ResultSet rs) throws SQLException {
        QualityStandard s = new QualityStandard();
        s.setId(rs.getInt("id"));
        s.setName(rs.getString("name"));
        s.setDescription(rs.getString("description"));
        Date d = rs.getDate("effective_date");
        if (d != null) s.setEffectiveDate(d.toLocalDate());
        s.setCategoryId(rs.getInt("category_id"));
        s.setCategoryName(rs.getString("category_name"));
        return s;
    }
}