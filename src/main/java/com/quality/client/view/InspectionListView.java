package com.quality.client.view;

import com.quality.client.NetworkClient;
import com.quality.client.Session;
import com.quality.model.Defect;
import com.quality.model.Inspection;
import com.quality.model.InspectionResult;
import com.quality.network.Response;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class InspectionListView {

    private final boolean myOnly;
    private TableView<Inspection> table;

    public InspectionListView(boolean myOnly) {
        this.myOnly = myOnly;
    }

    @SuppressWarnings("unchecked")
    public Node getView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));

        Label title = new Label(myOnly ? "Мои инспекции" : "Все инспекции");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        // Фильтр по периоду
        DatePicker dateFrom = new DatePicker();
        dateFrom.setPromptText("Дата с");
        DatePicker dateTo = new DatePicker();
        dateTo.setPromptText("Дата по");
        Button filterBtn = ViewHelper.createButton("Фильтр", "#8e44ad");
        Button detailsBtn = ViewHelper.createButton("Подробнее", "#2980b9");
        Button refreshBtn = ViewHelper.createButton("Обновить", "#3498db");

        filterBtn.setOnAction(e -> {
            if (dateFrom.getValue() != null && dateTo.getValue() != null) {
                filterByPeriod(dateFrom.getValue().toString(), dateTo.getValue().toString() + " 23:59:59");
            }
        });

        HBox toolbar = new HBox(10, dateFrom, dateTo, filterBtn, detailsBtn, refreshBtn);

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Inspection, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        idCol.setPrefWidth(50);

        TableColumn<Inspection, String> prodCol = new TableColumn<>("Продукт");
        prodCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProductName()));

        TableColumn<Inspection, String> stdCol = new TableColumn<>("Стандарт");
        stdCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStandardName()));

        TableColumn<Inspection, String> inspCol = new TableColumn<>("Инспектор");
        inspCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getInspectorName()));

        TableColumn<Inspection, String> dateCol = new TableColumn<>("Дата");
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getInspectionDate() != null ?
                        c.getValue().getInspectionDate().toString().replace("T", " ").substring(0, 16) : ""));

        TableColumn<Inspection, Number> scoreCol = new TableColumn<>("Балл");
        scoreCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getOverallScore()));

        TableColumn<Inspection, String> statusCol = new TableColumn<>("Статус");
        statusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));
        statusCol.setCellFactory(col -> new TableCell<Inspection, String>() {
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                if ("PASSED".equals(item))
                    setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                else if ("FAILED".equals(item))
                    setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                else
                    setStyle("-fx-text-fill: orange;");
            }
        });

        table.getColumns().addAll(idCol, prodCol, stdCol, inspCol, dateCol, scoreCol, statusCol);
        VBox.setVgrow(table, Priority.ALWAYS);

        detailsBtn.setOnAction(e -> showDetails());
        refreshBtn.setOnAction(e -> loadData());

        root.getChildren().addAll(title, toolbar, table);
        loadData();
        return root;
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        try {
            String action = myOnly ? "GET_MY_INSPECTIONS" : "GET_ALL_INSPECTIONS";
            Object data = myOnly ? Session.getCurrentUser().getId() : null;
            Response resp;
            if (data != null) {
                resp = NetworkClient.getInstance().sendRequest(action, data);
            } else {
                resp = NetworkClient.getInstance().sendRequest(action);
            }
            if (resp.isSuccess()) table.getItems().setAll((List<Inspection>) resp.getData());
        } catch (Exception e) { ViewHelper.showError(e.getMessage()); }
    }

    @SuppressWarnings("unchecked")
    private void filterByPeriod(String from, String to) {
        try {
            Response resp = NetworkClient.getInstance()
                    .sendRequest("GET_REPORT_BY_PERIOD", new String[]{from, to});
            if (resp.isSuccess()) table.getItems().setAll((List<Inspection>) resp.getData());
        } catch (Exception e) { ViewHelper.showError(e.getMessage()); }
    }

    @SuppressWarnings("unchecked")
    private void showDetails() {
        Inspection sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { ViewHelper.showError("Выберите инспекцию!"); return; }

        try {
            Response resp = NetworkClient.getInstance()
                    .sendRequest("GET_INSPECTION_DETAILS", sel.getId());
            if (!resp.isSuccess()) { ViewHelper.showError(resp.getMessage()); return; }

            List<Object> details = (List<Object>) resp.getData();
            List<InspectionResult> results = (List<InspectionResult>) details.get(0);
            List<Defect> defects = (List<Defect>) details.get(1);

            // Диалог с деталями
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Детали инспекции #" + sel.getId());
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.getDialogPane().setPrefSize(700, 500);

            // Результаты по критериям
            TableView<InspectionResult> resTable = new TableView<>();
            resTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            TableColumn<InspectionResult, String> crCol = new TableColumn<>("Критерий");
            crCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCriterionName()));

            TableColumn<InspectionResult, Number> valCol = new TableColumn<>("Значение");
            valCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getActualValue()));

            TableColumn<InspectionResult, String> passCol = new TableColumn<>("Годен");
            passCol.setCellValueFactory(c -> new SimpleStringProperty(
                    c.getValue().isPassed() ? "✅ Да" : "❌ Нет"));

            resTable.getColumns().addAll(crCol, valCol, passCol);
            resTable.getItems().addAll(results);

            // Дефекты
            TableView<Defect> defTable = new TableView<>();
            defTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            TableColumn<Defect, String> dtCol = new TableColumn<>("Тип дефекта");
            dtCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDefectTypeName()));

            TableColumn<Defect, Integer> qtyCol = new TableColumn<>("Кол-во");
            qtyCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getQuantity()).asObject());

            TableColumn<Defect, String> ddCol = new TableColumn<>("Описание");
            ddCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription()));

            defTable.getColumns().addAll(dtCol, qtyCol, ddCol);
            defTable.getItems().addAll(defects);

            // Информация
            String info = "Продукт: " + sel.getProductName() + "\n" +
                    "Стандарт: " + sel.getStandardName() + "\n" +
                    "Инспектор: " + sel.getInspectorName() + "\n" +
                    "Балл: " + sel.getOverallScore() + " | Статус: " + sel.getStatus();
            Label infoLabel = new Label(info);
            infoLabel.setStyle("-fx-font-size: 13;");

            TabPane tabs = new TabPane();
            Tab resTab = new Tab("Результаты (" + results.size() + ")", resTable);
            resTab.setClosable(false);
            Tab defTab = new Tab("Дефекты (" + defects.size() + ")", defTable);
            defTab.setClosable(false);
            tabs.getTabs().addAll(resTab, defTab);

            VBox content = new VBox(10, infoLabel, new Separator(), tabs);
            content.setPadding(new Insets(10));
            dialog.getDialogPane().setContent(content);
            dialog.showAndWait();

        } catch (Exception e) { ViewHelper.showError(e.getMessage()); }
    }
}