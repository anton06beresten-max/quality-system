package com.quality.client.view;

import com.quality.client.NetworkClient;
import com.quality.model.Product;
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

public class ProductView {

    private TableView<Product> table;
    private TextField searchField;

    @SuppressWarnings("unchecked")
    public Node getView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));

        Label title = new Label("Продукция");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        // Поиск
        searchField = new TextField();
        searchField.setPromptText("Поиск по названию или артикулу...");
        searchField.setPrefWidth(300);
        Button searchBtn = ViewHelper.createButton("Найти", "#8e44ad");
        searchBtn.setOnAction(e -> searchProducts());

        Button addBtn = ViewHelper.createButton("Добавить", "#27ae60");
        Button deleteBtn = ViewHelper.createButton("Удалить", "#e74c3c");
        Button refreshBtn = ViewHelper.createButton("Обновить", "#3498db");

        HBox toolbar = new HBox(10, searchField, searchBtn, addBtn, deleteBtn, refreshBtn);

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Product, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        idCol.setPrefWidth(50);

        TableColumn<Product, String> nameCol = new TableColumn<>("Название");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));

        TableColumn<Product, String> articleCol = new TableColumn<>("Артикул");
        articleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getArticle()));

        TableColumn<Product, String> catCol = new TableColumn<>("Категория");
        catCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategoryName()));

        TableColumn<Product, String> descCol = new TableColumn<>("Описание");
        descCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription()));

        table.getColumns().addAll(idCol, nameCol, articleCol, catCol, descCol);
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
            Response resp = NetworkClient.getInstance().sendRequest("GET_PRODUCTS");
            if (resp.isSuccess()) table.getItems().setAll((List<Product>) resp.getData());
        } catch (Exception e) { ViewHelper.showError(e.getMessage()); }
    }

    @SuppressWarnings("unchecked")
    private void searchProducts() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) { loadData(); return; }
        try {
            Response resp = NetworkClient.getInstance().sendRequest("SEARCH_PRODUCTS", keyword);
            if (resp.isSuccess()) table.getItems().setAll((List<Product>) resp.getData());
        } catch (Exception e) { ViewHelper.showError(e.getMessage()); }
    }

    @SuppressWarnings("unchecked")
    private void showAddDialog() {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Новый продукт");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        TextField articleField = new TextField();
        TextField descField = new TextField();
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
        grid.add(new Label("Артикул:"), 0, 1);   grid.add(articleField, 1, 1);
        grid.add(new Label("Категория:"), 0, 2); grid.add(catCombo, 1, 2);
        grid.add(new Label("Описание:"), 0, 3);  grid.add(descField, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK && catCombo.getValue() != null) {
                Product p = new Product();
                p.setName(nameField.getText().trim());
                p.setArticle(articleField.getText().trim());
                p.setCategoryId(catCombo.getValue().getId());
                p.setDescription(descField.getText().trim());
                return p;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(p -> {
            try {
                Response resp = NetworkClient.getInstance().sendRequest("CREATE_PRODUCT", p);
                if (resp.isSuccess()) { ViewHelper.showInfo("Продукт создан!"); loadData(); }
                else ViewHelper.showError(resp.getMessage());
            } catch (Exception e) { ViewHelper.showError(e.getMessage()); }
        });
    }

    private void handleDelete() {
        Product sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { ViewHelper.showError("Выберите продукт!"); return; }
        if (!ViewHelper.confirm("Удалить '" + sel.getName() + "'?")) return;
        try {
            Response resp = NetworkClient.getInstance().sendRequest("DELETE_PRODUCT", sel.getId());
            if (resp.isSuccess()) loadData();
            else ViewHelper.showError(resp.getMessage());
        } catch (Exception e) { ViewHelper.showError(e.getMessage()); }
    }
}