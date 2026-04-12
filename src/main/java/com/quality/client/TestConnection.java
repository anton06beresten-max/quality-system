package com.quality.client;

import com.quality.network.Request;
import com.quality.network.Response;

import java.io.*;
import java.net.Socket;

public class TestConnection {

    public static void main(String[] args) {
        String host = "localhost";
        int port = 5555;

        try (Socket socket = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            System.out.println("Подключено к серверу!");

            // Тест 1: PING
            out.writeObject(new Request("PING"));
            out.flush();
            Response response = (Response) in.readObject();
            System.out.println("PING → " + response);

            // Тест 2: LOGIN (обычный пароль — сервер сам хеширует)
            out.writeObject(new Request("LOGIN", new String[]{"admin", "admin123"}));
            out.flush();
            response = (Response) in.readObject();
            System.out.println("LOGIN → " + response);

            if (response.isSuccess()) {
                System.out.println("Пользователь: " + response.getData());
            }

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}