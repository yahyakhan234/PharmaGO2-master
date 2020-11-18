package com.fyp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;

public class lab_order_in_progress extends AppCompatActivity {
    MaterialButton live_chat_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_order_in_progress);

        live_chat_button=findViewById(R.id.live_chat);
        live_chat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(lab_order_in_progress.this, live_chat.class));


            }});


    }
}