package com.inventory.myinventorysystem.inventorysystem.viewmodel;

import android.app.Application;

import com.inventory.myinventorysystem.inventorysystem.database.Product;
import com.inventory.myinventorysystem.inventorysystem.repository.ProductRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Marc Q. Tan on 04/03/2020.
 */
public class ProductViewModel extends AndroidViewModel {
    private ProductRepository repository;
    private LiveData<List<Product>> products;
    private MutableLiveData<Boolean> updated;
    private MutableLiveData<List<Product>> productsFromQuery;
    private MutableLiveData<List<Product>> addedProductByMonth = new MutableLiveData<>();
    private CompositeDisposable disposable = new CompositeDisposable();
    private MutableLiveData<Boolean> updateProductComplete;
    private MutableLiveData<Boolean> insertProductComplete;

    public ProductViewModel(@NonNull Application application) {
        super(application);
        repository = ProductRepository.getInstance(application);
        products = repository.getAllProducts();
        updated = new MutableLiveData<>();
    }

    public LiveData<Boolean> getUpdated() {
        return updated;
    }

    public LiveData<List<Product>> getProducts() {
        return products;
    }

    public void insert(Product product) {
        disposable.add(repository.insert(product).subscribe(() -> insertProductComplete.setValue(true)));
    }

    public void update(Product product) {
        disposable.add(repository.update(product).subscribe(() -> updateProductComplete.setValue(true)));
    }

    public LiveData<Boolean> getUpdateStatus(){
        if(updateProductComplete == null) {
            updateProductComplete = new MutableLiveData<>();
        }
        return updateProductComplete;
    }

    public LiveData<Boolean> getInsertStatus() {
        if(insertProductComplete == null) {
            insertProductComplete = new MutableLiveData<>();
        }
        return insertProductComplete;
    }

    public void delete(Product product) {
        repository.delete(product);
    }

    public void queryProducts(String query) {
        disposable.add(repository.getAllProductsByQuery(query).subscribe(newProducts -> {
            productsFromQuery.setValue(newProducts);
        }));
    }

    public LiveData<List<Product>> getProductsFromQuery() {
        if(productsFromQuery == null) {
            productsFromQuery = new MutableLiveData<>();
        }
        return productsFromQuery;
    }

    public LiveData<Product> getProductById(int id) {
        return repository.getProductByID(id);
    }

    public void updateQuantity(int id, int quantity) {
        disposable.add(repository.updateQuantity(id, quantity).subscribe(() -> updated.setValue(true)));
    }

    public void queryAddedProductByMonth(String month) {
        disposable.add(repository.getAddedProductsByMonth(month)
                .subscribe(products1 -> addedProductByMonth.setValue(products1)));
    }

    public LiveData<List<Product>> getAddedProductByMonth() {
        return addedProductByMonth;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
