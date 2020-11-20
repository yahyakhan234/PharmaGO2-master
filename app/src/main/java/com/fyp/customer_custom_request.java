package com.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class customer_custom_request extends AppCompatActivity {

    MaterialButton addItem,proceed;

    int counter=0;
    TextInputLayout items;
    final public String MED_KEY="med";
    private FirebaseAuth mAuth;
    View[] vi=new View[3];
    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_custom_request);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String email= currentUser.getEmail();
        addItem=findViewById(R.id.addItem);
        proceed = findViewById(R.id.search_deliverer);
        final TextInputLayout med[]=new TextInputLayout[4];
        String medname[]=new String[4];
        final View[] vi=new View[4];
        med[0]=findViewById(R.id.med1);
        med[1]=findViewById(R.id.med2);
        med[2]=findViewById(R.id.med3);
        med[3]=findViewById(R.id.med4);

        vi[0]= findViewById(R.id.med2);
        vi[1]= findViewById(R.id.med3);
        vi[2]= findViewById(R.id.med4);
        vi[3]= findViewById(R.id.med5);
        final Map<String, Object> meds = new HashMap<>();
        GenerateNotif g=new GenerateNotif();
        g.sendNotificationToAllUsers();
    proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(counter==0)
                {
                    meds.put((MED_KEY+counter),med[counter].getEditText().getText().toString());

                }
                else {
                    for (int i = 0; i < counter; i++) {
                        String s= MED_KEY+String.valueOf(i);
                        meds.put(s,med[i].getEditText().getText().toString());

                    }
                }

                startActivity(new Intent(customer_custom_request.this, searching_deliverer.class));
                db.collection("orders").document(email)
                        .set(meds).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("tag", "Added Successfully");
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("tag", "Error adding document", e);
                            }
                        });


            }});



        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(counter<4) {

                    items = (TextInputLayout) vi[counter];
                    items.setVisibility(View.VISIBLE);
                    counter++;
                }


            }});

    }
}