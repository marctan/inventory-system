package com.inventory.myinventorysystem.inventorysystem.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "products")
public class Product {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name="description")
    private String description;
    @ColumnInfo(name="name")
    private String name;
    @ColumnInfo(name="imageURI")
    private String imageURI;
    @ColumnInfo(name="dateAdded")
    private String dateAdded;
    @ColumnInfo(name="quantity")
    private int quantity;

    public Product(Integer id, String description, String name, String imageURI, String dateAdded, int quantity) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.imageURI = imageURI;
        this.dateAdded = dateAdded;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getImageURI() {
        return imageURI;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public int getQuantity() {
        return quantity;
    }
}
