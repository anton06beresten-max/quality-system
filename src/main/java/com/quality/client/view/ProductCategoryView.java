package com.quality.client.view;

import com.quality.client.NetworkClient;
import com.quality.model.ProductCategory;
import com.quality.network.Response;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class ProductCategoryView {

    private TableView<ProductCategory> table;

    @SuppressWarnings("unchecked")
    public Node getView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));

        Label title = new Label("Категории продукции");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Button addBtn = ViewHelper.createButton("Добавить", "#27ae60");
        Button deleteBtn = ViewHelper.createButton("Удалить", "#e74c3c");
        Button refreshBtn = ViewHelper.createButton("Обновить", "#3498db");
        HBox toolbar = new HBox(10, addBtn, deleteBtn, refreshBtn);

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ProductCategory, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        idCol.setPrefWidth(50);

        TableColumn<ProductCategory, String> nameCol = new TableColumn<>("Название");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));

        TableColumn<ProductCategory, String> descCol = new TableColumn<>("Описание");
        descCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription()));

        table.getColumns().addAll(idCol, nameCol, descCol);
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
            Response resp = NetworkClient.getInstance().sendRequest("GET_CATEGORIES");
            if (resp.isSuccess()) {
                table.getItems().setAll((List<ProductCategory>) resp.getData());
            }
        } catch (Exception e) {
            ViewHelper.showError("Ошибка загрузки: " + e.getMessage());
        }
    }

    private void showAddDialog() {
        Dialog<ProductCategory> dialog = new Dialog<>();
        dialog.setTitle("Новая категория");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        TextField descField = new TextField();

        grid.add(new Label("Название:"), 0, 0);  grid.add(nameField, 1, 0);
        grid.add(new Label("Описание:"), 0, 1);  grid.add(descField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                ProductCategory cat = new ProductCategory();
                cat.setName(nameField.getText().trim());
                cat.setDescription(descField.getText().trim());
                return cat;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(cat -> {
            try {
                Response resp = NetworkClient.getInstance().sendRequest("CREATE_CATEGORY", cat);
                if (resp.isSuccess()) { ViewHelper.showInfo("Создано!"); loadData(); }
                else ViewHelper.showError(resp.getMessage());
            } catch (Exception e) { ViewHelper.showError(e.getMessage()); }
        });
    }

    private void handleDelete() {
        ProductCategory sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { ViewHelper.showError("Выберите категорию!"); return; }
        if (!ViewHelper.confirm("Удалить категорию '" + sel.getName() + "'?")) return;
        try {
            Response resp = NetworkClient.getInstance().sendRequest("DELETE_CATEGORY", sel.getId());
            if (resp.isSuccess()) loadData();
            else ViewHelper.showError(resp.getMessage());
        } catch (Exception e) { ViewHelper.showError(e.getMessage()); }
    }
}