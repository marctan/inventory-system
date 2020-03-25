package com.inventory.myinventorysystem.inventorysystem.Screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.inventory.myinventorysystem.inventorysystem.AssortedUtility.CameraGalleryHandler;
import com.inventory.myinventorysystem.inventorysystem.BuildConfig;
import com.inventory.myinventorysystem.inventorysystem.database.Product;
import com.inventory.myinventorysystem.inventorysystem.databinding.ActivityAddProductBinding;
import com.inventory.myinventorysystem.inventorysystem.viewmodel.ProductViewModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddProductActivity extends AppCompatActivity {

    String destination = null;
    String cameraFilePath = null;

    ProductViewModel productViewModel;

    ActivityAddProductBinding binding;

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
                Bitmap bitmap = CameraGalleryHandler.cameraImage(AddProductActivity.this, cameraFilePath.replace("file://", ""));
                Uri imageURI = CameraGalleryHandler.getImageUri(AddProductActivity.this, bitmap);
                destination = CameraGalleryHandler.getRealPathFromURI(AddProductActivity.this, imageURI);

                binding.productImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == CameraGalleryHandler.PICK_IMAGE_GALLERY) {
            if (data != null) {
                Uri selectedImage = data.getData();
                Bitmap bitmap = CameraGalleryHandler.galleryImage(AddProductActivity.this, selectedImage);

                Uri imageURI = CameraGalleryHandler.getImageUri(AddProductActivity.this, bitmap);
                destination = CameraGalleryHandler.getRealPathFromURI(AddProductActivity.this, imageURI);
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
        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar myToolbar = binding.myToolbar;
        myToolbar.setTitle("Add a Product");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(ProductViewModel.class);
        productViewModel.getInsertStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(AddProductActivity.this, "Product successfully added to DB!", Toast.LENGTH_SHORT).show();
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.productDesc.getText().toString().length() == 0 ||
                        binding.productName.getText().toString().length() == 0 ||
                        binding.productQty.getText().toString().length() == 0) {
                    Toast.makeText(AddProductActivity.this, "Some fields are missing!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (binding.productQty.getText().toString().length() > 0 && binding.productQty.getText().toString().equals("0")) {
                    Toast.makeText(AddProductActivity.this, "Input a valid quantity!", Toast.LENGTH_SHORT).show();
                    return;
                }

                binding.progressBar.setVisibility(View.VISIBLE);
                String formatedDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                Product product = new Product(0, binding.productDesc.getText().toString(),
                        binding.productName.getText().toString(), destination, formatedDate,
                        Integer.parseInt(binding.productQty.getText().toString()));

                productViewModel.insert(product);
            }
        });
        binding.productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraGalleryHandler.selectImage(AddProductActivity.this);

            }
        });
    }
}