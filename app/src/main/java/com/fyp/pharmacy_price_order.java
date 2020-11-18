package com.fyp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;

public class pharmacy_price_order extends AppCompatActivity {

    MaterialButton process_order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_price_order);
        process_order=findViewById(R.id.process_order);
        process_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(pharmacy_price_order.this, pharmacy_order_in_progress.class));
                finish();
            }

        });

    }
}