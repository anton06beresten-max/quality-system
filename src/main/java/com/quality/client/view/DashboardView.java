package com.quality.client.view;

import com.quality.client.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class DashboardView {

    public Node getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));

        Label welcome = new Label("Добро пожаловать, " + Session.getCurrentUser().getFullName() + "!");
        welcome.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Label role = new Label("Роль: " + Session.getRole());
        role.setFont(Font.font("Arial", 16));
        role.setStyle("-fx-text-fill: #7f8c8d;");

        // Карточки
        HBox cards = new HBox(20);
        cards.setPadding(new Insets(20, 0, 0, 0));

        if (Session.isAdmin()) {
            cards.getChildren().addAll(
                    createCard("Пользователи", "Управление учётными записями", "#3498db"),
                    createCard("Справочники", "Категории, критерии, типы дефектов", "#2ecc71"),
                    createCard("Инспекции", "Все проверки качества", "#e67e22"),
                    createCard("Аналитика", "Отчёты и статистика", "#9b59b6")
            );
        } else if (Session.isInspector()) {
            cards.getChildren().addAll(
                    createCard("Новая инспекция", "Провести проверку качества", "#3498db"),
                    createCard("Мои инспекции", "История ваших проверок", "#2ecc71")
            );
        } else if (Session.isManager()) {
            cards.getChildren().addAll(
                    createCard("Продукция", "Управление каталогом", "#3498db"),
                    createCard("Стандарты", "Стандарты качества", "#2ecc71"),
                    createCard("Инспекции", "Все проверки", "#e67e22"),
                    createCard("Аналитика", "Отчёты и рейтинги", "#9b59b6")
            );
        }

        root.getChildren().addAll(welcome, role, cards);
        return root;
    }

    private VBox createCard(String title, String description, String color) {
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setStyle("-fx-text-fill: white;");

        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.8); -fx-font-size: 12;");
        descLabel.setWrapText(true);

        VBox card = new VBox(10, titleLabel, descLabel);
        card.setPrefWidth(200);
        card.setPrefHeight(120);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.TOP_LEFT);
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 5, 0, 0, 2);");
        return card;
    }
}