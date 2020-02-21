package com.example.marcqtan.inventorysystem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marcqtan.inventorysystem.database.InventoryDatabase;
import com.example.marcqtan.inventorysystem.database.Product;
import com.example.marcqtan.inventorysystem.database.Request;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ReportsActivity extends AppCompatActivity {

    @BindView(R.id.spinner)
    Spinner monthsList;
    @BindView(R.id.rvrequestreport)
    RecyclerView rvrequest;
    @BindView(R.id.rvsuppliesreport)
    RecyclerView rvsupply;
    @BindView(R.id.noMonth)
    TextView no_month;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.listsupply)
    TextView listsupply;
    @BindView(R.id.listrequest)
    TextView listrequest;

    List<Request> requests;
    List<Product> requested_product;
    List<Product> products;

    ReportRequestAdapter requestAdapter;
    ReportSuppliesAdapter supplyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        ButterKnife.bind(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Reports");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner_layout,
                getResources().getStringArray(R.array.months));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthsList.setAdapter(adapter);
        products = new ArrayList<>();
        requested_product = new ArrayList<>();
        requests = new ArrayList<>();

        requestAdapter = new ReportRequestAdapter(this, requests, requested_product);
        rvrequest.setAdapter(requestAdapter);
        rvrequest.setHasFixedSize(true);
        rvrequest.setLayoutManager(new LinearLayoutManager(this));

        supplyAdapter = new ReportSuppliesAdapter(this, products);
        rvsupply.setAdapter(supplyAdapter);
        rvsupply.setHasFixedSize(true);
        rvsupply.setLayoutManager(new LinearLayoutManager(this));

        monthsList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    listrequest.setVisibility(View.VISIBLE);
                    listsupply.setVisibility(View.VISIBLE);
                    no_month.setVisibility(View.GONE);
                    String month = monthsList.getSelectedItem().toString();
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

                    new RequestsbyMonthAsync(ReportsActivity.this).execute(monthNumber);
                } else {
                    listrequest.setVisibility(View.GONE);
                    listsupply.setVisibility(View.GONE);
                    no_month.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    static class RequestsbyMonthAsync extends AsyncTask<String, Void, List<Request>> {
        WeakReference<ReportsActivity> activity;

        RequestsbyMonthAsync(ReportsActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            activity.get().progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected List<Request> doInBackground(String... params) {
            List<Request> req = InventoryDatabase.getInstance(activity.get().getApplicationContext())
                    .requestDao().getApprovedRequestByMonth(params[0]);

            activity.get().requested_product.clear();

            for (int x = 0; x < req.size(); x++) {
                activity.get().requested_product.add(InventoryDatabase.getInstance
                        (activity.get().getApplicationContext()).productsDao().
                        getProduct(req.get(x).getIdProduct())); //get the requested product
            }

            activity.get().products.clear();
            activity.get().products.addAll(InventoryDatabase.getInstance(activity.get().getApplicationContext()).productsDao()
                    .getAllProductsByMonth(params[0]));

            return req;
        }

        @Override
        protected void onPostExecute(List<Request> requests) {
            ReportsActivity ra = activity.get();
            ra.progressBar.setVisibility(View.GONE);
            ra.requests.clear();
            ra.requests.addAll(requests);
            ra.requestAdapter.notifyDataSetChanged();
            ra.supplyAdapter.notifyDataSetChanged();

            super.onPostExecute(requests);
        }
    }

}