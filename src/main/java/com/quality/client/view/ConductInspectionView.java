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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ConductInspectionView {

    private ComboBox<Product> productCombo;
    private ComboBox<QualityStandard> standardCombo;
    private TextField batchField;
    private TextArea notesArea;
    private VBox criteriaBox;
    private List<StandardCriteria> currentCriteria = new ArrayList<>();
    private List<TextField> valueFields = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public Node getView() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);

        VBox root = new VBox(15);
        root.setPadding(new Insets(15));

        Label title = new Label("Проведение инспекции");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        // Шаг 1: Выбор продукта
        Label step1 = new Label("Шаг 1: Выберите продукт");
        step1.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        productCombo = new ComboBox<>();
        productCombo.setPrefWidth(400);
        productCombo.setConverter(new javafx.util.StringConverter<Product>() {
            public String toString(Product p) {
                return p != null ? p.getName() + " [" + p.getArticle() + "]" : "";
            }
            public Product fromString(String s) { return null; }
        });

        // Загрузка продуктов
        try {
            Response resp = NetworkClient.getInstance().sendRequest("GET_PRODUCTS");
            if (resp.isSuccess()) productCombo.getItems().addAll((List<Product>) resp.getData());
        } catch (Exception ignored) {}

        // Шаг 2: Выбор стандарта
        Label step2 = new Label("Шаг 2: Выберите стандарт");
        step2.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        standardCombo = new ComboBox<>();
        standardCombo.setPrefWidth(400);
        standardCombo.setConverter(new javafx.util.StringConverter<QualityStandard>() {
            public String toString(QualityStandard s) { return s != null ? s.getName() : ""; }
            public QualityStandard fromString(String s) { return null; }
        });

        // При выборе продукта — загрузить стандарты его категории
        productCombo.setOnAction(e -> {
            standardCombo.getItems().clear();
            criteriaBox.getChildren().clear();
            Product selected = productCombo.getValue();
            if (selected != null) {
                try {
                    Response resp = NetworkClient.getInstance()
                            .sendRequest("GET_STANDARDS_BY_CATEGORY", selected.getCategoryId());
                    if (resp.isSuccess()) {
                        standardCombo.getItems().addAll((List<QualityStandard>) resp.getData());
                    }
                } catch (Exception ignored2) {}
            }
        });

        // При выборе стандарта — загрузить критерии
        standardCombo.setOnAction(e -> loadCriteria());

        // Номер партии
        Label batchLabel = new Label("Номер партии (необязательно):");
        batchField = new TextField();
        batchField.setPrefWidth(400);

        // Шаг 3: Критерии
        Label step3 = new Label("Шаг 3: Введите фактические значения");
        step3.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        criteriaBox = new VBox(10);
        criteriaBox.setPadding(new Insets(10));
        criteriaBox.setStyle("-fx-background-color: white; -fx-border-color: #ddd; " +
                "-fx-border-radius: 5; -fx-background-radius: 5;");

        // Примечания
        Label notesLabel = new Label("Примечания:");
        notesArea = new TextArea();
        notesArea.setPrefRowCount(3);
        notesArea.setPrefWidth(400);

        // Кнопка отправки
        Button submitBtn = ViewHelper.createButton("Провести инспекцию", "#27ae60");
        submitBtn.setPrefWidth(400);
        submitBtn.setStyle(submitBtn.getStyle() + "-fx-font-size: 16; -fx-padding: 15;");
        submitBtn.setOnAction(e -> submitInspection());

        root.getChildren().addAll(
                title, new Separator(),
                step1, productCombo,
                step2, standardCombo,
                batchLabel, batchField,
                step3, criteriaBox,
                notesLabel, notesArea,
                new Separator(), submitBtn
        );

        scrollPane.setContent(root);
        return scrollPane;
    }

    @SuppressWarnings("unchecked")
    private void loadCriteria() {
        criteriaBox.getChildren().clear();
        currentCriteria.clear();
        valueFields.clear();

        QualityStandard standard = standardCombo.getValue();
        if (standard == null) return;

        try {
            Response resp = NetworkClient.getInstance()
                    .sendRequest("GET_STANDARD_CRITERIA", standard.getId());
            if (resp.isSuccess()) {
                currentCriteria = (List<StandardCriteria>) resp.getData();

                if (currentCriteria.isEmpty()) {
                    criteriaBox.getChildren().add(new Label("У стандарта нет критериев!"));
                    return;
                }

                // Заголовок таблицы критериев
                HBox header = new HBox(10);
                Label hName = new Label("Критерий");
                hName.setPrefWidth(200); hName.setStyle("-fx-font-weight: bold;");
                Label hRange = new Label("Норма");
                hRange.setPrefWidth(150); hRange.setStyle("-fx-font-weight: bold;");
                Label hWeight = new Label("Вес");
                hWeight.setPrefWidth(60); hWeight.setStyle("-fx-font-weight: bold;");
                Label hValue = new Label("Факт. значение");
                hValue.setPrefWidth(150); hValue.setStyle("-fx-font-weight: bold;");
                header.getChildren().addAll(hName, hRange, hWeight, hValue);
                criteriaBox.getChildren().add(header);
                criteriaBox.getChildren().add(new Separator());

                for (StandardCriteria sc : currentCriteria) {
                    HBox row = new HBox(10);

                    Label nameLabel = new Label(sc.getCriterionName() +
                            (sc.getCriterionUnit() != null ? " (" + sc.getCriterionUnit() + ")" : ""));
                    nameLabel.setPrefWidth(200);

                    Label rangeLabel = new Label(sc.getMinValue() + " — " + sc.getMaxValue());
                    rangeLabel.setPrefWidth(150);

                    Label weightLabel = new Label(String.valueOf(sc.getWeight()));
                    weightLabel.setPrefWidth(60);

                    TextField valueField = new TextField();
                    valueField.setPrefWidth(150);
                    valueField.setPromptText("Введите значение");
                    valueFields.add(valueField);

                    row.getChildren().addAll(nameLabel, rangeLabel, weightLabel, valueField);
                    criteriaBox.getChildren().add(row);
                }
            }
        } catch (Exception e) {
            ViewHelper.showError("Ошибка загрузки критериев: " + e.getMessage());
        }
    }

    private void submitInspection() {
        Product product = productCombo.getValue();
        QualityStandard standard = standardCombo.getValue();

        if (product == null || standard == null) {
            ViewHelper.showError("Выберите продукт и стандарт!");
            return;
        }

        if (currentCriteria.isEmpty()) {
            ViewHelper.showError("Нет критериев для оценки!");
            return;
        }

        // Создаём инспекцию
        Inspection inspection = new Inspection();
        inspection.setProductId(product.getId());
        inspection.setStandardId(standard.getId());
        inspection.setInspectorId(Session.getCurrentUser().getId());
        inspection.setBatchNumber(batchField.getText().trim());
        inspection.setNotes(notesArea.getText().trim());

        // Собираем результаты
        ArrayList<InspectionResult> results = new ArrayList<>();
        for (int i = 0; i < currentCriteria.size(); i++) {
            String valueText = valueFields.get(i).getText().trim();
            if (valueText.isEmpty()) {
                ViewHelper.showError("Заполните все значения!");
                return;
            }

            double actualValue;
            try {
                actualValue = Double.parseDouble(valueText);
            } catch (NumberFormatException e) {
                ViewHelper.showError("Некорректное число в поле '" +
                        currentCriteria.get(i).getCriterionName() + "'");
                return;
            }

            InspectionResult result = new InspectionResult();
            result.setCriterionId(currentCriteria.get(i).getCriterionId());
            result.setActualValue(actualValue);
            results.add(result);
        }

        // Отправляем на сервер
        try {
            Object[] params = new Object[]{inspection, (Serializable) results};
            Response resp = NetworkClient.getInstance().sendRequest("CONDUCT_INSPECTION", params);

            if (resp.isSuccess()) {
                Inspection result = (Inspection) resp.getData();
                String status = result.getStatus();
                String statusText = "PASSED".equals(status) ? "✅ ГОДЕН" : "❌ НЕ ГОДЕН";

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Результат инспекции");
                alert.setHeaderText(statusText);
                alert.setContentText(
                        "Продукт: " + product.getName() + "\n" +
                                "Стандарт: " + standard.getName() + "\n" +
                                "Итоговый балл: " + result.getOverallScore() + " / 100\n" +
                                "Статус: " + status
                );
                alert.showAndWait();

                // Очистить форму
                productCombo.setValue(null);
                standardCombo.getItems().clear();
                criteriaBox.getChildren().clear();
                batchField.clear();
                notesArea.clear();
            } else {
                ViewHelper.showError(resp.getMessage());
            }
        } catch (Exception e) {
            ViewHelper.showError("Ошибка: " + e.getMessage());
        }
    }
}