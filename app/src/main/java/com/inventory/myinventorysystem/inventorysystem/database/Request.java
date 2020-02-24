package com.inventory.myinventorysystem.inventorysystem.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "requests")
public class Request {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "idProduct")
    private int idProduct;
    @ColumnInfo(name = "idRequestor")
    private int idRequestor;
    @ColumnInfo(name = "productName")
    private String productName;
    @ColumnInfo(name = "requestorName")
    private String requestorName;
    @ColumnInfo(name = "quantityRequest")
    private int quantityRequest;
    @ColumnInfo(name = "dateRequested")
    private String dateRequested;
    @ColumnInfo(name = "dateApproved")
    private String dateApproved;
    @ColumnInfo(name = "idApprover")
    private int idApprover;
    @ColumnInfo(name = "isApproved")
    private boolean isApproved;
    @ColumnInfo(name = "status")
    private int status; //0 = pending 1 = approved 2 = denied 3 = cancelled

    public Request(Integer id, int idProduct, int idRequestor, String productName, String requestorName
            , int quantityRequest, String dateRequested, String dateApproved, int idApprover, boolean isApproved, int status) {
        this.id = id;
        this.idProduct = idProduct;
        this.idRequestor = idRequestor;
        this.productName = productName;
        this.requestorName = requestorName;
        this.quantityRequest = quantityRequest;
        this.dateRequested = dateRequested;
        this.dateApproved = dateApproved;
        this.idApprover = idApprover;
        this.isApproved = isApproved;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int getIdProduct() {
        return idProduct;
    }

    public int getIdRequestor() {
        return idRequestor;
    }

    public String getProductName() {
        return productName;
    }

    public String getRequestorName() {
        return requestorName;
    }

    public int getQuantityRequest() {
        return quantityRequest;
    }

    public String getDateRequested() {
        return dateRequested;
    }

    public String getDateApproved() {
        return dateApproved;
    }

    public int getIdApprover() {
        return idApprover;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public int getStatus() {
        return status;
    }
}
