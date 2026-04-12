package com.quality.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final int port;
    private final ExecutorService threadPool;

    public Server() {
        Properties props = new Properties();
        try {
            InputStream input = getClass()
                    .getClassLoader()
                    .getResourceAsStream("server.properties");
            props.load(input);
        } catch (Exception e) {
            System.err.println("Не удалось загрузить server.properties, используем значения по умолчанию");
        }

        this.port = Integer.parseInt(props.getProperty("server.port", "5555"));
        int maxThreads = Integer.parseInt(props.getProperty("server.max_threads", "10"));
        this.threadPool = Executors.newFixedThreadPool(maxThreads);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("=================================");
            System.out.println("  Сервер запущен на порту " + port);
            System.out.println("  Ожидание подключений...");
            System.out.println("=================================");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[Сервер] Новое подключение: " +
                        clientSocket.getInetAddress().getHostAddress());

                ClientHandler handler = new ClientHandler(clientSocket);
                threadPool.execute(handler);
            }

        } catch (IOException e) {
            System.err.println("Ошибка сервера: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}