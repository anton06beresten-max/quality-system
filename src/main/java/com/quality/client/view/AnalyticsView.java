package com.quality.client.view;

import com.quality.client.NetworkClient;
import com.quality.network.Response;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.Map;

public class AnalyticsView {

    @SuppressWarnings("unchecked")
    public Node getView() {
        TabPane tabs = new TabPane();

        tabs.getTabs().addAll(
                createRatingsTab(),
                createDefectStatsTab(),
                createCategoryComparisonTab()
        );

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label title = new Label("Аналитика и отчёты");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        VBox.setVgrow(tabs, Priority.ALWAYS);
        root.getChildren().addAll(title, tabs);
        return root;
    }

    @SuppressWarnings("unchecked")
    private Tab createRatingsTab() {
        Tab tab = new Tab("Рейтинг продукции");
        tab.setClosable(false);

        VBox content = new VBox(15);
        content.setPadding(new Insets(15));

        Button loadBtn = ViewHelper.createButton("Загрузить рейтинг", "#3498db");

        // Таблица
        TableView<Map<String, Object>> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Map<String, Object>, String> nameCol = new TableColumn<>("Продукт");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().get("productName"))));

        TableColumn<Map<String, Object>, String> artCol = new TableColumn<>("Артикул");
        artCol.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().get("article"))));

        TableColumn<Map<String, Object>, String> scoreCol = new TableColumn<>("Средний балл");
        scoreCol.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().get("avgScore"))));

        TableColumn<Map<String, Object>, String> cntCol = new TableColumn<>("Инспекций");
        cntCol.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().get("inspectionCount"))));

        table.getColumns().addAll(nameCol, artCol, scoreCol, cntCol);

        // График
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis(0, 100, 10);
        xAxis.setLabel("Продукт");
        yAxis.setLabel("Средний балл");
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Рейтинг продукции по качеству");
        chart.setPrefHeight(300);
        chart.setLegendVisible(false);

        loadBtn.setOnAction(e -> {
            try {
                Response resp = NetworkClient.getInstance().sendRequest("GET_PRODUCT_RATINGS");
                if (resp.isSuccess()) {
                    List<Map<String, Object>> data = (List<Map<String, Object>>) resp.getData();
                    table.getItems().setAll(FXCollections.observableArrayList(data));

                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    for (Map<String, Object> row : data) {
                        String name = String.valueOf(row.get("productName"));
                        double score = ((Number) row.get("avgScore")).doubleValue();
                        series.getData().add(new XYChart.Data<>(name, score));
                    }
                    chart.getData().clear();
                    chart.getData().add(series);
                }
            } catch (Exception ex) { ViewHelper.showError(ex.getMessage()); }
        });

        VBox.setVgrow(table, Priority.ALWAYS);
        content.getChildren().addAll(loadBtn, chart, table);
        tab.setContent(content);
        return tab;
    }

    @SuppressWarnings("unchecked")
    private Tab createDefectStatsTab() {
        Tab tab = new Tab("Статистика дефектов");
        tab.setClosable(false);

        VBox content = new VBox(15);
        content.setPadding(new Insets(15));

        Button loadBtn = ViewHelper.createButton("Загрузить статистику", "#3498db");

        // Таблица
        TableView<Map<String, Object>> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Map<String, Object>, String> typeCol = new TableColumn<>("Тип дефекта");
        typeCol.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().get("defectType"))));

        TableColumn<Map<String, Object>, String> sevCol = new TableColumn<>("Серьёзность");
        sevCol.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().get("severity"))));

        TableColumn<Map<String, Object>, String> cntCol = new TableColumn<>("Количество");
        cntCol.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().get("totalQuantity"))));

        table.getColumns().addAll(typeCol, sevCol, cntCol);

        // Круговая диаграмма
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Распределение дефектов");
        pieChart.setPrefHeight(300);

        loadBtn.setOnAction(e -> {
            try {
                Response resp = NetworkClient.getInstance().sendRequest("GET_DEFECT_STATISTICS");
                if (resp.isSuccess()) {
                    List<Map<String, Object>> data = (List<Map<String, Object>>) resp.getData();
                    table.getItems().setAll(FXCollections.observableArrayList(data));

                    pieChart.getData().clear();
                    for (Map<String, Object> row : data) {
                        String name = String.valueOf(row.get("defectType"));
                        int qty = ((Number) row.get("totalQuantity")).intValue();
                        if (qty > 0) {
                            pieChart.getData().add(new PieChart.Data(name, qty));
                        }
                    }
                }
            } catch (Exception ex) { ViewHelper.showError(ex.getMessage()); }
        });

        VBox.setVgrow(table, Priority.ALWAYS);
        content.getChildren().addAll(loadBtn, pieChart, table);
        tab.setContent(content);
        return tab;
    }

    @SuppressWarnings("unchecked")
    private Tab createCategoryComparisonTab() {
        Tab tab = new Tab("Сравнение по категориям");
        tab.setClosable(false);

        VBox content = new VBox(15);
        content.setPadding(new Insets(15));

        Button loadBtn = ViewHelper.createButton("Загрузить данные", "#3498db");

        // Таблица
        TableView<Map<String, Object>> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Map<String, Object>, String> catCol = new TableColumn<>("Категория");
        catCol.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().get("categoryName"))));

        TableColumn<Map<String, Object>, String> scoreCol = new TableColumn<>("Средний балл");
        scoreCol.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().get("avgScore"))));

        TableColumn<Map<String, Object>, String> prodCol = new TableColumn<>("Продуктов");
        prodCol.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().get("productCount"))));

        TableColumn<Map<String, Object>, String> inspCol = new TableColumn<>("Инспекций");
        inspCol.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().get("inspectionCount"))));

        table.getColumns().addAll(catCol, scoreCol, prodCol, inspCol);

        // Гистограмма
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis(0, 100, 10);
        xAxis.setLabel("Категория");
        yAxis.setLabel("Средний балл");
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Сравнение качества по категориям");
        chart.setPrefHeight(300);
        chart.setLegendVisible(false);

        loadBtn.setOnAction(e -> {
            try {
                Response resp = NetworkClient.getInstance().sendRequest("GET_CATEGORY_COMPARISON");
                if (resp.isSuccess()) {
                    List<Map<String, Object>> data = (List<Map<String, Object>>) resp.getData();
                    table.getItems().setAll(FXCollections.observableArrayList(data));

                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    for (Map<String, Object> row : data) {
                        String name = String.valueOf(row.get("categoryName"));
                        double score = ((Number) row.get("avgScore")).doubleValue();
                        series.getData().add(new XYChart.Data<>(name, score));
                    }
                    chart.getData().clear();
                    chart.getData().add(series);
                }
            } catch (Exception ex) { ViewHelper.showError(ex.getMessage()); }
        });

        VBox.setVgrow(table, Priority.ALWAYS);
        content.getChildren().addAll(loadBtn, chart, table);
        tab.setContent(content);
        return tab;
    }
}