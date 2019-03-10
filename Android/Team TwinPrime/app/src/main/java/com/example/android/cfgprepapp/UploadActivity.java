package com.example.android.cfgprepapp;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.android.cfgprepapp.data.MySingleton;
import com.example.android.cfgprepapp.view.cpb.CircularProgressButton;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;
import com.yalantis.ucrop.view.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.maxHeight;
import static android.R.attr.maxWidth;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener {

    private File mImageFolder;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private CircularProgressButton UploadBn, ChooseBn, CropBn;
    private EditText NAME;
    private ImageView imgView;
    Uri imgpath;
    private final int IMG_REQUEST = 1;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        UploadBn = findViewById(R.id.uploadBn);
        ChooseBn =  findViewById(R.id.chooseBn);
        CropBn =  findViewById(R.id.CropBn);
        imgView = (ImageView) findViewById(R.id.imageView);
        ChooseBn.setOnClickListener(this);
        UploadBn.setOnClickListener(this);
        CropBn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chooseBn:
                ChooseBn.setIndeterminateProgressMode(true);
                selectImage();
                break;
            case R.id.uploadBn:
                uploadImage();
                break;
            case R.id.CropBn:
                Log.e("Crop Test","Btn Success");
                cropImage();
                break;
        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMG_REQUEST);

    }

    private void cropImage()
    {
        Log.e("ImagePath",getPath(this,imgpath));
        Uri imgpathuri=Uri.fromFile(new File((getPath(this,imgpath))));
        if(checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            createImageFolder();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String prepend = "IMAGE_" + timestamp + "_";
            File imageFile = null;
            try {
                imageFile = File.createTempFile(prepend, ".jpg", mImageFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            UCrop.Options myOptions = new UCrop.Options();
            myOptions.setAspectRatioOptions(1,
                    new AspectRatio("1x2", 1, 2),
                    new AspectRatio("3x4", 3, 4),
                    new AspectRatio("Default", CropImageView.DEFAULT_ASPECT_RATIO, CropImageView.DEFAULT_ASPECT_RATIO),
                    new AspectRatio("16x9", 16, 9),
                    new AspectRatio("1x1", 1, 1));
            myOptions.setFreeStyleCropEnabled(true);
            Uri destinationUri = Uri.fromFile(imageFile);
            UCrop.of(imgpathuri, destinationUri)
                    .withOptions(myOptions)
                    .withMaxResultSize(maxWidth, maxHeight)
                    .start(UploadActivity.this);
        }
    }

    private void createImageFolder() {
        File imageFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        mImageFolder = new File(imageFile, "CFGPrepApp");
        if(!mImageFolder.exists()) {
            mImageFolder.mkdirs();
            Log.e("Folder Checked", "Yes "+mImageFolder.getPath());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Request Code",String.valueOf(requestCode));
        if (requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri path = data.getData();
            imgpath=path;
            try {
                ChooseBn.setVisibility(View.GONE);
                CropBn.setVisibility(View.VISIBLE);
                UploadBn.setVisibility(View.VISIBLE);
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                imgView.setImageBitmap(bitmap);
                for (int progress = 0; progress <= 100; progress += 5) {
                    ChooseBn.setProgress(progress);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                ChooseBn.setProgress(-1);
                e.printStackTrace();
            }
        }else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Log.e("Crop Result Test","Success");
            final Uri resultUri = UCrop.getOutput(data);
            imgView.setImageURI(resultUri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Log.e("Crop Result Test","Failed"+cropError.getMessage());
        }
    }

    private void uploadImage() {
        UploadBn.setProgress(10);
        UploadBn.setIndeterminateProgressMode(true);
        String ipadd = getResources().getString(R.string.ipadd);
        String url = "/CFGAPI/updateinfo.php";
        String headurl = "http://";
        url=headurl+ipadd+url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String Response = jsonObject.getString("response");
                            Toast.makeText(UploadActivity.this, Response, Toast.LENGTH_LONG).show();
                            for (int progress = 10; progress <= 100; progress += 5) {
                                UploadBn.setProgress(progress);
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            //imgView.setImageResource(0);
                            //imgView.setVisibility(View.GONE);
                            //NAME.setText("");
                            //NAME.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            UploadBn.setProgress(-1);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        UploadBn.setProgress(-1);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", "1");
                params.put("image", imageToString(bitmap));
                return params;
            }
        };
        MySingleton.getmInstance(UploadActivity.this).addToRequestQue(stringRequest);
    }

    private String imageToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }

    //External Read Permission Check Code
    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context, Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(UploadActivity.this, "Permission Granted",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UploadActivity.this, "Permission Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }



    //URI Problem Solving Code

    public static String getPath(Context context, Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else
            if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}