package com.inventory.myinventorysystem.inventorysystem.repository;

import android.content.Context;

import com.inventory.myinventorysystem.inventorysystem.database.InventoryDatabase;
import com.inventory.myinventorysystem.inventorysystem.database.Request;
import com.inventory.myinventorysystem.inventorysystem.database.RequestsDao;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Marc Q. Tan on 07/03/2020.
 */
public class RequestRepository {

    private static RequestRepository instance;

    private Executor executor = Executors.newSingleThreadExecutor();

    private RequestsDao requestsDao;
    private LiveData<List<Request>> requests;

    private RequestRepository(Context ctx) {
        requestsDao = InventoryDatabase.getInstance(ctx).requestDao();
    }

    public static RequestRepository getInstance(Context ctx) {
        if (instance == null) {
            instance = new RequestRepository(ctx);
        }

        return instance;
    }

    public void deleteByID(int id) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                requestsDao.deleteRequestByProductId(id);
            }
        });
    }

    public Completable insert(Request request) {
        return Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                requestsDao.insertRequest(request);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public LiveData<List<Request>> getAllRequests(int id) {
        if (id == -1) {
            requests = requestsDao.getAllRequests();
        } else {
            requests = requestsDao.getAllRequestsByRequestor(id);
        }
        return requests;
    }

    public LiveData<Request> getRequest(int id) {
        return requestsDao.getRequest(id);
    }

    public Completable approveRequest(String date, int approver, boolean isApproved, int id, int status) {
        return Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                requestsDao.approveRequest(date, approver, isApproved, id, status);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Completable cancelRequest(int id) {
        return Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                requestsDao.cancelRequest(id);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Request>> getApprovedRequestByMonth(String month) {
        return Single.fromCallable(new Callable<List<Request>>() {
            @Override
            public List<Request> call() throws Exception {
                return requestsDao.getApprovedRequestByMonth(month);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
    }
}

