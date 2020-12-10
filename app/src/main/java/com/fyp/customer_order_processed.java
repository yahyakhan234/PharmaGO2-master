package com.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialDialogs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Source;

import java.util.Objects;

import co.intentservice.chatui.models.ChatMessage;

public class customer_order_processed extends AppCompatActivity {
    public static final String MED_ID="100";
    public static final String PRICE_ID="101";

    private FirebaseFirestore db;
    MaterialButton cancel_button,live_chat_button;
    FirebaseAuth mAuth;
    String s;
    TextView med,price;


    int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order_processed);
    cancel_button=findViewById(R.id.cancel_button);
    live_chat_button=findViewById(R.id.live_chat);
    inflate_menu();
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(customer_order_processed.this)
                        .setTitle("Cancel Order")
                        .setMessage("Are you sure you want to cancel this order?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                startActivity(new Intent(customer_order_processed.this, dashboard.class));

                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(R.drawable.logo_splash)
                        .show();

                /*

                new MaterialDialog.Builder(this)
                        .title(R.string.ok)
                        .content(R.string.are_you_finish_app)
                        .positiveText(R.string.finish)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("EXIT", true);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .show();*/
            }});

        live_chat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(customer_order_processed.this, live_chat.class));


            }});

    }
    void inflate_menu(){
        count=0;
        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

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
                    med=findViewById(R.id.med);
                    med.setId(Integer.parseInt(MED_ID+i));
                    s=documentSnapshot.getString(customer_custom_request.MED_KEY+i)
                            +" "+documentSnapshot.getString(customer_custom_request.TYPE_KEY+i)
                            +" "+documentSnapshot.getString(customer_custom_request.QTY_KEY+i);
                    med.setText(s);
                    price=findViewById(R.id.price);
                    price.setId(Integer.parseInt(PRICE_ID+i));
                    s="Price: "+documentSnapshot.getString(pharmacy_price_order.PRICE_KEY+i);
                    price.setText(s);

                }
                med=findViewById(R.id.total);
                med.setText(documentSnapshot.getString(pharmacy_price_order.TOTAL_KEY));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


        }
}