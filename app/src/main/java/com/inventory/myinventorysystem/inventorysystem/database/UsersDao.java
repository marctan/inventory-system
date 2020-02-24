package com.inventory.myinventorysystem.inventorysystem.database;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UsersDao {
    @Query("SELECT * FROM users")
    List<User> getAllUsers();
    @Query("Select * from users where username = :username AND password = :password limit 1")
    User getUser(String username, String password);
    @Insert
    void insertUser(User user);
    @Update
    void updateUser(User user);
    @Delete
    void deleteUser(User user);
}
