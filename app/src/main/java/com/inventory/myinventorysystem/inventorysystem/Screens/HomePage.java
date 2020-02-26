package com.inventory.myinventorysystem.inventorysystem.Screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.inventory.myinventorysystem.inventorysystem.R;
import com.inventory.myinventorysystem.inventorysystem.SearchProvider.SearchSuggestionProvider;

public class HomePage extends AppCompatActivity {

    @BindView(R.id.rlSupply)
    RelativeLayout image_supply;
    @BindView(R.id.rlRequests)
    RelativeLayout image_request;
    @BindView(R.id.rlReports)
    RelativeLayout image_report;
    @BindView(R.id.rlAccount)
    RelativeLayout image_account;
    @BindView(R.id.txRequest)
    TextView txtRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        ButterKnife.bind(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Dashboard");
        setSupportActionBar(myToolbar);
        String firstname = getIntent().getStringExtra("firstname");
        String lastname = getIntent().getStringExtra("lastname");
        String username = getIntent().getStringExtra("username");

        boolean isAdmin = getIntent().getBooleanExtra("admin", false);

        if(MainActivity.isAdmin) {
            txtRequest.setText("List of Requests");
            image_report.setVisibility(View.VISIBLE);
        } else {
            txtRequest.setText("My Requests");
            image_report.setVisibility(View.GONE);
        }

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

        image_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePage.this, AccountActivity.class);
                i.putExtra("firstname", firstname);
                i.putExtra("lastname", lastname);
                i.putExtra("username", username);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.logout) {
           showLogoutMessage();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showLogoutMessage();
    }

    private void showLogoutMessage(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(HomePage.this,
                        SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
                suggestions.clearHistory();
                finish();
            }
        });
        builder.show();
    }
}
