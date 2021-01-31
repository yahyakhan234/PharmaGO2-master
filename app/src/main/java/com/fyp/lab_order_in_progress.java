package com.fyp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import static com.fyp.customer_book_test.TEST_UPLOADED_KEY;
import static com.fyp.customer_custom_request.UID_KEY;
import static com.fyp.customer_lab_booking.LAB_ACCEPTED_BOOKINGS_KEY;


public class lab_order_in_progress extends AppCompatActivity {
    public static final String LAB_COMPLETE_REQUESTED_KEY="lab_complete_requested";

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    TextView timeRequestedTV,dateRequestedTV,totalTV,testName,resultTV;
    ProgressDialog wait;
    MaterialButton live_chat_button,select_file_button,upload_result_button,complete_order;
    Uri pdfUri;
    TextView selectedFile;
    String orderID,uemail,UID;
    ProgressDialog progressDialog;
    boolean test_uploaded;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_order_in_progress);
        uemail=getIntent().getStringExtra("email");
        live_chat_button=findViewById(R.id.live_chat);
        select_file_button=findViewById(R.id.select_file);
        selectedFile=findViewById(R.id.file_selected);
        upload_result_button=findViewById(R.id.upload_result);
        wait= ProgressDialog.show(lab_order_in_progress.this,"Processing","Please Wait");
        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        timeRequestedTV=findViewById(R.id.time_requested);
        testName=findViewById(R.id.test_type);
        dateRequestedTV=findViewById(R.id.date_requested);
        totalTV=findViewById(R.id.total);
        resultTV=findViewById(R.id.result_in_days);
        complete_order=findViewById(R.id.complete_order);
        complete_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (test_uploaded) {
                    completeOrderRequested();
                }
                else {
                    Toast.makeText(lab_order_in_progress.this,"Please Upload Test before marking your order as complete",Toast.LENGTH_LONG).show();
                }
            }
        });
        setDetails();
        upload_result_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pdfUri!=null){
                    uploadFile();
                }
                else {
                    Toast.makeText(lab_order_in_progress.this,"Please Select a file first!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        select_file_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(lab_order_in_progress.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                {
                    selectPDF();
                }
                else {
                    ActivityCompat.requestPermissions(lab_order_in_progress.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},9);

                }
            }
        });


    }

    private void setDetails() {
        db.collection(LAB_ACCEPTED_BOOKINGS_KEY).document(uemail).get(Source.SERVER)
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        testName.setText(documentSnapshot.getString(lab_price_order.TEST_TYPE_NAME_KEY));
                        timeRequestedTV.setText(documentSnapshot.getString(lab_price_order.TIME_REQUESTED_KEY));
                        dateRequestedTV.setText(documentSnapshot.getString(lab_price_order.DATE_REQUESTED_KEY));
                        totalTV.setText(documentSnapshot.getString(lab_price_order.TOTAL_KEY));
                        resultTV.setText(documentSnapshot.getString(lab_price_order.DELIVERY_IN_DAYS_KEY));
                        orderID=documentSnapshot.getString("orderID");
                        test_uploaded=documentSnapshot.getBoolean(TEST_UPLOADED_KEY);
                        UID=documentSnapshot.getString(UID_KEY);
                        live_chat_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(lab_order_in_progress.this,live_chat_pharma.class)
                                        .putExtra("id","LC"+orderID));
                            }
                        });
                        wait.dismiss();
                    }
                });
    }

    private void uploadFile() {
        progressDialog=new ProgressDialog(lab_order_in_progress.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading Result");
        progressDialog.setProgress(0);
        progressDialog.show();

        StorageReference storageReference= FirebaseStorage.getInstance().getReference();
        storageReference=storageReference.child("lab_results").child(orderID);
        storageReference.putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(lab_order_in_progress.this,"Uploaded",Toast.LENGTH_LONG).show();
                Map<String,Object> objectMap=new HashMap<>();
                objectMap.put(TEST_UPLOADED_KEY,true);
                test_uploaded=true;
                db.collection(LAB_ACCEPTED_BOOKINGS_KEY).document(uemail).set(objectMap,SetOptions.merge());
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                int progressPercentage= (int) (100*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                progressDialog.setProgress(progressPercentage);
                if (progressPercentage==100){
                    progressDialog.dismiss();
                    Toast.makeText(lab_order_in_progress.this,"Result Uploaded, Notifying User",Toast.LENGTH_LONG).show();
                }
            }
        })
        ;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==9&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            selectPDF();
        }
        else{
            Toast.makeText(lab_order_in_progress.this,"Please Provide Permission",Toast.LENGTH_SHORT).show();
        }
    }

    private void selectPDF() {
        Intent intent=new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,69);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==69 && resultCode==RESULT_OK && data!=null){
            pdfUri=data.getData();
            selectedFile.setText("File Selected");
        }
        else {
            Toast.makeText(lab_order_in_progress.this,"Please Select A file",Toast.LENGTH_SHORT).show();
        }

    }
    private void completeOrderRequested(){

        Map<String,Object> map=new HashMap<>();
        map.put(LAB_COMPLETE_REQUESTED_KEY,true);
        db.collection("users").document(uemail).set(map, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                new AlertDialog.Builder(lab_order_in_progress.this)
                        .setTitle("Done")
                        .setIcon(R.drawable.logo_splash)
                        .setMessage("User Notified, You will be informed when user marks order as complete")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                startActivity(new Intent(lab_order_in_progress.this,dashboard_lab.class));
                            }
                        });
                new GenerateNotif().labOrderCompleteNotify(UID);
            }
        });

    }
}