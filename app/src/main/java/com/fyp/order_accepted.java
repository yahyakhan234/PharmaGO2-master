package com.fyp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class order_accepted extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_accepted);
        new Timer().schedule(new TimerTask(){
            public void run() {
                startActivity(new Intent(order_accepted.this, customer_order_processed.class));
                finish();
                Log.d("MainActivity:", "onCreate: waiting 5 seconds for MainActivity... loading PrimaryActivity.class");
            }
        }, 10000 );

    }
}
