package com.inventory.myinventorysystem.inventorysystem.Screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.inventory.myinventorysystem.inventorysystem.R;
import com.inventory.myinventorysystem.inventorysystem.Fragments.ReportRequestFragment;
import com.inventory.myinventorysystem.inventorysystem.Fragments.ReportSupplyFragment;
import com.inventory.myinventorysystem.inventorysystem.databinding.ActivityReportsBinding;
import com.inventory.myinventorysystem.inventorysystem.viewmodel.ProductViewModel;
import com.inventory.myinventorysystem.inventorysystem.viewmodel.ReportsViewModel;
import com.inventory.myinventorysystem.inventorysystem.viewmodel.RequestViewModel;

public class ReportsActivity extends AppCompatActivity {

    static int REPORT_VIEW = 0;//0 = request report 1 = supply report

    RequestViewModel requestViewModel;
    ProductViewModel productViewModel;
    ReportsViewModel reportsViewModel;

    ActivityReportsBinding binding;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reports);

        Toolbar myToolbar = binding.myToolbar;
        myToolbar.setTitle("Reports");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        requestViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(RequestViewModel.class);
        productViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(ProductViewModel.class);
        reportsViewModel = new ViewModelProvider(this).get(ReportsViewModel.class);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner_layout,
                getResources().getStringArray(R.array.months));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(adapter);

        ReportRequestFragment frag = new ReportRequestFragment();
        loadFragment(frag);
        REPORT_VIEW = 0;

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    binding.noMonth.setVisibility(View.GONE);
                    binding.container.setVisibility(View.VISIBLE);
                    binding.btnReportSwitch.setVisibility(View.VISIBLE);
                    String month = binding.spinner.getSelectedItem().toString();
                    String monthNumber = "";
                    switch (month) {
                        case "January":
                            monthNumber = "01";
                            break;
                        case "February":
                            monthNumber = "02";
                            break;
                        case "March":
                            monthNumber = "03";
                            break;
                        case "April":
                            monthNumber = "04";
                            break;
                        case "May":
                            monthNumber = "05";
                            break;
                        case "June":
                            monthNumber = "06";
                            break;
                        case "July":
                            monthNumber = "07";
                            break;
                        case "August":
                            monthNumber = "08";
                            break;
                        case "September":
                            monthNumber = "09";
                            break;
                        case "October":
                            monthNumber = "10";
                            break;
                        case "November":
                            monthNumber = "11";
                            break;
                        case "December":
                            monthNumber = "12";
                            break;
                    }
                    binding.progress.setVisibility(View.VISIBLE);
                    requestViewModel.queryApprovedRequestByMonth(monthNumber);
                    productViewModel.queryAddedProductByMonth(monthNumber);
                } else {
                    binding.noMonth.setVisibility(View.VISIBLE);
                    binding.container.setVisibility(View.GONE);
                    binding.btnReportSwitch.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.btnReportSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (REPORT_VIEW == 0) {//request report
                    REPORT_VIEW = 1; //if request report then change to supply report
                } else {//supply report
                    REPORT_VIEW = 0;
                }
                reportsViewModel.setReportType(REPORT_VIEW);
            }
        });

        reportsViewModel.getReportType().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer reportType) {
                binding.setReportType(reportType);
                Fragment frag;
                if (reportType == 0) { //request report
                    frag = new ReportRequestFragment();
                } else {//supply report
                    frag = new ReportSupplyFragment();
                }
                loadFragment(frag);
            }
        });

        binding.executePendingBindings();
    }

    void loadFragment(Fragment frag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container, frag);
        ft.commit();
    }

}
