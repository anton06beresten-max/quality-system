package com.quality.client.view;

import com.quality.client.NetworkClient;
import com.quality.model.Product;
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
                createTrendTab()
        );
        tabs.getTabs().add(createDefectPieTab());
        VBox root = new VBox(15);
        root.setPadding(new Insets(15));

        Label title = new Label("Аналитика");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));

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

        Button loadBtn =
                ViewHelper.createButton("Загрузить рейтинг", "#3498db");

        TableView<Map<String, Object>> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Map<String, Object>, String> nameCol =
                new TableColumn<>("Продукт");
        nameCol.setCellValueFactory(c ->
                new SimpleStringProperty(
                        String.valueOf(c.getValue().get("productName"))));

        TableColumn<Map<String, Object>, String> scoreCol =
                new TableColumn<>("Средний балл");
        scoreCol.setCellValueFactory(c ->
                new SimpleStringProperty(
                        String.valueOf(c.getValue().get("avgScore"))));

        table.getColumns().addAll(nameCol, scoreCol);

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setTickLabelRotation(-45);
        xAxis.setStyle("-fx-tick-label-font-size: 10;");

        NumberAxis yAxis = new NumberAxis(0, 100, 10);
        yAxis.setLabel("Средний балл");

        BarChart<String, Number> chart =
                new BarChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setPrefHeight(400);

        loadBtn.setOnAction(e -> {

            try {

                Response resp =
                        NetworkClient.getInstance()
                                .sendRequest("GET_PRODUCT_RATINGS");

                if (resp.isSuccess()) {

                    List<Map<String, Object>> data =
                            (List<Map<String, Object>>) resp.getData();

                    table.setItems(
                            FXCollections.observableArrayList(data));

                    XYChart.Series<String, Number> series =
                            new XYChart.Series<>();

                    for (Map<String, Object> row : data) {

                        String name =
                                String.valueOf(row.get("productName"));

                        double score =
                                ((Number) row.get("avgScore"))
                                        .doubleValue();

                        series.getData().add(
                                new XYChart.Data<>(name, score));
                    }

                    chart.getData().clear();
                    chart.getData().add(series);
                }

            } catch (Exception ex) {
                ViewHelper.showError("Ошибка аналитики: "
                        + ex.getMessage());
            }
        });

        VBox.setVgrow(table, Priority.ALWAYS);
        content.getChildren().addAll(loadBtn, chart, table);
        tab.setContent(content);

        return tab;
    }


    @SuppressWarnings("unchecked")
    private Tab createTrendTab() {

        Tab tab = new Tab("Тренд качества");
        tab.setClosable(false);

        VBox content = new VBox(15);
        content.setPadding(new Insets(15));

        ComboBox<Product> productCombo =
                new ComboBox<>();
        productCombo.setPrefWidth(350);

        Button loadBtn =
                ViewHelper.createButton("Построить график", "#27ae60");

        // Загружаем продукты
        try {
            Response resp =
                    NetworkClient.getInstance()
                            .sendRequest("GET_PRODUCTS");

            if (resp.isSuccess()) {
                productCombo.getItems()
                        .addAll((List<Product>)
                                resp.getData());
            }

        } catch (Exception ignored) {}

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Дата");

        NumberAxis yAxis = new NumberAxis(0, 100, 10);
        yAxis.setLabel("Средний балл");

        LineChart<String, Number> lineChart =
                new LineChart<>(xAxis, yAxis);

        lineChart.setTitle("Динамика качества");
        lineChart.setAnimated(false);
        lineChart.setPrefHeight(450);

        loadBtn.setOnAction(e -> {

            if (productCombo.getValue() == null) {
                ViewHelper.showError("Выберите продукт");
                return;
            }

            try {

                Response resp =
                        NetworkClient.getInstance()
                                .sendRequest(
                                        "GET_QUALITY_TREND",
                                        productCombo.getValue().getId());

                if (resp.isSuccess()) {

                    List<Map<String, Object>> data =
                            (List<Map<String, Object>>) resp.getData();

                    XYChart.Series<String, Number> series =
                            new XYChart.Series<>();

                    series.setName(
                            productCombo.getValue().getName());

                    for (Map<String, Object> row : data) {

                        String date =
                                String.valueOf(row.get("date"));

                        double score =
                                ((Number) row.get("avgScore"))
                                        .doubleValue();

                        series.getData().add(
                                new XYChart.Data<>(date, score));
                    }

                    lineChart.getData().clear();
                    lineChart.getData().add(series);
                }

            } catch (Exception ex) {
                ViewHelper.showError("Ошибка тренда: "
                        + ex.getMessage());
            }
        });

        content.getChildren().addAll(
                new Label("Продукт:"),
                productCombo,
                loadBtn,
                lineChart
        );

        tab.setContent(content);
        return tab;
    }
    @SuppressWarnings("unchecked")
    private Tab createDefectPieTab() {

        Tab tab = new Tab("Статистика дефектов");
        tab.setClosable(false);

        VBox content = new VBox(15);
        content.setPadding(new Insets(15));

        Button loadBtn =
                ViewHelper.createButton("Загрузить дефекты", "#e67e22");

        PieChart pieChart = new PieChart();
        pieChart.setTitle("Распределение дефектов");

        loadBtn.setOnAction(e -> {

            try {

                Response resp =
                        NetworkClient.getInstance()
                                .sendRequest("GET_DEFECT_STATISTICS");

                if (resp.isSuccess()) {

                    List<Map<String, Object>> data =
                            (List<Map<String, Object>>) resp.getData();

                    pieChart.getData().clear();

                    for (Map<String, Object> row : data) {

                        int qty =
                                ((Number) row.get("totalQuantity"))
                                        .intValue();

                        if (qty > 0) {
                            pieChart.getData().add(
                                    new PieChart.Data(
                                            String.valueOf(
                                                    row.get("defectType")),
                                            qty
                                    )
                            );
                        }
                    }
                }

            } catch (Exception ex) {
                ViewHelper.showError("Ошибка дефектов: "
                        + ex.getMessage());
            }
        });

        content.getChildren().addAll(loadBtn, pieChart);

        tab.setContent(content);
        return tab;
    }
}