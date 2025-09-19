package com.example.nablarch.batch.model;

public class UserRow {
    public final int id;
    public final String name;
    public final String email;

    public UserRow(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
