package com.fyp.classes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.R;
import com.fyp.generateComplaint;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class complaintDetail extends AppCompatActivity {

    String complaintID,subject,timestamp,message;
    TextView bodyView,subjectView,timestampView,statusView;
    boolean is_solved;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_detail);
        db= FirebaseFirestore.getInstance();
        complaintID=getIntent().getStringExtra("complaintID");
        bodyView=findViewById(R.id.complain_message);
        subjectView=findViewById(R.id.subject);
        timestampView=findViewById(R.id.timestamp_text_view);
        statusView=findViewById(R.id.status_text_view);

        final ProgressDialog wait=ProgressDialog.show(complaintDetail.this,"Loading","Please Wait",true);
        if (complaintID==null){
            finish();
        }
        db.collection("complains").document(complaintID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                subject=documentSnapshot.getString(generateComplaint.SUBJECT_KEY);
                message=documentSnapshot.getString(generateComplaint.MESSAGE_KEY);
                timestamp=documentSnapshot.getString(generateComplaint.TIMESTAMP_KEY);
                is_solved=documentSnapshot.getBoolean(generateComplaint.IS_SOLVED_KEY);
                bodyView.setText(message);
                subjectView.setText(subject);
                timestampView.setText(timestamp);
                if (is_solved){
                    statusView.setText("Solved");
                }
                else
                {
                    statusView.setText("Unsolved");

                }
                wait.dismiss();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(complaintDetail.this,"Some Error Occurred, Try again Later",Toast.LENGTH_LONG).show();
                finish();
            }
        });

    }
}