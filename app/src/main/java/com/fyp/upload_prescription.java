package com.fyp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class upload_prescription extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    public static final String ORDERID_KEY="orderID";
    public static final String TIME_KEY="time";
    public static final String ORDER_TYPE_KEY="type";
    public static final String UID_KEY="UID";
    public static final String FULL_NAME_KEY="Name";

    private static final String TAG = "CapturePicture";
    static final int REQUEST_PICTURE_CAPTURE = 1;
    private ImageView image;
    private String pictureFilePath;
    private FirebaseStorage firebaseStorage;
    private String deviceIdentifier;

    Button upload_button;
    Bitmap prescription_picture;
    FirebaseFirestore db;
    private boolean is_empty=true;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_prescription);
        db=FirebaseFirestore.getInstance();
        this.imageView = (ImageView)this.findViewById(R.id.imageView1);
        Button photoButton = (Button) this.findViewById(R.id.button1);
        upload_button=findViewById(R.id.submit_search);

        photoButton.setOnClickListener(new View.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v)
            {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                //    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                   // startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    sendTakePictureIntent();
                }
            }
        });
        upload_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                if (!is_empty) {
                    final String email= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
                   /* ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    prescription_picture.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] data = baos.toByteArray();*/
                    File f = new File(pictureFilePath);
                    final Uri picUri = Uri.fromFile(f);
                    final Map<String,Object> medicine_order=new HashMap<>();
                    @SuppressLint("SimpleDateFormat")
                    DateFormat df = new SimpleDateFormat("H:mm a");
                    String date = df.format(Calendar.getInstance().getTime());
                    medicine_order.put(TIME_KEY,date);
                    medicine_order.put(UID_KEY,FirebaseAuth.getInstance().getCurrentUser().getUid());
                    medicine_order.put(FULL_NAME_KEY,"ame");
                    medicine_order.put("uemail",FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    medicine_order.put(ORDER_TYPE_KEY,"Prescription upload");
                    db.collection("entityCount").document("TotalOrders")
                            .get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String s=documentSnapshot.getString("latest_order_number");
                            s=Integer.toString(Integer.parseInt(s)+1);
                            final String orderid=s;
                            medicine_order.put(ORDERID_KEY,s);
                            assert email != null;
                            db.collection("orders")
                                    .document(email)
                                    .set(medicine_order).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    GenerateNotif g=new GenerateNotif();
                                    g.sendNotificationToAllUsers();
                                    Map<String,Object> setorder=new HashMap<>();
                                    setorder.put("is_ordering",true);
                                    setorder.put("is_accepted",false);
                                    db.collection("users").document(email).set(setorder, SetOptions.merge());
                                    Log.d("tag", "Added Successfully");
                                    StorageReference storageReference=FirebaseStorage.getInstance().getReference();
                                    storageReference=storageReference.child("prescriptions/"+orderid+".jpg");
                                    storageReference.putFile(picUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Toast.makeText(upload_prescription.this,"Uploaded",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    startActivity(new Intent(upload_prescription.this, searching_deliverer.class));
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("tag", "Error adding document", e);
                                        }
                                    });

                        }
                    });




                }
                else {
                    Toast.makeText(upload_prescription.this,"Please Take a picture to upload",Toast.LENGTH_LONG).show();
                }
            }
            });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Log.d(CAMERA_SERVICE, "before check");
        if (requestCode == REQUEST_PICTURE_CAPTURE && resultCode == Activity.RESULT_OK) {
            File imgFile = new File(pictureFilePath);
            if (imgFile.exists()) {
                is_empty = false;
                imageView.setImageURI(Uri.fromFile(imgFile));
                Log.d(CAMERA_SERVICE, "On Return");
            }
            // Bitmap photo = (Bitmap) data.getExtras().get("data");

        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sendTakePictureIntent() {

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra( MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
           // startActivityForResult(cameraIntent, REQUEST_PICTURE_CAPTURE);

            File pictureFile = null;
            try {
                pictureFile = getPictureFile();
            } catch (IOException ex) {
                Toast.makeText(this,
                        "Photo file can't be created, please try again",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (pictureFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.fyp.android.fileprovider",
                        pictureFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
               startActivityForResult(cameraIntent, REQUEST_PICTURE_CAPTURE);
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private File getPictureFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureFile = "PHARMAGO_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(pictureFile,  ".jpg", storageDir);
        pictureFilePath = image.getAbsolutePath();
        return image;
    }
}