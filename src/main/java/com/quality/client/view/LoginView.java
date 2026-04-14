package com.quality.client.view;

import com.quality.client.NetworkClient;
import com.quality.client.Session;
import com.quality.model.User;
import com.quality.network.Response;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginView {

    private final Stage stage;

    public LoginView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        // Заголовок
        Label title = new Label("Оценка качества");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: #2c3e50;");

        Label subtitle = new Label("Авторизация");
        subtitle.setFont(Font.font("Arial", 16));
        subtitle.setStyle("-fx-text-fill: #7f8c8d;");

        // Поля ввода
        TextField usernameField = new TextField();
        usernameField.setPromptText("Логин");
        usernameField.setPrefWidth(300);
        usernameField.setStyle("-fx-font-size: 14; -fx-padding: 10;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Пароль");
        passwordField.setPrefWidth(300);
        passwordField.setStyle("-fx-font-size: 14; -fx-padding: 10;");

        // Кнопка входа
        Button loginBtn = new Button("Войти");
        loginBtn.setPrefWidth(300);
        loginBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                "-fx-font-size: 16; -fx-padding: 12; -fx-cursor: hand;");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 13;");

        // Обработчик входа
        loginBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Заполните все поля!");
                return;
            }

            try {
                NetworkClient client = NetworkClient.getInstance();
                if (!client.isConnected()) {
                    client.connect();
                }

                Response response = client.sendRequest("LOGIN", new String[]{username, password});

                if (response.isSuccess()) {
                    User user = (User) response.getData();
                    Session.setCurrentUser(user);
                    new MainView(stage).show();
                } else {
                    errorLabel.setText(response.getMessage());
                }
            } catch (Exception ex) {
                errorLabel.setText("Не удалось подключиться к серверу!");
            }
        });

        passwordField.setOnAction(e -> loginBtn.fire());

        // Компоновка
        VBox form = new VBox(15, title, subtitle, usernameField, passwordField, loginBtn, errorLabel);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(40));
        form.setMaxWidth(400);
        form.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        StackPane root = new StackPane(form);
        root.setStyle("-fx-background-color: #ecf0f1;");

        stage.setScene(new Scene(root));
        stage.show();
    }
}