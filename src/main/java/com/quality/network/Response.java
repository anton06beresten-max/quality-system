package com.quality.network;

import java.io.Serializable;

public class Response implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String message;
    private Object data;

    public Response() {}

    // Статические фабричные методы (удобство)
    public static Response ok(Object data) {
        Response r = new Response();
        r.success = true;
        r.message = "OK";
        r.data = data;
        return r;
    }

    public static Response ok(String message, Object data) {
        Response r = new Response();
        r.success = true;
        r.message = message;
        r.data = data;
        return r;
    }

    public static Response error(String message) {
        Response r = new Response();
        r.success = false;
        r.message = message;
        r.data = null;
        return r;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }

    @Override
    public String toString() {
        return "Response{success=" + success + ", message='" + message + "'}";
    }
}