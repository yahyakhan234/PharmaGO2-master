package com.fyp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jsibbold.zoomage.ZoomageView;

import java.io.File;
import java.io.IOException;

import static com.fyp.pharmacy_prescription_upload.ORDERID_KEY;

public class view_prescription extends AppCompatActivity {

    String UID,orderID,email;
    File prescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_prescription);


                orderID=getIntent().getStringExtra("orderID");
                StorageReference getpresc= FirebaseStorage.getInstance().getReference();
                try {
                    prescription= File.createTempFile("images", "jpg");;
                    getpresc.child("prescriptions/"+orderID+".jpg").getFile(prescription).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            ProgressBar p=findViewById(R.id.progress_bar);
                            p.setVisibility(View.GONE);
                            ZoomageView prescView=findViewById(R.id.presc_view);
                            prescView.setImageDrawable(Drawable.createFromPath(prescription.getPath()));
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
        MaterialButton materialButton=findViewById(R.id.go_back);
                materialButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }




}