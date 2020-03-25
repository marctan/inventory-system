package com.inventory.myinventorysystem.inventorysystem.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Created by Marc Q. Tan on 15/03/2020.
 */
public class ReportsViewModel extends ViewModel {
    private MutableLiveData<Integer> reportType;

    public LiveData<Integer> getReportType () {
        if(reportType == null) {
            reportType = new MutableLiveData<>();
        }
        return reportType;
    }

    public void setReportType (int val) {
        reportType.setValue(val);
    }
}
