package com.inventory.myinventorysystem.inventorysystem.Screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.inventory.myinventorysystem.inventorysystem.AssortedUtility.CameraGalleryHandler;
import com.inventory.myinventorysystem.inventorysystem.BuildConfig;
import com.inventory.myinventorysystem.inventorysystem.R;
import com.inventory.myinventorysystem.inventorysystem.database.InventoryDatabase;
import com.inventory.myinventorysystem.inventorysystem.database.Product;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddProductActivity extends AppCompatActivity {
    @BindView(R.id.productDesc)
    EditText productDesc;
    @BindView(R.id.productName)
    EditText productName;
    @BindView(R.id.productImage)
    ImageView productImage;
    @BindView(R.id.productQty)
    EditText qty;
    @BindView(R.id.btnAdd)
    Button btnAdd;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    String destination = null;
    String cameraFilePath = null;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CameraGalleryHandler.PERMISSION_ALL) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED //0 camera 1 file
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = null;
                try {
                    file = CameraGalleryHandler.createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (file != null) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file));
                    cameraFilePath = "file://" + file.getAbsolutePath();
                    startActivityForResult(intent, CameraGalleryHandler.PICK_IMAGE_CAMERA);
                }
            } else {
                Toast.makeText(this, "Camera or write storage permission was denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CameraGalleryHandler.PICK_IMAGE_CAMERA) {
            try {
                if (cameraFilePath == null) {
                    cameraFilePath = CameraGalleryHandler.getCameraPath();
                }
                Bitmap bitmap = CameraGalleryHandler.cameraImage(AddProductActivity.this, cameraFilePath.replace("file://", ""));
                Uri imageURI = CameraGalleryHandler.getImageUri(AddProductActivity.this, bitmap);
                destination = CameraGalleryHandler.getRealPathFromURI(AddProductActivity.this, imageURI);

                productImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == CameraGalleryHandler.PICK_IMAGE_GALLERY) {
            if (data != null) {
                Uri selectedImage = data.getData();
                Bitmap bitmap = CameraGalleryHandler.galleryImage(AddProductActivity.this, selectedImage);

                Uri imageURI = CameraGalleryHandler.getImageUri(AddProductActivity.this, bitmap);
                destination = CameraGalleryHandler.getRealPathFromURI(AddProductActivity.this, imageURI);
                productImage.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        ButterKnife.bind(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Add a Product");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productDesc.getText().toString().length() == 0 ||
                        productName.getText().toString().length() == 0 ||
                        qty.getText().toString().length() == 0) {
                    Toast.makeText(AddProductActivity.this, "Some fields are missing!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (qty.getText().toString().length() > 0 && qty.getText().toString().equals("0")) {
                    Toast.makeText(AddProductActivity.this, "Input a valid quantity!", Toast.LENGTH_SHORT).show();
                    return;
                }

                new AddProductToDB(AddProductActivity.this).execute();
            }
        });
        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraGalleryHandler.selectImage(AddProductActivity.this);

            }
        });
    }

    static class AddProductToDB extends AsyncTask<Void, Void, Void> {
        WeakReference<AddProductActivity> activity;
        String description, name;
        int quantity;

        AddProductToDB(AddProductActivity activity) {
            this.activity = new WeakReference<>(activity);
            description = activity.productDesc.getText().toString();
            name = activity.productName.getText().toString();
            quantity = Integer.parseInt(activity.qty.getText().toString());
        }

        @Override
        protected void onPreExecute() {
            activity.get().progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String formatedDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            AddProductActivity act = activity.get();
            Product product = new Product(0, description,
                    name, act.destination, formatedDate, quantity);
            InventoryDatabase.getInstance(act.getApplicationContext()).productsDao().insertProduct(product);

            return null;
        }

        @Override
        protected void onPostExecute(Void products) {
            activity.get().progressBar.setVisibility(View.GONE);
            Toast.makeText(activity.get().getApplicationContext(), "Product successfully added to DB!"
                    , Toast.LENGTH_SHORT).show();
            super.onPostExecute(products);
            Intent returnIntent = new Intent();
            activity.get().setResult(Activity.RESULT_OK, returnIntent);
            activity.get().finish();
        }
    }
}