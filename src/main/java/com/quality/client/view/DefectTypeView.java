package com.quality.client.view;

import com.quality.client.NetworkClient;
import com.quality.model.DefectType;
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

public class DefectTypeView {

    private TableView<DefectType> table;

    @SuppressWarnings("unchecked")
    public Node getView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));

        Label title = new Label("Типы дефектов");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Button addBtn = ViewHelper.createButton("Добавить", "#27ae60");
        Button deleteBtn = ViewHelper.createButton("Удалить", "#e74c3c");
        Button refreshBtn = ViewHelper.createButton("Обновить", "#3498db");
        HBox toolbar = new HBox(10, addBtn, deleteBtn, refreshBtn);

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<DefectType, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());

        TableColumn<DefectType, String> nameCol = new TableColumn<>("Название");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));

        TableColumn<DefectType, String> sevCol = new TableColumn<>("Серьёзность");
        sevCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSeverity()));

        TableColumn<DefectType, String> descCol = new TableColumn<>("Описание");
        descCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription()));

        table.getColumns().addAll(idCol, nameCol, sevCol, descCol);
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
            Response resp = NetworkClient.getInstance().sendRequest("GET_DEFECT_TYPES");
            if (resp.isSuccess()) table.getItems().setAll((List<DefectType>) resp.getData());
        } catch (Exception e) { ViewHelper.showError(e.getMessage()); }
    }

    private void showAddDialog() {
        Dialog<DefectType> dialog = new Dialog<>();
        dialog.setTitle("Новый тип дефекта");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        TextField descField = new TextField();
        ComboBox<String> sevCombo = new ComboBox<>();
        sevCombo.getItems().addAll("LOW", "MEDIUM", "HIGH", "CRITICAL");
        sevCombo.setValue("MEDIUM");

        grid.add(new Label("Название:"), 0, 0);      grid.add(nameField, 1, 0);
        grid.add(new Label("Серьёзность:"), 0, 1);   grid.add(sevCombo, 1, 1);
        grid.add(new Label("Описание:"), 0, 2);      grid.add(descField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                DefectType dt = new DefectType();
                dt.setName(nameField.getText().trim());
                dt.setSeverity(sevCombo.getValue());
                dt.setDescription(descField.getText().trim());
                return dt;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(dt -> {
            try {
                Response resp = NetworkClient.getInstance().sendRequest("CREATE_DEFECT_TYPE", dt);
                if (resp.isSuccess()) { ViewHelper.showInfo("Тип дефекта создан!"); loadData(); }
                else ViewHelper.showError(resp.getMessage());
            } catch (Exception e) { ViewHelper.showError(e.getMessage()); }
        });
    }

    private void handleDelete() {
        DefectType sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { ViewHelper.showError("Выберите тип дефекта!"); return; }
        if (!ViewHelper.confirm("Удалить '" + sel.getName() + "'?")) return;
        try {
            Response resp = NetworkClient.getInstance().sendRequest("DELETE_DEFECT_TYPE", sel.getId());
            if (resp.isSuccess()) loadData();
            else ViewHelper.showError(resp.getMessage());
        } catch (Exception e) { ViewHelper.showError(e.getMessage()); }
    }
}