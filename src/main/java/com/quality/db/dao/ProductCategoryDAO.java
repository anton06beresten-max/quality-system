package com.quality.db.dao;

import com.quality.db.DatabaseConnection;
import com.quality.model.ProductCategory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductCategoryDAO {

    public List<ProductCategory> findAll() throws SQLException {
        List<ProductCategory> list = new ArrayList<>();
        String sql = "SELECT id, name, description FROM product_categories ORDER BY name";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ProductCategory cat = new ProductCategory();
                cat.setId(rs.getInt("id"));
                cat.setName(rs.getString("name"));
                cat.setDescription(rs.getString("description"));
                list.add(cat);
            }
        }
        return list;
    }

    public void create(ProductCategory cat) throws SQLException {
        String sql = "INSERT INTO product_categories (name, description) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, cat.getName());
            stmt.setString(2, cat.getDescription());
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) cat.setId(keys.getInt(1));
        }
    }

    public void update(ProductCategory cat) throws SQLException {
        String sql = "UPDATE product_categories SET name = ?, description = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cat.getName());
            stmt.setString(2, cat.getDescription());
            stmt.setInt(3, cat.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM product_categories WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}