package com.inventory.myinventorysystem.inventorysystem.Screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.os.Bundle;
import android.widget.TextView;

import com.inventory.myinventorysystem.inventorysystem.R;

public class AccountActivity extends AppCompatActivity {

    @BindView(R.id.txtFirstName)
    TextView firstname;

    @BindView(R.id.txtLastName)
    TextView lastname;

    @BindView(R.id.txtUserName)
    TextView username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        ButterKnife.bind(this);
        firstname.setText(getIntent().getStringExtra("firstname"));
        lastname.setText(getIntent().getStringExtra("lastname"));
        username.setText(getIntent().getStringExtra("username"));

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Account");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
