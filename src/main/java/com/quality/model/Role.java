package com.quality.model;

public class Role extends Entity {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;

    public Role() {}

    public Role(int id, String name) {
        super(id);
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "Role{id=" + id + ", name='" + name + "'}";
    }
}