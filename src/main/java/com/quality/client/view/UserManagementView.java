package com.quality.client.view;

import com.quality.client.NetworkClient;
import com.quality.model.Role;
import com.quality.model.User;
import com.quality.network.Response;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class UserManagementView {

    private TableView<User> table;

    @SuppressWarnings("unchecked")
    public Node getView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));

        Label title = new Label("Управление пользователями");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        // Кнопки
        Button addBtn = ViewHelper.createButton("Добавить", "#27ae60");
        Button toggleBtn = ViewHelper.createButton("Блок./Разблок.", "#e67e22");
        Button refreshBtn = ViewHelper.createButton("Обновить", "#3498db");

        HBox toolbar = new HBox(10, addBtn, toggleBtn, refreshBtn);

        // Таблица
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        idCol.setPrefWidth(50);

        TableColumn<User, String> usernameCol = new TableColumn<>("Логин");
        usernameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsername()));

        TableColumn<User, String> nameCol = new TableColumn<>("ФИО");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFullName()));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));

        TableColumn<User, String> roleCol = new TableColumn<>("Роль");
        roleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRoleName()));

        TableColumn<User, Boolean> activeCol = new TableColumn<>("Активен");
        activeCol.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isActive()).asObject());

        table.getColumns().addAll(idCol, usernameCol, nameCol, emailCol, roleCol, activeCol);
        VBox.setVgrow(table, Priority.ALWAYS);

        // Обработчики
        addBtn.setOnAction(e -> showAddDialog());
        toggleBtn.setOnAction(e -> toggleActive());
        refreshBtn.setOnAction(e -> loadData());

        root.getChildren().addAll(title, toolbar, table);
        loadData();
        return root;
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        try {
            Response response = NetworkClient.getInstance().sendRequest("GET_ALL_USERS");
            if (response.isSuccess()) {
                List<User> users = (List<User>) response.getData();
                table.getItems().setAll(users);
            } else {
                ViewHelper.showError(response.getMessage());
            }
        } catch (Exception e) {
            ViewHelper.showError("Ошибка загрузки: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void showAddDialog() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Новый пользователь");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        TextField fullNameField = new TextField();
        TextField emailField = new TextField();
        ComboBox<Role> roleCombo = new ComboBox<>();

        // Загрузка ролей
        try {
            Response resp = NetworkClient.getInstance().sendRequest("GET_ROLES");
            if (resp.isSuccess()) {
                List<Role> roles = (List<Role>) resp.getData();
                roleCombo.getItems().addAll(roles);
                roleCombo.setConverter(new javafx.util.StringConverter<Role>() {
                    public String toString(Role r) { return r != null ? r.getName() : ""; }
                    public Role fromString(String s) { return null; }
                });
            }
        } catch (Exception ignored) {}

        grid.add(new Label("Логин:"), 0, 0);     grid.add(usernameField, 1, 0);
        grid.add(new Label("Пароль:"), 0, 1);    grid.add(passwordField, 1, 1);
        grid.add(new Label("ФИО:"), 0, 2);       grid.add(fullNameField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);     grid.add(emailField, 1, 3);
        grid.add(new Label("Роль:"), 0, 4);      grid.add(roleCombo, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                User user = new User();
                user.setUsername(usernameField.getText().trim());
                user.setPasswordHash(passwordField.getText().trim());
                user.setFullName(fullNameField.getText().trim());
                user.setEmail(emailField.getText().trim());
                if (roleCombo.getValue() != null) {
                    user.setRoleId(roleCombo.getValue().getId());
                }
                return user;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(user -> {
            try {
                Response resp = NetworkClient.getInstance().sendRequest("REGISTER", user);
                if (resp.isSuccess()) {
                    ViewHelper.showInfo("Пользователь создан!");
                    loadData();
                } else {
                    ViewHelper.showError(resp.getMessage());
                }
            } catch (Exception e) {
                ViewHelper.showError("Ошибка: " + e.getMessage());
            }
        });
    }

    private void toggleActive() {
        User selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            ViewHelper.showError("Выберите пользователя!");
            return;
        }
        try {
            Response resp = NetworkClient.getInstance().sendRequest("TOGGLE_USER_ACTIVE", selected.getId());
            if (resp.isSuccess()) {
                loadData();
            } else {
                ViewHelper.showError(resp.getMessage());
            }
        } catch (Exception e) {
            ViewHelper.showError("Ошибка: " + e.getMessage());
        }
    }
}