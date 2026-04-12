package com.quality.client;

import com.quality.model.User;

public class Session {
    private static User currentUser;

    public static User getCurrentUser() { return currentUser; }
    public static void setCurrentUser(User user) { currentUser = user; }
    public static String getRole() { return currentUser != null ? currentUser.getRoleName() : null; }
    public static boolean isAdmin() { return "ADMIN".equals(getRole()); }
    public static boolean isInspector() { return "INSPECTOR".equals(getRole()); }
    public static boolean isManager() { return "MANAGER".equals(getRole()); }
}