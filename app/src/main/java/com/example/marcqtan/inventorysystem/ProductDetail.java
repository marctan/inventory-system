package com.example.marcqtan.inventorysystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marcqtan.inventorysystem.database.InventoryDatabase;
import com.example.marcqtan.inventorysystem.database.Product;

import java.lang.ref.WeakReference;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        ButterKnife.bind(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Edit Product");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int product_id = getIntent().getIntExtra("product_id", 0);
        new GetProduct(this, product_id).execute();
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qty.getText().toString().length() == 0 || productName.getText().toString().length() == 0 ||
                        productDesc.getText().toString().length() == 0) {
                    Toast.makeText(ProductDetail.this, "Some fields are missing!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(qty.getText().toString().length() > 0 && qty.getText().toString().equals("0")) {
                    Toast.makeText(ProductDetail.this, "Input a valid quantity!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Product productToUpdate = new Product(product_id, productDesc.getText().toString(), productName.getText().toString(),
                        "testURI", product.getDateAdded(), Integer.parseInt(qty.getText().toString()));

                new UpdateProduct(ProductDetail.this, productToUpdate).execute();
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
            act.get().finish();
            super.onPostExecute(aVoid);
        }
    }
}
