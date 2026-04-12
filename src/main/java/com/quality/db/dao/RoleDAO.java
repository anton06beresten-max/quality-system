package com.quality.db.dao;

import com.quality.db.DatabaseConnection;
import com.quality.model.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO {

    public List<Role> findAll() throws SQLException {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT id, name, description FROM roles ORDER BY id";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Role role = new Role();
                role.setId(rs.getInt("id"));
                role.setName(rs.getString("name"));
                role.setDescription(rs.getString("description"));
                roles.add(role);
            }
        }
        return roles;
    }

    public Role findById(int id) throws SQLException {
        String sql = "SELECT id, name, description FROM roles WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Role role = new Role();
                role.setId(rs.getInt("id"));
                role.setName(rs.getString("name"));
                role.setDescription(rs.getString("description"));
                return role;
            }
        }
        return null;
    }
}