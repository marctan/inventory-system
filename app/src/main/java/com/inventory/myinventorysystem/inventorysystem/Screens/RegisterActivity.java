package com.inventory.myinventorysystem.inventorysystem.Screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.inventory.myinventorysystem.inventorysystem.database.User;
import com.inventory.myinventorysystem.inventorysystem.databinding.ActivityRegisterBinding;
import com.inventory.myinventorysystem.inventorysystem.viewmodel.UserViewModel;

public class RegisterActivity extends AppCompatActivity {

    UserViewModel viewModel;
    ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar myToolbar = binding.myToolbar;
        myToolbar.setTitle("Register");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(UserViewModel.class);

        viewModel.getInsertUserStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Successfully created account!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        binding.btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.TedFirstName.length() == 0 || binding.TedLastName.length() == 0 || binding.TedAccountUsername.length() == 0 || binding.TedAccountPassword.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Some fields are empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                binding.progressBar.setVisibility(View.VISIBLE);
                User user = new User(0, binding.TedAccountUsername.getText().toString(), binding.TedAccountPassword.getText().toString(),
                        binding.TedFirstName.getText().toString(), binding.TedLastName.getText().toString(), binding.checkBoxAdmin.isChecked());
                viewModel.insert(user);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
