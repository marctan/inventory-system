package com.inventory.myinventorysystem.inventorysystem.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {User.class, Product.class, Request.class}, version = 1, exportSchema = false)
public abstract class InventoryDatabase extends RoomDatabase {
    private static final String DB_NAME = "inventory-db";
    private static InventoryDatabase instance;

    public static synchronized InventoryDatabase getInstance(Context ctx) {
        if(instance == null){
            instance = Room.databaseBuilder(ctx,
                    InventoryDatabase.class, DB_NAME).build();
        }
        return instance;
    }

    public abstract UsersDao userDao();
    public abstract ProductsDao productsDao();
    public abstract RequestsDao requestDao();
}
