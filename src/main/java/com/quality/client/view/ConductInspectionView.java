package com.quality.client.view;

import com.quality.client.NetworkClient;
import com.quality.client.Session;
import com.quality.model.*;
import com.quality.network.Response;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.beans.property.SimpleStringProperty;


import java.util.ArrayList;
import java.util.List;

public class ConductInspectionView {

    private ComboBox<Product> productCombo;
    private ComboBox<QualityStandard> standardCombo;
    private VBox criteriaBox;
    private List<StandardCriteria> currentCriteria = new ArrayList<>();
    private List<TextField> valueFields = new ArrayList<>();

    public Node getView() {

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);

        VBox root = new VBox(15);
        root.setPadding(new Insets(15));

        Label title = new Label("Проведение инспекции");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        productCombo = new ComboBox<>();
        standardCombo = new ComboBox<>();
        criteriaBox = new VBox(10);

        loadProducts();

        productCombo.setOnAction(e -> loadStandards());
        standardCombo.setOnAction(e -> loadCriteria());

        Button submitBtn =
                ViewHelper.createButton("Завершить инспекцию", "#27ae60");

        submitBtn.setOnAction(e -> submitInspection());

        root.getChildren().addAll(
                title,
                new Label("Продукт:"), productCombo,
                new Label("Стандарт:"), standardCombo,
                new Label("Критерии:"), criteriaBox,
                submitBtn
        );

        scroll.setContent(root);
        return scroll;
    }

    @SuppressWarnings("unchecked")
    private void loadProducts() {
        try {
            Response resp =
                    NetworkClient.getInstance().sendRequest("GET_PRODUCTS");

            if (resp.isSuccess()) {
                productCombo.getItems()
                        .addAll((List<Product>) resp.getData());
            }
        } catch (Exception ignored) {}
    }

    @SuppressWarnings("unchecked")
    private void loadStandards() {
        standardCombo.getItems().clear();
        Product p = productCombo.getValue();
        if (p == null) return;

        try {
            Response resp =
                    NetworkClient.getInstance()
                            .sendRequest("GET_STANDARDS_BY_CATEGORY",
                                    p.getCategoryId());

            if (resp.isSuccess()) {
                standardCombo.getItems()
                        .addAll((List<QualityStandard>) resp.getData());
            }
        } catch (Exception ignored) {}
    }

    @SuppressWarnings("unchecked")
    private void loadCriteria() {
        criteriaBox.getChildren().clear();
        valueFields.clear();

        QualityStandard s = standardCombo.getValue();
        if (s == null) return;

        try {
            Response resp =
                    NetworkClient.getInstance()
                            .sendRequest("GET_STANDARD_CRITERIA",
                                    s.getId());

            if (resp.isSuccess()) {
                currentCriteria =
                        (List<StandardCriteria>) resp.getData();

                for (StandardCriteria sc : currentCriteria) {

                    HBox row = new HBox(10);

                    Label name =
                            new Label(sc.getCriterionName());

                    TextField value =
                            new TextField();
                    value.setPromptText("Введите значение");

                    valueFields.add(value);

                    row.getChildren().addAll(name, value);
                    criteriaBox.getChildren().add(row);
                }
            }

        } catch (Exception e) {
            ViewHelper.showError(e.getMessage());
        }
    }

    private void submitInspection() {

        if (productCombo.getValue() == null ||
                standardCombo.getValue() == null) {
            ViewHelper.showError("Выберите продукт и стандарт");
            return;
        }

        Inspection inspection = new Inspection();
        inspection.setProductId(productCombo.getValue().getId());
        inspection.setStandardId(standardCombo.getValue().getId());
        inspection.setInspectorId(Session.getCurrentUser().getId());

        List<InspectionResult> results = new ArrayList<>();

        for (int i = 0; i < currentCriteria.size(); i++) {

            try {
                double val =
                        Double.parseDouble(valueFields.get(i)
                                .getText());

                InspectionResult r =
                        new InspectionResult();
                r.setCriterionId(
                        currentCriteria.get(i).getCriterionId());
                r.setActualValue(val);
                results.add(r);

            } catch (Exception e) {
                ViewHelper.showError("Введите корректные значения");
                return;
            }
        }

        try {
            Response resp =
                    NetworkClient.getInstance()
                            .sendRequest("CONDUCT_INSPECTION",
                                    new Object[]{inspection, results});

            if (resp.isSuccess()) {

                Inspection result =
                        (Inspection) resp.getData();

                Alert alert = new Alert(
                        Alert.AlertType.INFORMATION);

                alert.setHeaderText(
                        result.getStatus());

                alert.setContentText(
                        "Балл: " + result.getOverallScore());

                alert.showAndWait();

                if ("FAILED".equals(result.getStatus())) {
                    if (ViewHelper.confirm(
                            "Добавить дефекты?")) {
                        addDefects(result.getId());
                    }
                }
            }

        } catch (Exception e) {
            ViewHelper.showError(e.getMessage());
        }
    }

    private void addDefects(int inspectionId) {

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Добавление дефектов");
        dialog.getDialogPane().getButtonTypes()
                .addAll(ButtonType.OK, ButtonType.CANCEL);

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        ComboBox<DefectType> typeCombo = new ComboBox<>();
        TextField quantityField = new TextField("1");
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Описание дефекта");

        TableView<Defect> defectTable = new TableView<>();
        defectTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Defect, String> typeCol =
                new TableColumn<>("Тип");
        typeCol.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getDefectTypeName()));

        TableColumn<Defect, String> qtyCol =
                new TableColumn<>("Кол-во");
        qtyCol.setCellValueFactory(c ->
                new SimpleStringProperty(
                        String.valueOf(c.getValue().getQuantity())));

        defectTable.getColumns().addAll(typeCol, qtyCol);

        // Загрузка типов дефектов
        try {
            Response resp =
                    NetworkClient.getInstance()
                            .sendRequest("GET_DEFECT_TYPES");

            if (resp.isSuccess()) {
                typeCombo.getItems()
                        .addAll((List<DefectType>) resp.getData());
            }

        } catch (Exception ignored) {}

        Button addBtn = new Button("Добавить в список");
        Button removeBtn = new Button("Удалить выбранный");

        addBtn.setOnAction(e -> {

            if (typeCombo.getValue() == null) {
                ViewHelper.showError("Выберите тип дефекта");
                return;
            }

            try {

                int qty = Integer.parseInt(quantityField.getText());

                Defect defect = new Defect();
                defect.setInspectionId(inspectionId);
                defect.setDefectTypeId(typeCombo.getValue().getId());
                defect.setDefectTypeName(typeCombo.getValue().getName());
                defect.setQuantity(qty);
                defect.setDescription(descriptionArea.getText());

                defectTable.getItems().add(defect);

                quantityField.setText("1");
                descriptionArea.clear();

            } catch (Exception ex) {
                ViewHelper.showError("Некорректное количество");
            }
        });

        removeBtn.setOnAction(e -> {
            Defect selected =
                    defectTable.getSelectionModel().getSelectedItem();

            if (selected != null) {
                defectTable.getItems().remove(selected);
            }
        });

        root.getChildren().addAll(
                new Label("Тип дефекта:"), typeCombo,
                new Label("Количество:"), quantityField,
                new Label("Описание:"), descriptionArea,
                addBtn,
                removeBtn,
                new Separator(),
                defectTable
        );

        dialog.getDialogPane().setContent(root);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    for (Defect d : defectTable.getItems()) {
                        NetworkClient.getInstance()
                                .sendRequest("ADD_DEFECT", d);
                    }
                    ViewHelper.showInfo("Дефекты сохранены");
                } catch (Exception ignored) {}
            }
            return null;
        });

        dialog.showAndWait();
    }
}