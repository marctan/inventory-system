package com.inventory.myinventorysystem.inventorysystem.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "username")
    private String username;
    @ColumnInfo(name = "password")
    private String password;
    @ColumnInfo(name = "firstname")
    private String firstname;
    @ColumnInfo(name = "lastname")
    private String lastname;
    @ColumnInfo(name = "isAdmin")
    private boolean isAdmin;

    public User(Integer id, String username, String password, String firstname, String lastname, boolean isAdmin) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.isAdmin = isAdmin;
    }



    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
