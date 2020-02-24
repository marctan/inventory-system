package com.example.marcqtan.inventorysystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marcqtan.inventorysystem.database.InventoryDatabase;
import com.example.marcqtan.inventorysystem.database.Product;
import com.example.marcqtan.inventorysystem.database.Request;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2;
    private final int PERMISSION_ALL = 111;
    String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };

    String destination = null;
    String cameraFilePath = null;

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED //0 camera 1 file
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, PICK_IMAGE_CAMERA);
                } else {
                    Toast.makeText(this, "Camera or write storage permission was denied!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_CAMERA) {
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(cameraFilePath.replace("file://", ""));
                Bitmap bitmap2 = getResizedBitmap(bitmap, 1000);//1mb size image

                Uri imageURI = getImageUri(this, bitmap2);
                destination = getRealPathFromURI(imageURI);
                productImage.setImageBitmap(bitmap2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_IMAGE_GALLERY) {
            Uri selectedImage = data.getData();

            Bitmap bitmap = BitmapFactory.decodeFile(getRealPathFromURI(selectedImage).replace("file://", ""));
            Bitmap bitmap2 = getResizedBitmap(bitmap, 1000);//1mb size image

            Uri imageURI = getImageUri(this, bitmap2);
            destination = getRealPathFromURI(imageURI);
            productImage.setImageBitmap(bitmap2);
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        // Get the cursor
        Cursor cursor = getContentResolver().query(contentUri, filePathColumn, null, null, null);
        // Move to first row
        cursor.moveToFirst();
        //Get the column index of MediaStore.Images.Media.DATA
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        //Gets the String value in the column
        String path = cursor.getString(columnIndex);
        cursor.close();
        return path;
    }

    public static Uri getImageUri(Activity inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //This is the directory in which the file will be created. This is the default location of Camera photos
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for using again
        cameraFilePath = "file://" + image.getAbsolutePath();
        return image;

    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    dialog.dismiss();

                    //request for file and camera permission;
                    if (!hasPermissions(AddProductActivity.this, PERMISSIONS)) {
                        ActivityCompat.requestPermissions(AddProductActivity.this, PERMISSIONS, PERMISSION_ALL);
                    } else {
                        //permission granted for camera and file
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        try {
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(AddProductActivity.this, BuildConfig.APPLICATION_ID + ".provider", createImageFile()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        startActivityForResult(intent, PICK_IMAGE_CAMERA);
                    }

                } else if (options[item].equals("Choose From Gallery")) {
                    dialog.dismiss();
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
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

                if(qty.getText().toString().length() > 0 && qty.getText().toString().equals("0")) {
                    Toast.makeText(AddProductActivity.this, "Input a valid quantity!", Toast.LENGTH_SHORT).show();
                    return;
                }

                new AddProductToDB(AddProductActivity.this).execute();
            }
        });
        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
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