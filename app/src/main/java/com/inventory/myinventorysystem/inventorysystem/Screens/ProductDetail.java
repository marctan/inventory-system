package com.inventory.myinventorysystem.inventorysystem.Screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.inventory.myinventorysystem.inventorysystem.AssortedUtility.CameraGalleryHandler;
import com.inventory.myinventorysystem.inventorysystem.BuildConfig;
import com.inventory.myinventorysystem.inventorysystem.R;
import com.inventory.myinventorysystem.inventorysystem.database.InventoryDatabase;
import com.inventory.myinventorysystem.inventorysystem.database.Product;
import com.inventory.myinventorysystem.inventorysystem.database.Request;

import java.io.File;
import java.io.IOException;
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

    String image_uri = null;
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
        } else if (requestCode == CameraGalleryHandler.PERMISSION_GALLERY) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, CameraGalleryHandler.PICK_IMAGE_GALLERY);
            } else {
                Toast.makeText(this, "Write storage permission was denied!", Toast.LENGTH_SHORT).show();
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
                Bitmap bitmap = CameraGalleryHandler.cameraImage(ProductDetail.this, cameraFilePath.replace("file://", ""));
                Uri imageURI = CameraGalleryHandler.getImageUri(ProductDetail.this, bitmap);
                image_uri = CameraGalleryHandler.getRealPathFromURI(ProductDetail.this, imageURI);

                productImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == CameraGalleryHandler.PICK_IMAGE_GALLERY) {
            if (data != null) {
                Uri selectedImage = data.getData();
                Bitmap bitmap = CameraGalleryHandler.galleryImage(ProductDetail.this, selectedImage);

                Uri imageURI = CameraGalleryHandler.getImageUri(ProductDetail.this, bitmap);
                image_uri = CameraGalleryHandler.getRealPathFromURI(ProductDetail.this, imageURI);
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
        setContentView(R.layout.activity_product_detail);
        ButterKnife.bind(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (MainActivity.isAdmin) {
            myToolbar.setTitle("Edit Product");
        } else {
            myToolbar.setTitle("Request Product");
        }
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productName.setFocusable(MainActivity.isAdmin);
        productDesc.setFocusable(MainActivity.isAdmin);
        qty.setFocusable(MainActivity.isAdmin);
        update.setText(MainActivity.isAdmin ? "SAVE CHANGES" : "REQUEST PRODUCT");

        int product_id = getIntent().getIntExtra("product_id", 0);
        image_uri = getIntent().getStringExtra("imageURI");
        new GetProduct(this, product_id).execute();
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.isAdmin) {
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
                            image_uri, product.getDateAdded(), Integer.parseInt(qty.getText().toString()));

                    new UpdateProduct(ProductDetail.this, productToUpdate).execute();
                } else {
                    View view = getLayoutInflater().inflate(R.layout.quantity_alert_layout, null);
                    EditText edField = view.findViewById(R.id.edQty);
                    MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(ProductDetail.this)
                            .setView(view)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if ((edField.getText().toString().length() > 0 && edField.getText().toString().equals("0")) ||
                                            edField.getText().toString().length() == 0) {
                                        Toast.makeText(ProductDetail.this, "Input a valid quantity!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        new AddRequest(ProductDetail.this, product_id,
                                                Integer.parseInt(edField.getText().toString())).execute();
                                        dialog.dismiss();
                                    }
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

        if (MainActivity.isAdmin) {
            productImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CameraGalleryHandler.selectImage(ProductDetail.this);
                }
            });
        }
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
            if (activity.product.getImageURI() != null) {
                activity.productImage.setImageURI(Uri.parse(activity.product.getImageURI()));
            } else {
                activity.productImage.setImageDrawable(MainActivity.isAdmin ?
                        activity.getResources().getDrawable(R.drawable.add_photo_250) :
                        activity.getResources().getDrawable(R.drawable.photo_placeholder_icon_250));
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
