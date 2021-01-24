package com.fyp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.google.type.DateTime;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class generateComplaint extends AppCompatActivity {
    //FIRESTORE KEY CONSTANTS
    public static final String IS_SOLVED_KEY="is_solved";
    public static final String MESSAGE_KEY="message";
    public static final String SUBJECT_KEY="subject";
    public static final String TIMESTAMP_KEY="timestamp";
    public static final String UEMAIL_KEY="uemail";
    public static final String COMPLAIN_ID_KEY="complainID";
    public static final String COUNT_KEY="count";



    boolean is_solved;
    String uemail;
    String uid;
    String subject;
    String messageBody;
    String timeStamp;
    String oldComplNum;
    String newComplNum,complaintCount;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    MaterialButton submitComplaint,cancel;
    TextInputLayout sub,mBody;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_complaint);
        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        uemail=mAuth.getCurrentUser().getEmail();
        uid=mAuth.getCurrentUser().getUid();
        @SuppressLint("SimpleDateFormat")
        DateFormat df = new SimpleDateFormat("d/M/y|h:mm a");
        timeStamp = df.format(Calendar.getInstance().getTime());
        is_solved=false;
        submitComplaint=findViewById(R.id.submit_complain);
        cancel=findViewById(R.id.cancel_button);
        sub=findViewById(R.id.subject);
        mBody=findViewById(R.id.complain_message);
        db.collection("users_complain_count").document(uid).get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                complaintCount=documentSnapshot.getString("count");
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(generateComplaint.this)
                        .setIcon(R.drawable.logo_splash)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton("No",null)
                        .setTitle("Cancel Complaint")
                        .setMessage("Are you Sure You want to cancel complain submission?")
                        .show();
            }
        });
        submitComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(generateComplaint.this)
                        .setTitle("Submit Complain")
                        .setMessage("Are You sure You want to submit Complain")
                        .setIcon(R.drawable.logo_splash)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final ProgressDialog wait=ProgressDialog.show(generateComplaint.this,"Processing","Please Wait");
                                subject=sub.getEditText().getText().toString();
                                messageBody=mBody.getEditText().getText().toString();
                                db.collection("entityCount").document("complains")
                                        .get(Source.SERVER)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                     oldComplNum=documentSnapshot.getString("latest_complain_number");
                                                     newComplNum= String.valueOf((Integer.parseInt(oldComplNum) + 1));
                                                    final Map<String,Object> complain=new HashMap<>();
                                                    complain.put(customer_custom_request.UID_KEY,uid);
                                                    complain.put(IS_SOLVED_KEY,is_solved);
                                                    complain.put(MESSAGE_KEY,messageBody);
                                                    complain.put(SUBJECT_KEY,subject);
                                                    complain.put(TIMESTAMP_KEY,timeStamp);
                                                    complain.put(UEMAIL_KEY,uemail);

                                                     db.collection("complains").document("C"+newComplNum).set(complain)
                                                             .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                 @Override
                                                                 public void onSuccess(Void aVoid) {

                                                                     Map<String,Object> map=new HashMap<>();
                                                                     map.put("latest_complain_number",newComplNum);
                                                                         db.collection("entityCount").document("complains")
                                                                                 .set(map, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                             @Override
                                                                             public void onSuccess(Void aVoid) {

                                                                                 complaintCount=Integer.toString(Integer.parseInt(complaintCount)+1);
                                                                                 Map<String,Object> hashMap=new HashMap<>();
                                                                                 hashMap.put(COMPLAIN_ID_KEY+complaintCount,"C"+newComplNum);
                                                                                 hashMap.put(COUNT_KEY,complaintCount);
                                                                                 hashMap.put(SUBJECT_KEY+complaintCount,complain.get(SUBJECT_KEY));
                                                                                 db.collection("users_complain_count")
                                                                                         .document(uid).set(hashMap,SetOptions.merge())
                                                                                         .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                             @Override
                                                                                             public void onSuccess(Void aVoid) {
                                                                                                 wait.dismiss();
                                                                                                 Toast.makeText(generateComplaint.this,"Complaint Generated",Toast.LENGTH_LONG).show();
                                                                                                 new AlertDialog.Builder(generateComplaint.this)
                                                                                                         .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                                                             @Override
                                                                                                             public void onClick(DialogInterface dialog, int which) {
                                                                                                                 finish();
                                                                                                             }
                                                                                                         })
                                                                                                         .setIcon(R.drawable.logo_splash)
                                                                                                         .setTitle("Done!")
                                                                                                         .setMessage("Your Complaint has been registered, Your representative will contact you on your email. You can check the status of your complain in complains tab in settings")
                                                                                                         .show();
                                                                                             }
                                                                                         });

                                                                             }
                                                                         });
                                                                 }
                                                             });

                                            }
                                        });

                            }
                        })
                        .setNegativeButton("No",null)
                        .show();

            }
        });


    }
}