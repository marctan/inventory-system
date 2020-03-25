package com.inventory.myinventorysystem.inventorysystem.database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ProductsDao {
    @Query("Select * from products")
    LiveData<List<Product>> getAllProducts();

    @Query("SELECT * FROM products WHERE name LIKE :query")
    List<Product> getAllProductsByQuery(String query);

    @Query("Select * from products where id = :id limit 1")
    LiveData<Product> getProduct(int id);

    @Query("UPDATE products SET quantity = quantity - :quantity where ID = :id")
    void updateQuantity(int id, int quantity);

    @Query("SELECT * from products where strftime('%m', dateAdded) = :month")
    List<Product> getAllProductsByMonth(String month);

    @Query("SELECT * FROM PRODUCTS where id in (:request_id)")
    Product getProductFromRequests(int request_id);

    @Delete
    void deleteProduct(Product product);

    @Insert
    void insertProduct(Product product);

    @Update
    void updateProduct(Product product);
}
