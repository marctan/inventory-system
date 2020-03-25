package com.inventory.myinventorysystem.inventorysystem.Screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.inventory.myinventorysystem.inventorysystem.database.User;
import com.inventory.myinventorysystem.inventorysystem.databinding.ActivityMainBinding;
import com.inventory.myinventorysystem.inventorysystem.viewmodel.UserViewModel;

public class MainActivity extends AppCompatActivity {

    static int userID = 0;
    static boolean isAdmin = false;
    static String loggedInName = "";

    UserViewModel userViewModel;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar myToolbar = binding.myToolbar;
        myToolbar.setTitle("Login");
        setSupportActionBar(myToolbar);

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Logging in");
        alertDialog.setMessage("Please wait..");
        alertDialog.setCancelable(false);

        userViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(UserViewModel.class);

        userViewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                alertDialog.dismiss();

                if (user == null) {
                    Toast.makeText(MainActivity.this, "Incorrect password or username!", Toast.LENGTH_SHORT).show();
                } else {
                    userID = user.getId();
                    isAdmin = user.isAdmin();
                    loggedInName = user.getFirstname() + " " + user.getLastname();
                    Intent i = new Intent(MainActivity.this, HomePage.class);
                    startActivity(i);
                    finish();
                }
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.edMaterialUsername.getText().toString().length() == 0 || binding.edMaterialPassword.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Some fields are empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                alertDialog.show();

                userViewModel.queryUser(binding.edMaterialUsername.getText().toString(), binding.edMaterialPassword.getText().toString());
            }
        });

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });
    }
}
