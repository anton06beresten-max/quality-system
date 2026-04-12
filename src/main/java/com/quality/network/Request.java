package com.quality.network;

import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    private String action;    // "LOGIN", "GET_PRODUCTS", "CREATE_INSPECTION" и т.д.
    private Object data;      // данные запроса (User, Product, строка, и т.д.)

    public Request() {}

    public Request(String action) {
        this.action = action;
    }

    public Request(String action, Object data) {
        this.action = action;
        this.data = data;
    }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }

    @Override
    public String toString() {
        return "Request{action='" + action + "'}";
    }
}