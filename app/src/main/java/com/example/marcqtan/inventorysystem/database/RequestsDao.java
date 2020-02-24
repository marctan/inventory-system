package com.example.marcqtan.inventorysystem.database;

import com.example.marcqtan.inventorysystem.R;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

/**
 * Created by Marc Q. Tan on 18/02/2020.
 */
@Dao
public interface RequestsDao {
    @Query("SELECT * FROM requests where idApprover = 0 and status = 0")
    List<Request> getAllRequests();

    @Query("SELECT * FROM requests WHERE idRequestor = :id")
    List<Request> getAllRequestsByRequestor(int id);

    @Query("Select * from requests where id = :id limit 1")
    Request getRequest(int id);

    @Query("SELECT * FROM requests where isApproved = 1 and strftime('%m', dateApproved) = :month")
    List<Request> getApprovedRequestByMonth(String month);

    @Query("UPDATE requests set dateApproved = :date, idApprover = :idApprover, isApproved = :isApproved, status = :status " +
            "WHERE id = :id")
    void approveRequest(String date, int idApprover, boolean isApproved, int id, int status);

    @Query("DELETE FROM requests where idProduct = :id")
    void deleteRequestByProductId(int id);

    @Query("UPDATE requests SET status = 3 WHERE id = :id")
    void cancelRequest(int id);

    @Insert
    void insertRequest(Request request);

    @Update
    void updateRequest(Request request);

    @Delete
    void deleteRequest(Request request);
}
