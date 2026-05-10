package com.quality.model;

public enum UserRole {

    ADMIN("Администратор"),
    INSPECTOR("Инспектор"),
    MANAGER("Менеджер");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}