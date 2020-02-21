package com.example.marcqtan.inventorysystem.database;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

/**
 * Created by Marc Q. Tan on 16/02/2020.
 */
@Dao
public interface ProductsDao {
    @Query("Select * from products")
    List<Product> getAllProducts();

    @Query("Select * from products where id = :id limit 1")
    Product getProduct(int id);

    @Query("UPDATE products SET quantity = quantity + :quantity where ID = :id")
    void updateQuantity(int id, int quantity);

    @Query("SELECT * from products where strftime('%m', dateAdded) = :month")
    List<Product> getAllProductsByMonth(String month);

    @Delete
    void deleteProduct(Product product);

    @Insert
    void insertProduct(Product product);

    @Update
    void updateProduct(Product product);
}
