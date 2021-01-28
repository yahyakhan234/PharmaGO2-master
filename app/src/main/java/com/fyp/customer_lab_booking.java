package com.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class customer_lab_booking extends AppCompatActivity {
    public static final String HAS_TEST_BOOKED_KEY="has_test_booked";
    public static final String IS_TEST_ACCEPTED_KEY="is_test_accepted";
    public static final String LAB_ACCEPTED_BOOKINGS_KEY="lab_accepted_bookings";
    public static final String TAG="Accept order";
    public static final String LAB_UNACCEPTED_BOOKINGS_KEY="lab_unaccepted_bookings";


    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    TextView timeRequestedTV,dateRequestedTV,totalTV,testName,resultTV;
    MaterialButton liveChatButton,cancel_button_disabled,cancel_button,accept_button,lab_location_button;
    String orderID,s,PID;
    ProgressDialog wait;
    DocumentReference fromPath,toPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_lab_booking);
        wait=ProgressDialog.show(customer_lab_booking.this,"Processing","Please Wait");
        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        timeRequestedTV=findViewById(R.id.time_requested);
        testName=findViewById(R.id.test_type);
        dateRequestedTV=findViewById(R.id.date_requested);
        totalTV=findViewById(R.id.total);
        liveChatButton=findViewById(R.id.live_chat);
        cancel_button=findViewById(R.id.cancel_button);
        cancel_button_disabled=findViewById(R.id.cancel_button_disabled);
        accept_button=findViewById(R.id.accept_button);
        resultTV=findViewById(R.id.result_in_days);
        setDetails();
        accept_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptOrder();
                cancel_button.setVisibility(View.GONE);
                cancel_button_disabled.setVisibility(View.VISIBLE);
                accept_button.setVisibility(View.GONE);
                liveChatButton.setVisibility(View.VISIBLE);
            }});

    }
    void acceptOrder(){

        new AlertDialog.Builder(customer_lab_booking.this)
                .setTitle("Accept Order")
                .setMessage("Are you sure you want to Accept this order? You will not be able to cancel the order once it is accepted")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        s = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        fromPath = db.collection(LAB_UNACCEPTED_BOOKINGS_KEY).document(s);
                        toPath = db.collection(LAB_ACCEPTED_BOOKINGS_KEY).document(s);

                        fromPath.get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                PID = documentSnapshot.getString("PID");
                            }
                        });
                        fromPath.get(Source.SERVER).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    final DocumentSnapshot document = task.getResult();
                                    if (document != null) {
                                        toPath.set(document.getData())
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                                        fromPath.delete()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        final Map<String, Object> m = new HashMap<>();
                                                                        m.put(HAS_TEST_BOOKED_KEY, true);
                                                                        m.put(IS_TEST_ACCEPTED_KEY,true);
                                                                        db.collection("users")
                                                                                .document(Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail()))
                                                                                .set(m, SetOptions.merge());
                                                                        final Map<String, Object> map = new HashMap<>();
                                                                        db.collection("lab_bookings").document(PID)
                                                                                .get(Source.SERVER)
                                                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                                    @Override
                                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                        String i = Integer.toString(Integer.parseInt(Objects.requireNonNull(documentSnapshot.getString("count"))) + 1);
                                                                                        SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAIL", MODE_PRIVATE);
                                                                                        String FullName = sharedPreferences.getString("NAME", "");
                                                                                        map.put("count", i);
                                                                                        map.put("order" + i, mAuth.getCurrentUser().getEmail());
                                                                                        map.put("name" + i, FullName);
                                                                                        db.collection("lab_bookings").document(PID).set(map, SetOptions.merge());
                                                                                    }
                                                                                });
                                                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                                        new GenerateNotif().sendNotificationToSinglePharmacist(PID);
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Log.w(TAG, "Error deleting document", e);
                                                                    }
                                                                });
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error writing document", e);
                                                    }
                                                });
                                    } else {
                                        Log.d(TAG, "No such document");
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });

                    }
                })

                .setNegativeButton(android.R.string.no, null)
                .setIcon(R.drawable.logo_splash)
                .show();
    }

    void setDetails(){

        db.collection("users").document(mAuth.getCurrentUser().getEmail()).get(Source.SERVER)
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        wait.dismiss();
                        if (documentSnapshot.getBoolean(IS_TEST_ACCEPTED_KEY)){
                            cancel_button.setVisibility(View.GONE);
                            cancel_button_disabled.setVisibility(View.VISIBLE);
                            accept_button.setVisibility(View.GONE);
                            liveChatButton.setVisibility(View.VISIBLE);
                            db.collection(LAB_ACCEPTED_BOOKINGS_KEY).document(mAuth.getCurrentUser().getEmail()).get(Source.SERVER)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            testName.setText(documentSnapshot.getString(lab_price_order.TEST_TYPE_NAME_KEY));
                                            timeRequestedTV.setText(documentSnapshot.getString(lab_price_order.TIME_REQUESTED_KEY));
                                            dateRequestedTV.setText(documentSnapshot.getString(lab_price_order.DATE_REQUESTED_KEY));
                                            totalTV.setText(documentSnapshot.getString(lab_price_order.TOTAL_KEY));
                                            resultTV.setText(documentSnapshot.getString(lab_price_order.DELIVERY_IN_DAYS_KEY));
                                            orderID=documentSnapshot.getString("orderID");
                                            liveChatButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    startActivity(new Intent(customer_lab_booking.this,live_chat.class)
                                                            .putExtra("id","LC"+orderID));
                                                }
                                            });
                                        }
                                    });
                        }
                        else {
                            cancel_button.setVisibility(View.VISIBLE);
                            cancel_button_disabled.setVisibility(View.GONE);
                            accept_button.setVisibility(View.VISIBLE);
                            liveChatButton.setVisibility(View.GONE);
                            db.collection(LAB_UNACCEPTED_BOOKINGS_KEY).document(mAuth.getCurrentUser().getEmail()).get(Source.SERVER)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            testName.setText(documentSnapshot.getString(lab_price_order.TEST_TYPE_NAME_KEY));
                                            timeRequestedTV.setText(documentSnapshot.getString(lab_price_order.TIME_REQUESTED_KEY));
                                            dateRequestedTV.setText(documentSnapshot.getString(lab_price_order.DATE_REQUESTED_KEY));
                                            totalTV.setText(documentSnapshot.getString(lab_price_order.TOTAL_KEY));
                                            resultTV.setText(documentSnapshot.getString(lab_price_order.DELIVERY_IN_DAYS_KEY));
                                            orderID=documentSnapshot.getString("orderID");
                                            liveChatButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    startActivity(new Intent(customer_lab_booking.this,live_chat.class)
                                                            .putExtra("id","LC"+orderID));
                                                }
                                            });
                                        }
                                    });

                        }
                    }
                });
    }
}