package com.quality.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public abstract class Entity implements Serializable {
    private static final long serialVersionUID = 1L;

    protected int id;
    protected LocalDateTime createdAt;

    public Entity() {
        this.createdAt = LocalDateTime.now();
    }

    public Entity(int id) {
        this.id = id;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id + "}";
    }

    // Перегрузка (overloading) — два варианта метода
    public String getInfo() {
        return toString();
    }

    public String getInfo(boolean detailed) {
        if (detailed) {
            return toString() + ", createdAt=" + createdAt;
        }
        return toString();
    }
}