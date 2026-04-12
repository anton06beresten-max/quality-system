package com.quality.client.view;

import com.quality.client.NetworkClient;
import com.quality.model.QualityCriteria;
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

public class CriteriaView {

    private TableView<QualityCriteria> table;

    @SuppressWarnings("unchecked")
    public Node getView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));

        Label title = new Label("Критерии качества");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Button addBtn = ViewHelper.createButton("Добавить", "#27ae60");
        Button deleteBtn = ViewHelper.createButton("Удалить", "#e74c3c");
        Button refreshBtn = ViewHelper.createButton("Обновить", "#3498db");
        HBox toolbar = new HBox(10, addBtn, deleteBtn, refreshBtn);

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<QualityCriteria, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());

        TableColumn<QualityCriteria, String> nameCol = new TableColumn<>("Название");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));

        TableColumn<QualityCriteria, String> unitCol = new TableColumn<>("Единица");
        unitCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUnit()));

        TableColumn<QualityCriteria, String> descCol = new TableColumn<>("Описание");
        descCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription()));

        table.getColumns().addAll(idCol, nameCol, unitCol, descCol);
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
            Response resp = NetworkClient.getInstance().sendRequest("GET_CRITERIA");
            if (resp.isSuccess()) table.getItems().setAll((List<QualityCriteria>) resp.getData());
        } catch (Exception e) { ViewHelper.showError(e.getMessage()); }
    }

    private void showAddDialog() {
        Dialog<QualityCriteria> dialog = new Dialog<>();
        dialog.setTitle("Новый критерий");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        TextField unitField = new TextField();
        TextField descField = new TextField();

        grid.add(new Label("Название:"), 0, 0);  grid.add(nameField, 1, 0);
        grid.add(new Label("Единица:"), 0, 1);   grid.add(unitField, 1, 1);
        grid.add(new Label("Описание:"), 0, 2);  grid.add(descField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                QualityCriteria c = new QualityCriteria();
                c.setName(nameField.getText().trim());
                c.setUnit(unitField.getText().trim());
                c.setDescription(descField.getText().trim());
                return c;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(c -> {
            try {
                Response resp = NetworkClient.getInstance().sendRequest("CREATE_CRITERIA", c);
                if (resp.isSuccess()) { ViewHelper.showInfo("Критерий создан!"); loadData(); }
                else ViewHelper.showError(resp.getMessage());
            } catch (Exception e) { ViewHelper.showError(e.getMessage()); }
        });
    }

    private void handleDelete() {
        QualityCriteria sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { ViewHelper.showError("Выберите критерий!"); return; }
        if (!ViewHelper.confirm("Удалить '" + sel.getName() + "'?")) return;
        try {
            Response resp = NetworkClient.getInstance().sendRequest("DELETE_CRITERIA", sel.getId());
            if (resp.isSuccess()) loadData();
            else ViewHelper.showError(resp.getMessage());
        } catch (Exception e) { ViewHelper.showError(e.getMessage()); }
    }
}