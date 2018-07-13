package com.pratham.admin.util;

public class CustomGroup {
    String name;
    String id;
    public CustomGroup(String name, String id) {
        this.name = name;
        this.id = id;
    }
    public CustomGroup(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
    @Override
    public String toString() {
        return name;
    }
}
