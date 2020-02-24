package com.example.marcqtan.inventorysystem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marcqtan.inventorysystem.database.InventoryDatabase;
import com.example.marcqtan.inventorysystem.database.Product;
import com.example.marcqtan.inventorysystem.database.Request;
import com.google.android.material.textfield.TextInputLayout;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProductDetail extends AppCompatActivity {

    Product product;
    @BindView(R.id.productImage)
    ImageView productImage;
    @BindView(R.id.productQty)
    EditText qty;
    @BindView(R.id.productName)
    EditText productName;
    @BindView(R.id.productDescription)
    EditText productDesc;
    @BindView(R.id.dateAdded)
    TextView dateAdded;
    @BindView(R.id.btnUpdate)
    Button update;
    EditText edField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        ButterKnife.bind(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Edit Product");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productName.setFocusable(MainActivity.isAdmin);
        productDesc.setFocusable(MainActivity.isAdmin);
        qty.setFocusable(MainActivity.isAdmin);
        update.setText(MainActivity.isAdmin ? "SAVE CHANGES" : "REQUEST PRODUCT");

        int product_id = getIntent().getIntExtra("product_id", 0);
        new GetProduct(this, product_id).execute();
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.isAdmin) {
                    if (qty.getText().toString().length() == 0 || productName.getText().toString().length() == 0 ||
                            productDesc.getText().toString().length() == 0) {
                        Toast.makeText(ProductDetail.this, "Some fields are missing!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (qty.getText().toString().length() > 0 && qty.getText().toString().equals("0")) {
                        Toast.makeText(ProductDetail.this, "Input a valid quantity!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Product productToUpdate = new Product(product_id, productDesc.getText().toString(), productName.getText().toString(),
                            "testURI", product.getDateAdded(), Integer.parseInt(qty.getText().toString()));

                    new UpdateProduct(ProductDetail.this, productToUpdate).execute();
                } else {
                    TextInputLayout iL = new TextInputLayout(ProductDetail.this);
                    iL.setPadding(
                            ProductDetail.this.getResources().getDimensionPixelOffset(R.dimen.dp_19),
                            ProductDetail.this.getResources().getDimensionPixelOffset(R.dimen.dp_19),
                            ProductDetail.this.getResources().getDimensionPixelOffset(R.dimen.dp_19),
                            0
                    );
                    iL.setHint("Enter quantity:");
                    edField = new EditText(ProductDetail.this);
                    iL.addView(edField);
                    edField.setInputType(InputType.TYPE_CLASS_NUMBER);
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ProductDetail.this)
                            .setView(iL)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new AddRequest(ProductDetail.this, product_id,
                                            Integer.parseInt(edField.getText().toString())).execute();
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    dialog.show();
                }
            }
        });
    }

    private static class GetProduct extends AsyncTask<Void, Void, Void> {
        WeakReference<ProductDetail> act;
        int id;

        GetProduct(ProductDetail act, int product_id) {
            this.act = new WeakReference<>(act);
            id = product_id;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            act.get().product = InventoryDatabase.getInstance(act.get().getApplicationContext()).productsDao().getProduct(id);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ProductDetail activity = act.get();
            activity.productName.setText(activity.product.getName());
            activity.productDesc.setText(activity.product.getDescription());
            activity.qty.setText(String.valueOf(activity.product.getQuantity()));
            activity.dateAdded.setText(activity.product.getDateAdded());
            if(activity.product.getImageURI() != null) {
                activity.productImage.setImageURI(Uri.parse(activity.product.getImageURI()));
            }
            super.onPostExecute(aVoid);
        }
    }


    private static class UpdateProduct extends AsyncTask<Void, Void, Void> {
        WeakReference<ProductDetail> act;
        Product product;

        UpdateProduct(ProductDetail act, Product product) {
            this.act = new WeakReference<>(act);
            this.product = product;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            InventoryDatabase.getInstance(act.get().getApplicationContext()).productsDao().updateProduct(product);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(act.get().getApplicationContext(), "Successfully updated product!", Toast.LENGTH_SHORT).show();
            Intent returnIntent = new Intent();
            act.get().setResult(Activity.RESULT_OK, returnIntent);
            act.get().finish();
            super.onPostExecute(aVoid);
        }
    }

    private static class AddRequest extends AsyncTask<Void, Void, Void> {
        WeakReference<ProductDetail> act;
        int product_id;
        int quantity;

        AddRequest(ProductDetail act, int product_id, int quantity) {
            this.act = new WeakReference<>(act);
            this.product_id = product_id;
            this.quantity = quantity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String formatedDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            Product product = act.get().product;
            Request request = new Request(0, product_id, MainActivity.userID, product.getName(),
                    MainActivity.loggedInName, quantity, formatedDate, null, 0, false, 0);
            InventoryDatabase.getInstance(act.get().getApplicationContext()).requestDao().insertRequest(request);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(act.get().getApplicationContext(), "Successfully requested product!", Toast.LENGTH_SHORT).show();
            Intent returnIntent = new Intent();
            act.get().setResult(Activity.RESULT_OK, returnIntent);
            act.get().finish();
            super.onPostExecute(aVoid);
        }
    }
}
