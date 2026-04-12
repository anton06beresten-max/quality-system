package com.quality.client;

import com.quality.network.Request;
import com.quality.network.Response;

import java.io.*;
import java.net.Socket;

public class NetworkClient {

    private static NetworkClient instance;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String host = "localhost";
    private int port = 5555;

    private NetworkClient() {}

    public static synchronized NetworkClient getInstance() {
        if (instance == null) {
            instance = new NetworkClient();
        }
        return instance;
    }

    public void connect() throws IOException {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        System.out.println("Подключено к серверу " + host + ":" + port);
    }

    public void connect(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        connect();
    }

    public synchronized Response sendRequest(Request request) throws IOException, ClassNotFoundException {
        out.writeObject(request);
        out.flush();
        out.reset();
        return (Response) in.readObject();
    }

    public Response sendRequest(String action) throws IOException, ClassNotFoundException {
        return sendRequest(new Request(action));
    }

    public Response sendRequest(String action, Object data) throws IOException, ClassNotFoundException {
        return sendRequest(new Request(action, data));
    }

    public void disconnect() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}