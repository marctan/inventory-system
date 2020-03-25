package com.inventory.myinventorysystem.inventorysystem.Screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.inventory.myinventorysystem.inventorysystem.AssortedUtility.CameraGalleryHandler;
import com.inventory.myinventorysystem.inventorysystem.BuildConfig;
import com.inventory.myinventorysystem.inventorysystem.R;
import com.inventory.myinventorysystem.inventorysystem.database.Product;
import com.inventory.myinventorysystem.inventorysystem.database.Request;
import com.inventory.myinventorysystem.inventorysystem.databinding.ActivityProductDetailBinding;
import com.inventory.myinventorysystem.inventorysystem.viewmodel.ProductViewModel;
import com.inventory.myinventorysystem.inventorysystem.viewmodel.RequestViewModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProductDetail extends AppCompatActivity {

    Product product;

    String image_uri = null;
    String cameraFilePath = null;

    ProductViewModel productViewModel;
    RequestViewModel requestViewModel;
    ActivityProductDetailBinding binding;

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

                binding.productImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == CameraGalleryHandler.PICK_IMAGE_GALLERY) {
            if (data != null) {
                Uri selectedImage = data.getData();
                Bitmap bitmap = CameraGalleryHandler.galleryImage(ProductDetail.this, selectedImage);

                Uri imageURI = CameraGalleryHandler.getImageUri(ProductDetail.this, bitmap);
                image_uri = CameraGalleryHandler.getRealPathFromURI(ProductDetail.this, imageURI);
                binding.productImage.setImageBitmap(bitmap);
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_detail);

        binding.setIsAdmin(MainActivity.isAdmin);

        Toolbar myToolbar = binding.myToolbar;
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        requestViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(RequestViewModel.class);
        productViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(ProductViewModel.class);

        int product_id = getIntent().getIntExtra("product_id", 0);
        image_uri = getIntent().getStringExtra("imageURI");

        productViewModel.getProductById(product_id).observe(this, new Observer<Product>() {
            @Override
            public void onChanged(Product singleProduct) {
                binding.setProduct(singleProduct);
                binding.executePendingBindings();
                product = singleProduct;
                if (singleProduct.getImageURI() != null) {
                    binding.productImage.setImageURI(Uri.parse(singleProduct.getImageURI()));
                } else {
                    binding.productImage.setImageDrawable(MainActivity.isAdmin ? getResources().getDrawable(
                            R.drawable.add_photo_250) :
                            getResources().getDrawable(R.drawable.photo_placeholder_icon_250));
                }
            }
        });

        productViewModel.getUpdateStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Toast.makeText(ProductDetail.this, "Successfully updated product!", Toast.LENGTH_SHORT).show();
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });

        requestViewModel.getInsertRequestStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Toast.makeText(ProductDetail.this, "Successfully requested product!",
                            Toast.LENGTH_SHORT).show();
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });

        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.isAdmin) {
                    if (binding.productQty.getText().toString().length() == 0 || binding.productName.getText().toString().length() == 0 ||
                            binding.productDescription.getText().toString().length() == 0) {
                        Toast.makeText(ProductDetail.this, "Some fields are missing!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (binding.productQty.getText().toString().length() > 0 && binding.productQty.getText().toString().equals("0")) {
                        Toast.makeText(ProductDetail.this, "Input a valid quantity!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Product productToUpdate = new Product(product_id, binding.productDescription.getText().toString(),
                            binding.productName.getText().toString(),
                            image_uri, product.getDateAdded(), Integer.parseInt(binding.productQty.getText().toString()));

                    productViewModel.update(productToUpdate);
                } else {
                    View view = getLayoutInflater().inflate(R.layout.quantity_alert_layout, null);
                    EditText edField = view.findViewById(R.id.edQty);
                    MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(ProductDetail.this)
                            .setView(view)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if ((edField.getText().toString().length() > 0 && edField.getText().toString().equals("0")) ||
                                            edField.getText().toString().length() == 0 || Integer.parseInt(edField.getText().toString()) >
                                            Integer.parseInt(binding.productQty.getText().toString())) {
                                        Toast.makeText(ProductDetail.this, "Input a valid quantity!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        String formatedDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                                        Request request = new Request(0, product_id, MainActivity.userID, product.getName(),
                                                MainActivity.loggedInName, Integer.parseInt(edField.getText().toString()),
                                                formatedDate, null, 0, false, 0);
                                        requestViewModel.insert(request);
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
            binding.productImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CameraGalleryHandler.selectImage(ProductDetail.this);
                }
            });
        }
        binding.executePendingBindings();
    }
}
