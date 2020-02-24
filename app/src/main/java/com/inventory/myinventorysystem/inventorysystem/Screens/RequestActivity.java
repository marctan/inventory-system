package com.inventory.myinventorysystem.inventorysystem.Screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.inventory.myinventorysystem.inventorysystem.R;
import com.inventory.myinventorysystem.inventorysystem.Adapters.RequestAdapter;
import com.inventory.myinventorysystem.inventorysystem.database.InventoryDatabase;
import com.inventory.myinventorysystem.inventorysystem.database.Product;
import com.inventory.myinventorysystem.inventorysystem.database.Request;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class RequestActivity extends AppCompatActivity {

    @BindView(R.id.rvRequest)

    RecyclerView rv;
    RequestAdapter adapter;
    List<Request> requests;
    List<Product> requested_products;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.no_request)
    TextView no_request;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        ButterKnife.bind(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Requests");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        requests = new ArrayList<>();
        requested_products = new ArrayList<>();
        adapter = new RequestAdapter(this, requests, requested_products);
        rv.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new AllRequestsAsync(this).execute();
    }

    static class AllRequestsAsync extends AsyncTask<Void, Void, List<Request>> {
        WeakReference<RequestActivity> activity;

        AllRequestsAsync(RequestActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            activity.get().progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected List<Request> doInBackground(Void... voids) {
            List<Request> req;
            if(MainActivity.isAdmin) {
                req = InventoryDatabase.getInstance(activity.get().getApplicationContext())
                        .requestDao().getAllRequests();
            } else {
                req = InventoryDatabase.getInstance(activity.get().getApplicationContext())
                        .requestDao().getAllRequestsByRequestor(MainActivity.userID);
            }

            activity.get().requested_products.clear();

            for (int x = 0; x < req.size(); x++) {
                activity.get().requested_products.add(InventoryDatabase.getInstance
                        (activity.get().getApplicationContext()).productsDao().
                        getProduct(req.get(x).getIdProduct())); //get the requested product
            }

            return req;
        }

        @Override
        protected void onPostExecute(List<Request> requests) {
            RequestActivity sa = activity.get();
            sa.progressBar.setVisibility(View.GONE);
            sa.requests.clear();
            sa.requests.addAll(requests);
            sa.adapter.notifyDataSetChanged();
            if (requests.size() == 0) {
                sa.no_request.setVisibility(View.VISIBLE);
            } else {
                sa.no_request.setVisibility(View.GONE);
            }
            super.onPostExecute(requests);
        }
    }
}
