package com.fyp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;

import java.net.UnknownHostException;

public class nointernet extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nointernet);
        MaterialButton materialButton=findViewById(R.id.retry_connection);
        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                splash_screen s=new splash_screen();
                try {
                    if(s.isInternetAvailable())
                    {
                        startActivity(new Intent(nointernet.this,splash_screen.class));
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}