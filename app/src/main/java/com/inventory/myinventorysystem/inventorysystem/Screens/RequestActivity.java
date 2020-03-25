package com.inventory.myinventorysystem.inventorysystem.Screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.inventory.myinventorysystem.inventorysystem.Adapters.RequestAdapter;
import com.inventory.myinventorysystem.inventorysystem.database.Product;
import com.inventory.myinventorysystem.inventorysystem.database.Request;
import com.inventory.myinventorysystem.inventorysystem.databinding.ActivityRequestBinding;
import com.inventory.myinventorysystem.inventorysystem.viewmodel.RequestViewModel;

import java.util.ArrayList;
import java.util.List;

public class RequestActivity extends AppCompatActivity {

    RequestAdapter adapter;
    List<Request> requests;

    RequestViewModel requestViewModel;
    ActivityRequestBinding binding;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar myToolbar = binding.myToolbar;
        myToolbar.setTitle("Requests");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.rvRequest.setHasFixedSize(true);
        binding.rvRequest.setLayoutManager(new LinearLayoutManager(this));
        requests = new ArrayList<>();
        adapter = new RequestAdapter(this);
        binding.rvRequest.setAdapter(adapter);

        requestViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(RequestViewModel.class);
        binding.progressBar.setVisibility(View.VISIBLE);

        requestViewModel.getAllRequests(MainActivity.isAdmin ? -1 : MainActivity.userID).observe(this, new Observer<List<Request>>() {
            @Override
            public void onChanged(List<Request> requestsProduct) {
                requests = requestsProduct;
                if (requests.size() == 0) {
                    binding.noRequest.setVisibility(View.VISIBLE);
                } else {
                    binding.noRequest.setVisibility(View.GONE);
                }
            }
        });
        requestViewModel.getProductFromRequest(false).observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                binding.progressBar.setVisibility(View.GONE);
                if (requests.size() == products.size()) {
                    adapter.setRequests(requests, products);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
