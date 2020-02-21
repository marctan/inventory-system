package com.example.marcqtan.inventorysystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class HomePage extends AppCompatActivity {

    @BindView(R.id.image_supply)
    ImageView image_supply;
    @BindView(R.id.image_request)
    ImageView image_request;
    @BindView(R.id.image_report)
    ImageView image_report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        ButterKnife.bind(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Dashboard");
        setSupportActionBar(myToolbar);
        int user_id = getIntent().getIntExtra("user_id", 0);
        boolean isAdmin = getIntent().getBooleanExtra("admin", false);

        image_supply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePage.this, SupplyActivity.class);
                startActivity(i);
            }
        });

        image_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePage.this, RequestActivity.class);
                startActivity(i);
            }
        });

        image_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePage.this, ReportsActivity.class);
                startActivity(i);
            }
        });
    }
}
