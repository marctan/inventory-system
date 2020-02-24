package com.example.marcqtan.inventorysystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.marcqtan.inventorysystem.database.InventoryDatabase;
import com.example.marcqtan.inventorysystem.database.User;

import java.lang.ref.WeakReference;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.btnCreateAccount)
    Button createAccount;

    @BindView(R.id.edFirstName)
    EditText firstname;

    @BindView(R.id.edLastName)
    EditText lastname;

    @BindView(R.id.edAccountUsername)
    EditText username;

    @BindView(R.id.edAccountPassword)
    EditText password;

    @BindView(R.id.checkBoxAdmin)
    CheckBox admin;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Register");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstname.length() == 0 || lastname.length() == 0 || username.length() == 0 || password.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Some fields are empty!",Toast.LENGTH_SHORT).show();
                    return;
                }
                new saveToDBTask(RegisterActivity.this, getApplicationContext(), username.getText().toString(),
                        password.getText().toString(), firstname.getText().toString(),
                        lastname.getText().toString(), admin.isChecked(), progressBar).execute();
            }
        });
    }


    private static class saveToDBTask extends AsyncTask<Void, Void, Void> {
        WeakReference<Context> ctx;
        String username, password, firstname, lastname;
        WeakReference<Activity> act;
        WeakReference<ProgressBar> progressBar;
        boolean admin;

        saveToDBTask(Activity act, Context ctx, String username, String password,
                     String firstname, String lastname, boolean admin, ProgressBar progressBar) {
            this.act = new WeakReference<>(act);
            this.ctx = new WeakReference<>(ctx);
            this.username = username;
            this.password = password;
            this.firstname = firstname;
            this.lastname = lastname;
            this.admin = admin;
            this.progressBar = new WeakReference<>(progressBar);
        }

        @Override
        protected void onPreExecute() {
            progressBar.get().setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            User user = new User(0, username, password,
                    firstname, lastname, admin);
            InventoryDatabase.getInstance(ctx.get()).userDao().insertUser(user);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.get().setVisibility(View.GONE);
            Toast.makeText(ctx.get(), "Successfully created account!", Toast.LENGTH_SHORT).show();
            super.onPostExecute(aVoid);
            act.get().finish();
        }
    }
}
