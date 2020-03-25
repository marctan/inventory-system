package com.inventory.myinventorysystem.inventorysystem.repository;

import android.content.Context;

import com.inventory.myinventorysystem.inventorysystem.database.InventoryDatabase;
import com.inventory.myinventorysystem.inventorysystem.database.Product;
import com.inventory.myinventorysystem.inventorysystem.database.ProductsDao;
import com.inventory.myinventorysystem.inventorysystem.database.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Marc Q. Tan on 04/03/2020.
 */
public class ProductRepository {

    private ProductsDao productsDao;
    private LiveData<List<Product>> products;
    private MutableLiveData<List<Product>> productFromRequests;

    private static ProductRepository instance;
    private Executor executor = Executors.newSingleThreadExecutor();

    private ProductRepository(Context ctx) {
        productsDao = InventoryDatabase.getInstance(ctx).productsDao();
        products = productsDao.getAllProducts();
    }

    public static ProductRepository getInstance(Context ctx) {
        if (instance == null) {
            instance = new ProductRepository(ctx);
        }
        return instance;
    }

    public LiveData<List<Product>> getAllProducts() {
        return products;
    }

    public Completable insert(Product product) {
        return Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                productsDao.insertProduct(product);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Product>> getAllProductsByQuery(String query) {
        return Single.fromCallable(new Callable<List<Product>>() {
            @Override
            public List<Product> call() throws Exception {
                return productsDao.getAllProductsByQuery(query);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
    }

    public Single<List<Product>> getAddedProductsByMonth(String month) {
        return Single.fromCallable(new Callable<List<Product>>() {
            @Override
            public List<Product> call() throws Exception {
                return productsDao.getAllProductsByMonth(month);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
    }

    public void delete(Product product) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                productsDao.deleteProduct(product);
            }
        });
    }

    public Completable update(Product product) {
        return Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                productsDao.updateProduct(product);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public LiveData<Product> getProductByID(int id) {
        return productsDao.getProduct(id);
    }

    public Completable updateQuantity(int id, int quantity) {
        return Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                productsDao.updateQuantity(id, quantity);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private void queryProductsFromRequests(List<Request> requests) {
        List<Product> products = new ArrayList<>();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                for (int x = 0; x < requests.size(); x++) {
                    products.add(productsDao.getProductFromRequests(requests.get(x).getIdProduct()));
                }
                productFromRequests.postValue(products);
            }
        });
    }

    public LiveData<List<Product>> getProductsFromRequests(List<Request> requests){
        productFromRequests = new MutableLiveData<>();
        queryProductsFromRequests(requests);
        return productFromRequests;
    }
}
