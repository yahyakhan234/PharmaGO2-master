package com.fyp;
import com.fyp.classes.users_collection;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class pharmacy_order_in_progress extends AppCompatActivity {
    public static final String MED_ID="100";
    public static final String PRICE_ID="101";
    public static final String TAG="tag";

    MaterialButton share,liveChatButton,completeOrderButton;
    private String liveChatID,UID;
    String emailExtra;
    FirebaseFirestore db;
    int count;
    String s;
    TextView med,price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_order_in_progress);
        share=findViewById(R.id.share_location);
        TextView tv=findViewById(R.id.timer_text);
        liveChatButton=findViewById(R.id.live_chat_button);
        completeOrderButton=findViewById(R.id.complete_order_button);

        timeticker(tv);
        db=FirebaseFirestore.getInstance();
        emailExtra=getIntent().getStringExtra("email");
        fillMedsMenu(emailExtra);
        completeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(pharmacy_order_in_progress.this)
                        .setTitle("Mark Order As complete")
                        .setMessage("Complete order request will be generated, and user will be prompted to mark order as complete, Continue?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Map<String,Object> map=new HashMap<>();
                                map.put(users_collection.COMPLETE_REQUESTED,true);
                                db.collection("users").document(emailExtra).set(map, SetOptions.merge())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                new GenerateNotif().requestCompleteFromUser(UID);
                                            }
                                        });

                            }
                        })
                        .setNegativeButton(android.R.string.no,null)
                        .setIcon(R.drawable.location_icon)
                        .show();

            }
        });
        liveChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(pharmacy_order_in_progress.this,
                       live_chat_pharma.class).putExtra("id",liveChatID));
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://maps.app.goo.gl/FPUMyAJ2W9Mx39Gi6");
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);

            }

        });


    }
    public void timeticker (final TextView tv){
        new CountDownTimer(3600000, 1000) {

            public void onTick(long millisUntilFinished) {
                tv.setText(""+ millisUntilFinished / 1000);
            }

            public void onFinish() {
                tv.setText("Time Over!");
            }
        }.start();
    }
    void fillMedsMenu(String email){
        db.collection("processed_accepted_order").document(email)
                .get(Source.SERVER)
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        count = Integer.parseInt(Objects.requireNonNull(documentSnapshot.getString(customer_custom_request.ORDER_COUNT_KEY)));

                        for (int i = 1; i <= count; i++) {

                            LayoutInflater l = LayoutInflater.from(pharmacy_order_in_progress.this);
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
                        UID=documentSnapshot.getString("UID");
                        liveChatID="LC"+documentSnapshot.getString(customer_custom_request.ORDERID_KEY);
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