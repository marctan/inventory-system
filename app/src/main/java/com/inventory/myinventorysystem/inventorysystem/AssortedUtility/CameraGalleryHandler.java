package com.inventory.myinventorysystem.inventorysystem.AssortedUtility;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.inventory.myinventorysystem.inventorysystem.BuildConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

public class CameraGalleryHandler {

    public static final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2;
    public static final int PERMISSION_ALL = 111;
    public static final int PERMISSION_GALLERY = 222;

    private static String cameraPath;

    private static String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };

    public static String getCameraPath(){
        return cameraPath;
    }

    private static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
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

    public static String getRealPathFromURI(Context ctx, Uri contentUri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        // Get the cursor
        Cursor cursor = ctx.getContentResolver().query(contentUri, filePathColumn, null, null, null);
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

    public static File createImageFile() throws IOException {
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
        return image;

    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void selectImage(Context ctx) {
        final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Choose option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    dialog.dismiss();
                    //request for file and camera permission;
                    if (!hasPermissions(ctx, PERMISSIONS)) {
                        ActivityCompat.requestPermissions(((Activity) ctx), PERMISSIONS, PERMISSION_ALL);
                    } else {
                        //permission granted for camera and file
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File file = null;
                        try {
                            file = createImageFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if(file != null) {
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(ctx, BuildConfig.APPLICATION_ID + ".provider", file));
                            cameraPath = "file://" + file.getAbsolutePath();
                            ((Activity) ctx).startActivityForResult(intent, PICK_IMAGE_CAMERA);
                        }
                    }

                } else if (options[item].equals("Choose From Gallery")) {
                    dialog.dismiss();

                    if (!hasPermissions(ctx, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        ActivityCompat.requestPermissions(((Activity) ctx), new String[]
                                {android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_GALLERY);
                    } else {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        ((Activity) ctx).startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                    }
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public static Bitmap cameraImage(Activity act, String pathname) {
        Bitmap bitmap = BitmapFactory.decodeFile(pathname);

        return CameraGalleryHandler.getResizedBitmap(bitmap, 1000);
    }

    public static Bitmap galleryImage(Activity act, Uri selectedImage) {
        Bitmap bitmap = BitmapFactory.decodeFile(getRealPathFromURI(act, selectedImage).replace("file://", ""));

        return CameraGalleryHandler.getResizedBitmap(bitmap, 1000);
    }
}
