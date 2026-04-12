package com.quality.client;

import com.quality.client.view.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

public class ClientApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        stage.setTitle("Система оценки качества продукции");
        stage.setWidth(1200);
        stage.setHeight(800);
        new LoginView(stage).show();
    }

    public static Stage getPrimaryStage() { return primaryStage; }

    public static void main(String[] args) {
        launch(args);
    }
}