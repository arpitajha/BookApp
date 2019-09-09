package com.example.anew;

//import android.app.AlertDialog;
import android.app.ProgressDialog;

//import android.content.DialogInterface;
import android.content.Intent;
//import android.database.Cursor;
import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
import android.net.Uri;
//import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.util.Base64;
//import android.view.ContextThemeWrapper;
import android.view.View;
//import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
//import com.android.volley.*;
//import com.android.volley.Response;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
import com.google.firebase.storage.*;
import com.google.android.gms.tasks.*;
import android.support.annotation.*;

import android.widget.Toast;
//import org.json.JSONObject;
//import java.io.File;
import java.io.IOException;
import java.lang.String;
import java.util.*;


public class Upload extends AppCompatActivity implements View.OnClickListener{
    private StorageReference mStorageRef;

    public static final String Key_User_Document1="doc1";
    ImageView Image;
    Button UploadBtn;
    private String Document_img1="";
    private int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        Intent intent = new Intent();
// Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

        Image=findViewById(R.id.Image);
        UploadBtn=findViewById(R.id.UploadBtn);
        UploadBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                uploadImage();
            }
        });
//        UploadBtn.setOnClickListener(this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                Image.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = mStorageRef.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(Upload.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Upload.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

//    private void selectImage(){
//        final CharSequence[] options={"Take Photo","Choose from Gallery","Cancel"};
//        android.support.v7.app.AlertDialog.Builder builder= new android.support.v7.app.AlertDialog.Builder(Upload.this);
//        builder.setTitle("Add Photo");
//        final android.support.v7.app.AlertDialog.Builder builder1 = builder.setItems(options, new DialogInterface.OnClickListener() {
//
//
//            @Override
//            public void onClick(DialogInterface dialog, int item) {
//                if (options[item].equals("Take Photo")) {
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
//                    startActivityForResult(intent, 1);
//                } else if (options[item].equals("Choose from Gallery")) {
//                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    startActivityForResult(intent, 2);
//                } else if (options[item].equals("Cancel")) {
//                    dialog.dismiss();
//                }
//            }
//        });
//        builder.show();
//    }

//    private void sendDetail(){
//        final ProgressDialog loading=new ProgressDialog(Upload.this);
//        loading.setMessage("Please wait.");
//        loading.show();
//        loading.setCanceledOnTouchOutside(false);
//        RetryPolicy mRetryPolicy=new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        StringRequest stringRequest= new StringRequest(Request.Method.POST, "images/*",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            loading.dismiss();
//                            Log.d("JSON", response);
//                            JSONObject eventObject = new JSONObject(response);
//                            String error_status = eventObject.getString("error");
//                            if (error_status.equals("true")) {
//                                String error_msg = eventObject.getString("msg");
//                                ContextThemeWrapper ctw = new ContextThemeWrapper(Upload.this, R.style.Theme_AppCompat_Dialog_Alert);
//                                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
//                                alertDialogBuilder.setTitle("Vendor Detail");
//                                alertDialogBuilder.setCancelable(false);
//                                alertDialogBuilder.setMessage(error_msg);
//                                alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int id) {
//
//                                    }
//                                });
//                                alertDialogBuilder.show();
//
//                            } else {
//                                String error_msg = eventObject.getString("msg");
//                                ContextThemeWrapper ctw = new ContextThemeWrapper(Upload.this, R.style.Theme_AppCompat_Dialog_Alert);
//                                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
//                                alertDialogBuilder.setTitle("Registration");
//                                alertDialogBuilder.setCancelable(false);
//                                alertDialogBuilder.setMessage(error_msg);
//                                alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        Intent intent = new Intent(Upload.this, Sell.class);//add next class
//                                        startActivity(intent);
//                                        finish();
//                                    }
//                                });
//                                alertDialogBuilder.show();
//                            }
//                        } catch (Exception e) {
//                            Log.d("Tag", e.getMessage());
//                        }
//                    }
//                },
//                new Response.ErrorListener(){
//
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        loading.dismiss();
//                        if(error instanceof TimeoutError|| error instanceof NoConnectionError){
//                            ContextThemeWrapper ctw=new ContextThemeWrapper(Upload.this, R.style.Theme_AppCompat_Dialog_Alert);
//                            final AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(ctw);
//                            alertDialogBuilder.setTitle("No Connection");
//                            alertDialogBuilder.setMessage("Connection time out. Please try again later.");
//                            alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int id) {
//
//                                }
//                            });
//                            alertDialogBuilder.show();
//                        }else if(error instanceof AuthFailureError){
//                            ContextThemeWrapper ctw=new ContextThemeWrapper();
//                            final AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(ctw);
//                            alertDialogBuilder.setTitle("Connection Error");
//                            alertDialogBuilder.setMessage("Authentication Failure connection error please try again.");
//                            alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int id) {
//
//                                }
//                            });
//                            alertDialogBuilder.show();
//                        }else if(error instanceof ServerError){
//                            ContextThemeWrapper ctw=new ContextThemeWrapper();
//                            final AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(ctw);
//                            alertDialogBuilder.setTitle("Connection Error");
//                            alertDialogBuilder.setMessage("Connection error please try again.");
//                            alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int id) {
//
//                                }
//                            });
//                            alertDialogBuilder.show();
//                        }else if(error instanceof ParseError){
//                            ContextThemeWrapper ctw=new ContextThemeWrapper();
//                            final AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(ctw);
//                            alertDialogBuilder.setTitle("Error");
//                            alertDialogBuilder.setMessage("Parse Error");
//                            alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int id) {
//
//                                }
//                            });
//                            alertDialogBuilder.show();
//                        }
//                    }
//                }){
//
//            protected Map<String,String> getparams() throws AuthFailureError{
//                Map<String,String> map=new HashMap<String,String>();
//                map.put(Key_User_Document1,Document_img1);
//                return map;
//            }
//        };
//        RequestQueue requestQueue= Volley.newRequestQueue(this);
//        stringRequest.setRetryPolicy(mRetryPolicy);
//        requestQueue.add(stringRequest);
//    }
    @Override
    public void onClick(View v){

    }

}
