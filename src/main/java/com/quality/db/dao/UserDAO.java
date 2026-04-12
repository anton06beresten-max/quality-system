package com.quality.db.dao;

import com.quality.db.DatabaseConnection;
import com.quality.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User authenticate(String username, String passwordHash) throws SQLException {
        String sql =
                "SELECT u.id, u.username, u.full_name, u.email, " +
                        "u.is_active, u.role_id, r.name AS role_name " +
                        "FROM users u " +
                        "JOIN roles r ON u.role_id = r.id " +
                        "WHERE u.username = ? AND u.password_hash = ? AND u.is_active = true";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, passwordHash);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
        }
        return null;
    }

    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql =
                "SELECT u.id, u.username, u.full_name, u.email, " +
                        "u.is_active, u.role_id, r.name AS role_name " +
                        "FROM users u " +
                        "JOIN roles r ON u.role_id = r.id " +
                        "ORDER BY u.id";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapUser(rs));
            }
        }
        return users;
    }

    public void create(User user) throws SQLException {
        String sql =
                "INSERT INTO users (username, password_hash, full_name, email, role_id, is_active) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getEmail());
            stmt.setInt(5, user.getRoleId());
            stmt.setBoolean(6, user.isActive());

            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                user.setId(keys.getInt(1));
            }
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setRoleId(rs.getInt("role_id"));
        user.setRoleName(rs.getString("role_name"));
        user.setActive(rs.getBoolean("is_active"));
        return user;
    }
    public void update(User user) throws SQLException {
        String sql = "UPDATE users SET full_name=?, email=?, role_id=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setInt(3, user.getRoleId());
            stmt.setInt(4, user.getId());
            stmt.executeUpdate();
        }
    }

    public void toggleActive(int userId) throws SQLException {
        String sql = "UPDATE users SET is_active = NOT is_active WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }
}