package com.example.marcqtan.inventorysystem.Screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.marcqtan.inventorysystem.AdapterInterface.AdapterInterface;
import com.example.marcqtan.inventorysystem.R;
import com.example.marcqtan.inventorysystem.Fragments.ReportRequestFragment;
import com.example.marcqtan.inventorysystem.Fragments.ReportSupplyFragment;
import com.example.marcqtan.inventorysystem.database.InventoryDatabase;
import com.example.marcqtan.inventorysystem.database.Product;
import com.example.marcqtan.inventorysystem.database.Request;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ReportsActivity extends AppCompatActivity {

    @BindView(R.id.spinner)
    Spinner monthsList;
    @BindView(R.id.noMonth)
    TextView no_month;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.btnReportSwitch)
    Button reportSwitch;
    @BindView(R.id.container)
    FrameLayout frmcontainer;

    List<Request> requests;
    List<Product> requested_product;
    List<Product> products;

    static int REPORT_VIEW = 0;//0 = request report 1 = supply report
    AdapterInterface adapterIface;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

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


        ReportRequestFragment frag = new ReportRequestFragment(requested_product, requests);
        adapterIface = frag;
        loadFragment(frag);
        REPORT_VIEW = 0;

        monthsList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    no_month.setVisibility(View.GONE);
                    frmcontainer.setVisibility(View.VISIBLE);
                    reportSwitch.setVisibility(View.VISIBLE);
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
                    no_month.setVisibility(View.VISIBLE);
                    frmcontainer.setVisibility(View.GONE);
                    reportSwitch.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        reportSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reportSwitch.getText().toString().equals("View Request Report")) {
                    reportSwitch.setText("View Supply Report");
                    ReportRequestFragment frag = new ReportRequestFragment(requested_product, requests);
                    adapterIface = frag;
                    loadFragment(frag);
                    REPORT_VIEW = 0;
                } else {
                    reportSwitch.setText("View Request Report");
                    ReportSupplyFragment frag = new ReportSupplyFragment(products);
                    adapterIface = frag;
                    loadFragment(frag);
                    REPORT_VIEW = 1;
                }
            }
        });
    }

    void loadFragment(Fragment frag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.addToBackStack(null);
        ft.replace(R.id.container, frag);
        ft.commit();
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

            FragmentManager fm = ra.getSupportFragmentManager();

            Fragment reportFrag = fm.findFragmentById(R.id.container);
            if (reportFrag != null && reportFrag.isAdded()) {
                ra.adapterIface.notifyAdapter();
            }
            super.onPostExecute(requests);
        }
    }

}
