package com.inventory.myinventorysystem.inventorysystem.Screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.inventory.myinventorysystem.inventorysystem.R;
import com.inventory.myinventorysystem.inventorysystem.database.Product;
import com.inventory.myinventorysystem.inventorysystem.database.Request;
import com.inventory.myinventorysystem.inventorysystem.databinding.ActivityRequestDetailBinding;
import com.inventory.myinventorysystem.inventorysystem.viewmodel.ProductViewModel;
import com.inventory.myinventorysystem.inventorysystem.viewmodel.RequestViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RequestDetail extends AppCompatActivity {

    ProductViewModel productViewModel;
    RequestViewModel requestViewModel;
    ActivityRequestDetailBinding binding;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_request_detail);

        int product_id = getIntent().getIntExtra("product_id", 0);
        int request_id = getIntent().getIntExtra("request_id", 0);

        Toolbar myToolbar = binding.myToolbar;
        myToolbar.setTitle("Request Details");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(ProductViewModel.class);
        requestViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(RequestViewModel.class);

        productViewModel.getProductById(product_id).observe(this, new Observer<Product>() {
            @Override
            public void onChanged(Product product) {
                if (product.getImageURI() != null) {
                    binding.productImage.setImageURI(Uri.parse(product.getImageURI()));
                } else {
                    binding.productImage.setImageDrawable(getResources().getDrawable(R.drawable.photo_placeholder_icon_250));
                }
            }
        });

        requestViewModel.getRequest(request_id).observe(this, new Observer<Request>() {
            @Override
            public void onChanged(Request requestList) {
                String status = "Unknown";
                binding.setRequest(requestList);

                switch (requestList.getStatus()) {
                    case 0:
                        status = "PENDING";
                        break;
                    case 1:
                        status = "APPROVED";
                        break;
                    case 2:
                        status = "DENIED";
                        break;
                    case 3:
                        status = "CANCELLED";
                        break;
                }
                binding.setStatus(status);
            }
        });

        binding.setAdmin(MainActivity.isAdmin);

        if (MainActivity.isAdmin) {
            requestViewModel.getApproved().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if (aBoolean) {
                        productViewModel.updateQuantity(product_id, Integer.parseInt(binding.txtQty.getText().toString()));
                    } else {
                        Toast.makeText(RequestDetail.this, "Request Denied!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });

            productViewModel.getUpdated().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if (aBoolean) {
                        Toast.makeText(RequestDetail.this, "Request Approved!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });

            binding.btnApprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String formatedDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                    requestViewModel.approveRequest(formatedDate, MainActivity.userID, true, request_id, 1);
                }
            });

            binding.btnDeny.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String formatedDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                    requestViewModel.approveRequest(formatedDate, MainActivity.userID, false, request_id, 2);
                }
            });
        } else {
            requestViewModel.getCanceled().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if (aBoolean) {
                        Toast.makeText(RequestDetail.this, "Request Cancelled!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });

            binding.btnApprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestViewModel.cancelRequest(request_id);
                }
            });

        }
    }
}
