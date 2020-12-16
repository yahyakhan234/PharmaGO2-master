package com.fyp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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


                    if(isNetworkAvailable())
                    {
                        startActivity(new Intent(nointernet.this,splash_screen.class));
                    }
            }
        });
    }
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}