package com.quality.db.dao;

import com.quality.db.DatabaseConnection;
import com.quality.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public List<Product> findAll() throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.id, p.name, p.article, p.category_id, p.description, " +
                "c.name AS category_name FROM products p " +
                "JOIN product_categories c ON p.category_id = c.id ORDER BY p.name";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapProduct(rs));
            }
        }
        return list;
    }

    public List<Product> findByCategory(int categoryId) throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.id, p.name, p.article, p.category_id, p.description, " +
                "c.name AS category_name FROM products p " +
                "JOIN product_categories c ON p.category_id = c.id " +
                "WHERE p.category_id = ? ORDER BY p.name";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapProduct(rs));
            }
        }
        return list;
    }

    public List<Product> search(String keyword) throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.id, p.name, p.article, p.category_id, p.description, " +
                "c.name AS category_name FROM products p " +
                "JOIN product_categories c ON p.category_id = c.id " +
                "WHERE LOWER(p.name) LIKE ? OR LOWER(p.article) LIKE ? ORDER BY p.name";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword.toLowerCase() + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapProduct(rs));
            }
        }
        return list;
    }

    public Product findById(int id) throws SQLException {
        String sql = "SELECT p.id, p.name, p.article, p.category_id, p.description, " +
                "c.name AS category_name FROM products p " +
                "JOIN product_categories c ON p.category_id = c.id WHERE p.id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapProduct(rs);
        }
        return null;
    }

    public void create(Product p) throws SQLException {
        String sql = "INSERT INTO products (name, article, category_id, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, p.getName());
            stmt.setString(2, p.getArticle());
            stmt.setInt(3, p.getCategoryId());
            stmt.setString(4, p.getDescription());
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) p.setId(keys.getInt(1));
        }
    }

    public void update(Product p) throws SQLException {
        String sql = "UPDATE products SET name=?, article=?, category_id=?, description=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getName());
            stmt.setString(2, p.getArticle());
            stmt.setInt(3, p.getCategoryId());
            stmt.setString(4, p.getDescription());
            stmt.setInt(5, p.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Product mapProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setArticle(rs.getString("article"));
        p.setCategoryId(rs.getInt("category_id"));
        p.setDescription(rs.getString("description"));
        p.setCategoryName(rs.getString("category_name"));
        return p;
    }
}