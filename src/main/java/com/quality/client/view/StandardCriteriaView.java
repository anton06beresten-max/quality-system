package com.quality.client.view;

import com.quality.client.NetworkClient;
import com.quality.model.*;
import com.quality.network.Response;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class StandardCriteriaView {

    private ComboBox<QualityStandard> standardCombo;
    private TableView<StandardCriteria> table;

    public Node getView() {

        VBox root = new VBox(15);
        root.setPadding(new Insets(15));

        Label title =
                new Label("Назначение критериев стандартам");
        title.setFont(Font.font("Arial",
                FontWeight.BOLD, 22));

        standardCombo = new ComboBox<>();
        standardCombo.setPrefWidth(350);

        Button loadBtn =
                ViewHelper.createButton("Загрузить", "#3498db");

        Button addBtn =
                ViewHelper.createButton("Добавить критерий", "#27ae60");

        loadBtn.setOnAction(e -> loadCriteria());
        addBtn.setOnAction(e -> showAddDialog());

        HBox top =
                new HBox(10,
                        new Label("Стандарт:"),
                        standardCombo,
                        loadBtn,
                        addBtn);

        // ================= TABLE =================

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<StandardCriteria, String> nameCol =
                new TableColumn<>("Критерий");
        nameCol.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getCriterionName()));

        TableColumn<StandardCriteria, Number> minCol =
                new TableColumn<>("Min");
        minCol.setCellValueFactory(c ->
                new SimpleDoubleProperty(
                        c.getValue().getMinValue()));

        TableColumn<StandardCriteria, Number> maxCol =
                new TableColumn<>("Max");
        maxCol.setCellValueFactory(c ->
                new SimpleDoubleProperty(
                        c.getValue().getMaxValue()));

        TableColumn<StandardCriteria, Number> weightCol =
                new TableColumn<>("Вес");
        weightCol.setCellValueFactory(c ->
                new SimpleDoubleProperty(
                        c.getValue().getWeight()));

        TableColumn<StandardCriteria, Void> actionCol =
                new TableColumn<>("Действие");

        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn =
                    new Button("Удалить");

            {
                deleteBtn.setOnAction(e -> {
                    StandardCriteria sc =
                            getTableView().getItems()
                                    .get(getIndex());

                    deleteCriteria(sc);
                });
            }

            @Override
            protected void updateItem(Void item,
                                      boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteBtn);
                }
            }
        });

        table.getColumns().addAll(
                nameCol,
                minCol,
                maxCol,
                weightCol,
                actionCol);

        VBox.setVgrow(table, Priority.ALWAYS);

        root.getChildren().addAll(
                title,
                top,
                table
        );

        loadStandards();

        return root;
    }

    // ================= LOAD STANDARDS =================

    @SuppressWarnings("unchecked")
    private void loadStandards() {
        try {
            Response resp =
                    NetworkClient.getInstance()
                            .sendRequest("GET_STANDARDS");

            if (resp.isSuccess()) {
                standardCombo.getItems()
                        .addAll((List<QualityStandard>)
                                resp.getData());
            }

        } catch (Exception ignored) {}
    }

    // ================= LOAD CRITERIA =================

    @SuppressWarnings("unchecked")
    private void loadCriteria() {

        table.getItems().clear();

        if (standardCombo.getValue() == null) {
            ViewHelper.showError("Выберите стандарт");
            return;
        }

        try {
            Response resp =
                    NetworkClient.getInstance()
                            .sendRequest("GET_STANDARD_CRITERIA",
                                    standardCombo.getValue().getId());

            if (resp.isSuccess()) {
                table.getItems()
                        .addAll((List<StandardCriteria>)
                                resp.getData());
            }

        } catch (Exception e) {
            ViewHelper.showError(e.getMessage());
        }
    }

    // ================= ADD CRITERIA =================

    @SuppressWarnings("unchecked")
    private void showAddDialog() {

        if (standardCombo.getValue() == null) {
            ViewHelper.showError("Сначала выберите стандарт");
            return;
        }

        Dialog<StandardCriteria> dialog =
                new Dialog<>();

        dialog.setTitle("Добавить критерий");
        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(ButtonType.OK,
                        ButtonType.CANCEL);

        ComboBox<QualityCriteria> criteriaCombo =
                new ComboBox<>();

        TextField minField = new TextField();
        TextField maxField = new TextField();
        TextField weightField = new TextField("1");

        try {
            Response resp =
                    NetworkClient.getInstance()
                            .sendRequest("GET_CRITERIA");

            if (resp.isSuccess()) {
                criteriaCombo.getItems()
                        .addAll((List<QualityCriteria>)
                                resp.getData());
            }

        } catch (Exception ignored) {}

        VBox box = new VBox(10,
                new Label("Критерий:"),
                criteriaCombo,
                new Label("Min:"),
                minField,
                new Label("Max:"),
                maxField,
                new Label("Вес:"),
                weightField);

        dialog.getDialogPane()
                .setContent(box);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {

                try {
                    StandardCriteria sc =
                            new StandardCriteria();

                    sc.setStandardId(
                            standardCombo.getValue()
                                    .getId());

                    sc.setCriterionId(
                            criteriaCombo.getValue()
                                    .getId());

                    sc.setMinValue(
                            Double.parseDouble(
                                    minField.getText()));

                    sc.setMaxValue(
                            Double.parseDouble(
                                    maxField.getText()));

                    sc.setWeight(
                            Double.parseDouble(
                                    weightField.getText()));

                    return sc;

                } catch (Exception e) {
                    ViewHelper.showError("Некорректные данные");
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(sc -> {

            try {
                NetworkClient.getInstance()
                        .sendRequest("ADD_STANDARD_CRITERIA",
                                sc);

                loadCriteria();

            } catch (Exception ignored) {}
        });
    }

    // ================= DELETE =================

    private void deleteCriteria(StandardCriteria sc) {

        try {

            NetworkClient.getInstance()
                    .sendRequest(
                            "REMOVE_STANDARD_CRITERIA",
                            new int[]{
                                    sc.getStandardId(),
                                    sc.getCriterionId()
                            });

            loadCriteria();

        } catch (Exception ignored) {}
    }
}