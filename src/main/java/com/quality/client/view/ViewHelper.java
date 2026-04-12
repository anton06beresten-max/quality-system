package com.quality.client.view;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Button;

public class ViewHelper {

    public static void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setHeaderText("Ошибка");
        alert.showAndWait();
    }

    public static void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setHeaderText("Информация");
        alert.showAndWait();
    }

    public static boolean confirm(String msg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Подтверждение");
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    public static Button createButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                "-fx-font-size: 13; -fx-padding: 8 16; -fx-cursor: hand;");
        return btn;
    }
}