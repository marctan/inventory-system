package com.inventory.myinventorysystem.inventorysystem.Screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.inventory.myinventorysystem.inventorysystem.R;
import com.inventory.myinventorysystem.inventorysystem.database.InventoryDatabase;
import com.inventory.myinventorysystem.inventorysystem.database.User;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.edMaterialUsername)
    TextInputEditText username;
    @BindView(R.id.edMaterialPassword)
    TextInputEditText password;
    @BindView(R.id.btnLogin)
    Button login;
    @BindView(R.id.btnRegister)
    Button register;

    static int userID = 0;
    static boolean isAdmin = false;
    static String loggedInName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Login");
        setSupportActionBar(myToolbar);

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Logging in");
        alertDialog.setMessage("Please wait..");
        alertDialog.setCancelable(false);

        ButterKnife.bind(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().toString().length() == 0 || password.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Some fields are empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                new queryDBLogin(MainActivity.this, alertDialog, getApplicationContext(),
                        username.getText().toString(), password.getText().toString()).execute();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });
    }

    private static class queryDBLogin extends AsyncTask<Void, Void, User> {
        WeakReference<Context> ctx;
        String username, password;
        AlertDialog dialog;
        WeakReference<Activity> act;

        queryDBLogin(Activity act, AlertDialog dialog, Context ctx, String username, String password) {
            this.ctx = new WeakReference<>(ctx);
            this.username = username;
            this.password = password;
            this.dialog = dialog;
            this.act = new WeakReference<>(act);
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected User doInBackground(Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return InventoryDatabase.getInstance(ctx.get()).userDao().getUser(username, password);
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            dialog.dismiss();

            if (user == null) {
                Toast.makeText(ctx.get(), "Incorrect password or username!", Toast.LENGTH_SHORT).show();
            } else {
                userID = user.getId();
                isAdmin = user.isAdmin();
                loggedInName = user.getFirstname() + " " + user.getLastname();
                Intent i = new Intent(act.get(), HomePage.class);
                i.putExtra("firstname", user.getFirstname());
                i.putExtra("lastname", user.getLastname());
                i.putExtra("username", user.getUsername());
                i.putExtra("admin",user.isAdmin());
                act.get().startActivity(i);
                act.get().finish();
            }
        }
    }
}
