package com.quality.client.view;

import com.quality.client.NetworkClient;
import com.quality.model.ProductCategory;
import com.quality.model.QualityStandard;
import com.quality.network.Response;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.util.List;

public class QualityStandardView {

    private TableView<QualityStandard> table;

    @SuppressWarnings("unchecked")
    public Node getView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));

        Label title = new Label("Стандарты качества");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Button addBtn = ViewHelper.createButton("Добавить", "#27ae60");
        Button deleteBtn = ViewHelper.createButton("Удалить", "#e74c3c");
        Button refreshBtn = ViewHelper.createButton("Обновить", "#3498db");
        HBox toolbar = new HBox(10, addBtn, deleteBtn, refreshBtn);

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<QualityStandard, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());

        TableColumn<QualityStandard, String> nameCol = new TableColumn<>("Название");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));

        TableColumn<QualityStandard, String> catCol = new TableColumn<>("Категория");
        catCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategoryName()));

        TableColumn<QualityStandard, String> dateCol = new TableColumn<>("Дата вступления");
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEffectiveDate() != null ? c.getValue().getEffectiveDate().toString() : ""));

        TableColumn<QualityStandard, String> descCol = new TableColumn<>("Описание");
        descCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription()));

        table.getColumns().addAll(idCol, nameCol, catCol, dateCol, descCol);
        VBox.setVgrow(table, Priority.ALWAYS);

        addBtn.setOnAction(e -> showAddDialog());
        deleteBtn.setOnAction(e -> handleDelete());
        refreshBtn.setOnAction(e -> loadData());

        root.getChildren().addAll(title, toolbar, table);
        loadData();
        return root;
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        try {
            Response resp = NetworkClient.getInstance().sendRequest("GET_STANDARDS");
            if (resp.isSuccess()) table.getItems().setAll((List<QualityStandard>) resp.getData());
        } catch (Exception e) { ViewHelper.showError(e.getMessage()); }
    }

    @SuppressWarnings("unchecked")
    private void showAddDialog() {
        Dialog<QualityStandard> dialog = new Dialog<>();
        dialog.setTitle("Новый стандарт");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        TextField descField = new TextField();
        DatePicker datePicker = new DatePicker(LocalDate.now());
        ComboBox<ProductCategory> catCombo = new ComboBox<>();

        try {
            Response resp = NetworkClient.getInstance().sendRequest("GET_CATEGORIES");
            if (resp.isSuccess()) {
                catCombo.getItems().addAll((List<ProductCategory>) resp.getData());
                catCombo.setConverter(new javafx.util.StringConverter<ProductCategory>() {
                    public String toString(ProductCategory c) { return c != null ? c.getName() : ""; }
                    public ProductCategory fromString(String s) { return null; }
                });
            }
        } catch (Exception ignored) {}

        grid.add(new Label("Название:"), 0, 0);  grid.add(nameField, 1, 0);
        grid.add(new Label("Категория:"), 0, 1); grid.add(catCombo, 1, 1);
        grid.add(new Label("Дата:"), 0, 2);      grid.add(datePicker, 1, 2);
        grid.add(new Label("Описание:"), 0, 3);  grid.add(descField, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK && catCombo.getValue() != null) {
                QualityStandard s = new QualityStandard();
                s.setName(nameField.getText().trim());
                s.setDescription(descField.getText().trim());
                s.setEffectiveDate(datePicker.getValue());
                s.setCategoryId(catCombo.getValue().getId());
                return s;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(s -> {
            try {
                Response resp = NetworkClient.getInstance().sendRequest("CREATE_STANDARD", s);
                if (resp.isSuccess()) { ViewHelper.showInfo("Стандарт создан!"); loadData(); }
                else ViewHelper.showError(resp.getMessage());
            } catch (Exception e) { ViewHelper.showError(e.getMessage()); }
        });
    }

    private void handleDelete() {
        QualityStandard sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { ViewHelper.showError("Выберите стандарт!"); return; }
        if (!ViewHelper.confirm("Удалить '" + sel.getName() + "'?")) return;
        try {
            Response resp = NetworkClient.getInstance().sendRequest("DELETE_STANDARD", sel.getId());
            if (resp.isSuccess()) loadData();
            else ViewHelper.showError(resp.getMessage());
        } catch (Exception e) { ViewHelper.showError(e.getMessage()); }
    }
}