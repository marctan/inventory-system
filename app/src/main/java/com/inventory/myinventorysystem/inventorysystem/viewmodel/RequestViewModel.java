package com.inventory.myinventorysystem.inventorysystem.viewmodel;

import android.app.Application;

import com.inventory.myinventorysystem.inventorysystem.database.Product;
import com.inventory.myinventorysystem.inventorysystem.database.Request;
import com.inventory.myinventorysystem.inventorysystem.repository.ProductRepository;
import com.inventory.myinventorysystem.inventorysystem.repository.RequestRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Marc Q. Tan on 07/03/2020.
 */
public class RequestViewModel extends AndroidViewModel {
    private RequestRepository requestRepository;

    private ProductRepository productRepository;

    private MutableLiveData<Boolean> approved;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private MutableLiveData<Boolean> canceled;
    private MutableLiveData<List<Request>> approvedRequestByMonth;
    private LiveData<List<Request>> allRequests;
    private MutableLiveData<Boolean> insertRequestStatus;

    public RequestViewModel(@NonNull Application application) {
        super(application);
        requestRepository = RequestRepository.getInstance(application);
        productRepository = ProductRepository.getInstance(application);
        approved = new MutableLiveData<>();
        canceled = new MutableLiveData<>();
    }

    public void deleteByID(int id) {
        requestRepository.deleteByID(id);
    }

    public void insert(Request request) {
        disposables.add(requestRepository.insert(request).subscribe(() -> insertRequestStatus.setValue(true)));
    }

    public LiveData<Boolean> getInsertRequestStatus() {
        if (insertRequestStatus == null) {
            insertRequestStatus = new MutableLiveData<>();
        }
        return insertRequestStatus;
    }

    public LiveData<List<Request>> getAllRequests(int id) {
        allRequests = requestRepository.getAllRequests(id);
        return allRequests;
    }

    public LiveData<Request> getRequest(int id) {
        return requestRepository.getRequest(id);
    }

    public void approveRequest(String date, int approver, boolean isApproved, int id, int status) {
        disposables.add(requestRepository.approveRequest(date, approver, isApproved, id, status).subscribe(
                () -> approved.setValue(isApproved)
        ));
    }

    public void cancelRequest(int id) {
        disposables.add(requestRepository.cancelRequest(id).subscribe(() -> canceled.setValue(true)));
    }

    public LiveData<Boolean> getApproved() {
        return approved;
    }

    public LiveData<Boolean> getCanceled() {
        return canceled;
    }

    public void queryApprovedRequestByMonth(String month) {
        disposables.add(requestRepository.getApprovedRequestByMonth(month).subscribe(requests ->
                approvedRequestByMonth.setValue(requests)));
    }

    public LiveData<List<Request>> getApprovedRequestByMonth() {
        if(approvedRequestByMonth == null) {
            approvedRequestByMonth = new MutableLiveData<>();
        }
        return approvedRequestByMonth;
    }

    public LiveData<List<Product>> getProductFromRequest(boolean bymonth) {
        return Transformations.switchMap(bymonth ? approvedRequestByMonth : allRequests,
                input -> productRepository.getProductsFromRequests(input));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}
