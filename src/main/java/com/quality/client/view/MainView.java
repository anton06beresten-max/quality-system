package com.quality.client.view;

import com.quality.client.NetworkClient;
import com.quality.client.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MainView {

    private final Stage stage;
    private StackPane contentArea;
    private VBox menuBox;
    private Button activeButton;

    public MainView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        BorderPane root = new BorderPane();

        // === ШАПКА ===
        HBox header = createHeader();
        root.setTop(header);

        // === БОКОВОЕ МЕНЮ ===
        ScrollPane sidebar = createSidebar();
        root.setLeft(sidebar);

        // === КОНТЕНТ ===
        contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color: #f5f6fa;");
        contentArea.setPadding(new Insets(20));
        root.setCenter(contentArea);

        // Показываем панель управления по умолчанию
        switchView("dashboard");

        stage.setScene(new Scene(root));
        stage.show();
    }

    private HBox createHeader() {
        Label title = new Label("Система оценки качества продукции");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setStyle("-fx-text-fill: white;");

        Label userLabel = new Label(Session.getCurrentUser().getFullName() +
                " [" + Session.getRole() + "]");
        userLabel.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 13;");

        Button logoutBtn = new Button("Выход");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                "-fx-cursor: hand; -fx-padding: 8 16;");
        logoutBtn.setOnAction(e -> {
            Session.setCurrentUser(null);
            NetworkClient.getInstance().disconnect();
            new LoginView(stage).show();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(15, title, spacer, userLabel, logoutBtn);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: #2c3e50;");
        return header;
    }

    private ScrollPane createSidebar() {
        menuBox = new VBox(5);
        menuBox.setPadding(new Insets(15, 10, 15, 10));
        menuBox.setPrefWidth(220);

        // Общие для всех
        addMenuItem("Панель управления", "dashboard");

        // Администратор — видит всё
        if (Session.isAdmin()) {
            addSectionLabel("Администрирование");
            addMenuItem("Пользователи", "users");
            addSectionLabel("Справочники");
            addMenuItem("Категории", "categories");
            addMenuItem("Продукция", "products");
            addMenuItem("Стандарты", "standards");
            addMenuItem("Критерии", "criteria");
            addMenuItem("Критерии стандартов", "standard_criteria");
            addMenuItem("Типы дефектов", "defect_types");
            addSectionLabel("Контроль качества");
            addMenuItem("Провести инспекцию", "conduct");
            addMenuItem("Все инспекции", "inspections");
            addSectionLabel("Отчёты");
            addMenuItem("Аналитика", "analytics");}

        // Инспектор
        if (Session.isInspector()) {
            addSectionLabel("Контроль качества");
            addMenuItem("Провести инспекцию", "conduct");
            addMenuItem("Мои инспекции", "my_inspections");
        }

        // Менеджер
        if (Session.isManager()) {
            addSectionLabel("Справочники");
            addMenuItem("Категории", "categories");
            addMenuItem("Продукция", "products");
            addMenuItem("Стандарты", "standards");
            addMenuItem("Критерии", "criteria");
            addMenuItem("Типы дефектов", "defect_types");
            addSectionLabel("Контроль качества");
            addMenuItem("Все инспекции", "inspections");
            addSectionLabel("Отчёты");
            addMenuItem("Аналитика", "analytics");
        }

        ScrollPane scroll = new ScrollPane(menuBox);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #34495e; -fx-background: #34495e;");
        return scroll;
    }

    private void addMenuItem(String text, String viewId) {
        Button btn = new Button(text);
        btn.setPrefWidth(200);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle(menuButtonStyle(false));
        btn.setOnAction(e -> {
            if (activeButton != null) activeButton.setStyle(menuButtonStyle(false));
            btn.setStyle(menuButtonStyle(true));
            activeButton = btn;
            switchView(viewId);
        });
        menuBox.getChildren().add(btn);
    }

    private void addSectionLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 11; -fx-padding: 15 0 5 5;");
        menuBox.getChildren().add(label);
    }

    private String menuButtonStyle(boolean active) {
        if (active) {
            return "-fx-background-color: #3498db; -fx-text-fill: white; " +
                    "-fx-font-size: 14; -fx-padding: 10 15; -fx-cursor: hand; -fx-background-radius: 5;";
        }
        return "-fx-background-color: transparent; -fx-text-fill: #ecf0f1; " +
                "-fx-font-size: 14; -fx-padding: 10 15; -fx-cursor: hand; -fx-background-radius: 5;";
    }

    private void switchView(String viewId) {
        contentArea.getChildren().clear();
        Node view;
        switch (viewId) {
            case "dashboard":       view = new DashboardView().getView(); break;
            case "users":           view = new UserManagementView().getView(); break;
            case "categories":      view = new ProductCategoryView().getView(); break;
            case "products":        view = new ProductView().getView(); break;
            case "standards":       view = new QualityStandardView().getView(); break;
            case "criteria":        view = new CriteriaView().getView(); break;
            case "defect_types":    view = new DefectTypeView().getView(); break;
            case "conduct":         view = new ConductInspectionView().getView(); break;
            case "inspections":     view = new InspectionListView(false).getView(); break;
            case "my_inspections":  view = new InspectionListView(true).getView(); break;
            case "analytics":       view = new AnalyticsView().getView(); break;
            case "standard_criteria": view = new StandardCriteriaView().getView(); break;
            default:                view = new DashboardView().getView(); break;
        }
        contentArea.getChildren().add(view);
    }
}