package com.fyp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class pharmacy_prescription_upload extends AppCompatActivity {

    MaterialButton addItem,proceed;

    int counter=0;
    TextInputLayout items;
    View[] vi=new View[3];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_prescription_upload);
        addItem=findViewById(R.id.addItem);
        proceed = findViewById(R.id.proceed);

        final View[] vi=new View[4];
        vi[0]= findViewById(R.id.med2);
        vi[1]= findViewById(R.id.med3);
        vi[2]= findViewById(R.id.med4);
        vi[3]= findViewById(R.id.med5);

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(pharmacy_prescription_upload.this, pharmacy_price_order.class));



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