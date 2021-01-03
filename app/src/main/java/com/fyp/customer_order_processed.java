package com.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialDialogs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import co.intentservice.chatui.models.ChatMessage;

public class customer_order_processed extends AppCompatActivity {
    public static final String MED_ID="100";
    public static final String PRICE_ID="101";
    public static final String TAG="tag";
    private FirebaseFirestore db;
    MaterialButton cancel_button,live_chat_button,accept_button,pharmacy_details_button;
    FirebaseAuth mAuth;
    String s,PID,tempo,setTime;
    boolean is_Accepted;
    TextView med,price;
    int remainderTime;
    CountDownTimer timer;


    int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order_processed);
    cancel_button=findViewById(R.id.cancel_button);
    live_chat_button=findViewById(R.id.live_chat);
    accept_button=findViewById(R.id.accept_order);
    pharmacy_details_button=findViewById(R.id.pharmay_details_button);
    pharmacy_details_button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(customer_order_processed.this,pharmacy_details.class));
        }
    });
    inflate_menu();
    if (is_Accepted){

        accept_button.setVisibility(View.GONE);
        live_chat_button.setVisibility(View.VISIBLE);

    }
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(customer_order_processed.this)
                        .setTitle("Cancel Order")
                        .setMessage("Are you sure you want to cancel this order?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                startActivity(new Intent(customer_order_processed.this, dashboard.class));
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(R.drawable.logo_splash)
                        .show();

            }});

        live_chat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(customer_order_processed.this, live_chat.class));


            }});
        accept_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(customer_order_processed.this)
                        .setTitle("Accept Order")
                        .setMessage("Are you sure you want to Accept this order? You will not be able to cancel the order once it is accepted")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation

                                s=FirebaseAuth.getInstance().getCurrentUser().getEmail();

                                final DocumentReference fromPath=db.collection("processed_unaccepted_order").document(s);
                                final DocumentReference toPath=db.collection("processed_accepted_order").document(s);

                                fromPath.get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        PID=documentSnapshot.getString("PID");
                                    }
                                });
                                fromPath.get(Source.SERVER).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
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
                                                                                Map<String, Object> m=new HashMap<>();
                                                                                m.put("is_accepted",true);
                                                                                db.collection("users")
                                                                                        .document(Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail()))
                                                                                        .set(m,SetOptions.merge());
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
                                accept_button.setVisibility(View.GONE);
                                live_chat_button.setVisibility(View.VISIBLE);
                                cancel_button.setVisibility(View.GONE);
                                cancel_button=findViewById(R.id.cancel_button_disabled);
                                cancel_button.setVisibility(View.VISIBLE);

                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(R.drawable.logo_splash)
                        .show();

            }
        });

    }
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences
                = getSharedPreferences("USER_DETAIL", MODE_PRIVATE);

        SharedPreferences.Editor editPrefs;
        editPrefs = sharedPreferences.edit();
        editPrefs.putBoolean("is_viewing_timer", true);
        editPrefs.apply();
        final TextView timer_text_customer_delivery=findViewById(R.id.timer_text_customer_delivery);
       SharedPreferences sp = getSharedPreferences("order_time", MODE_PRIVATE);
       remainderTime= (int) (sp.getInt("time",0)-System.currentTimeMillis());
       timer= new CountDownTimer(remainderTime, 1000) {

            public void onTick(long millisUntilFinished) {
                tempo=((millisUntilFinished/1000)/60)+":"+(millisUntilFinished/1000);
                timer_text_customer_delivery.setText(tempo);
            }
            public void onFinish() {
                tempo="Time Over!";
                timer_text_customer_delivery.setText(tempo);

            }
        }.start();

    }
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences
                = getSharedPreferences("USER_DETAIL", MODE_PRIVATE);
        SharedPreferences.Editor editPrefs;
        editPrefs = sharedPreferences.edit();
        editPrefs.putBoolean("is_viewing_timer", false);
        editPrefs.apply();
        timer.cancel();

    }
    void inflate_menu(){
        count=0;
        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        db.collection("users")
                .document(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail())).get(Source.SERVER)
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        is_Accepted = documentSnapshot.getBoolean("is_accepted");
                        if (!is_Accepted) {
                            db.collection("processed_unaccepted_order").document(mAuth.getCurrentUser().getEmail())
                                    .get(Source.SERVER)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            count = Integer.parseInt(Objects.requireNonNull(documentSnapshot.getString(customer_custom_request.ORDER_COUNT_KEY)));

                                            for (int i = 1; i <= count; i++) {

                                                LayoutInflater l = LayoutInflater.from(customer_order_processed.this);
                                                View v = l.inflate(R.layout.med_processed_price_resource, null);
                                                LinearLayout parent = findViewById(R.id.parent_inflater);
                                                parent.addView(v);
                                                med = findViewById(R.id.med);
                                                med.setId(Integer.parseInt(MED_ID + i));
                                                s = documentSnapshot.getString(customer_custom_request.MED_KEY + i)
                                                        + " " + documentSnapshot.getString(customer_custom_request.TYPE_KEY + i)
                                                        + " " + documentSnapshot.getString(customer_custom_request.QTY_KEY + i);
                                                med.setText(s);
                                                price = findViewById(R.id.price);
                                                price.setId(Integer.parseInt(PRICE_ID + i));
                                                s = "Price: " + documentSnapshot.getString(pharmacy_price_order.PRICE_KEY + i);
                                                price.setText(s);

                                            }
                                            med = findViewById(R.id.total);
                                            med.setText(documentSnapshot.getString(pharmacy_price_order.TOTAL_KEY));
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        } else {
                            accept_button.setVisibility(View.GONE);
                            live_chat_button.setVisibility(View.VISIBLE);
                            cancel_button.setVisibility(View.GONE);
                            cancel_button=findViewById(R.id.cancel_button_disabled);
                            cancel_button.setVisibility(View.VISIBLE);
                            db.collection("processed_accepted_order").document(mAuth.getCurrentUser().getEmail())
                                    .get(Source.SERVER)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            count = Integer.parseInt(Objects.requireNonNull(documentSnapshot.getString(customer_custom_request.ORDER_COUNT_KEY)));

                                            for (int i = 1; i <= count; i++) {

                                                LayoutInflater l = LayoutInflater.from(customer_order_processed.this);
                                                View v = l.inflate(R.layout.med_processed_price_resource, null);
                                                LinearLayout parent = findViewById(R.id.parent_inflater);
                                                parent.addView(v);
                                                med = findViewById(R.id.med);
                                                med.setId(Integer.parseInt(MED_ID + i));
                                                s = documentSnapshot.getString(customer_custom_request.MED_KEY + i)
                                                        + " " + documentSnapshot.getString(customer_custom_request.TYPE_KEY + i)
                                                        + " " + documentSnapshot.getString(customer_custom_request.QTY_KEY + i);
                                                med.setText(s);
                                                price = findViewById(R.id.price);
                                                price.setId(Integer.parseInt(PRICE_ID + i));
                                                s = "Price: " + documentSnapshot.getString(pharmacy_price_order.PRICE_KEY + i);
                                                price.setText(s);

                                            }
                                            med = findViewById(R.id.total);
                                            med.setText(documentSnapshot.getString(pharmacy_price_order.TOTAL_KEY));
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }
                    }


                });



        }
}