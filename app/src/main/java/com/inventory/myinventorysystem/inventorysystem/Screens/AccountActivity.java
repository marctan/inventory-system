package com.inventory.myinventorysystem.inventorysystem.Screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.inventory.myinventorysystem.inventorysystem.R;
import com.inventory.myinventorysystem.inventorysystem.SearchProvider.SearchSuggestionProvider;

public class AccountActivity extends AppCompatActivity {

    @BindView(R.id.firstname)
    TextView firstname;

    @BindView(R.id.lastname)
    TextView lastname;

    @BindView(R.id.username)
    TextView username;

    @BindView(R.id.type)
    TextView type;

    @BindView(R.id.avatar)
    ImageView avatar;

    @BindView(R.id.logout)
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        ButterKnife.bind(this);
        firstname.setText(getIntent().getStringExtra("firstname"));
        lastname.setText(getIntent().getStringExtra("lastname"));
        username.setText(getIntent().getStringExtra("username"));
        type.setText(MainActivity.isAdmin ? "Admin" : "User");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Account");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutMessage();
            }
        });

        avatar.setImageDrawable(MainActivity.isAdmin ? getDrawable(R.drawable.admin_avatar) : getDrawable(R.drawable.user_avatar));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
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
                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(AccountActivity.this,
                        SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
                suggestions.clearHistory();
                finish();
            }
        });
        builder.show();
    }
}
