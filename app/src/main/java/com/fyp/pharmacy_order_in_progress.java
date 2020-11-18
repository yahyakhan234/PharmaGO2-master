package com.fyp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

public class pharmacy_order_in_progress extends AppCompatActivity {

    MaterialButton share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_order_in_progress);
        share=findViewById(R.id.share_location);
        TextView tv=findViewById(R.id.timer_text);
        timeticker(tv);
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
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                tv.setText(""+ millisUntilFinished / 1000);
            }

            public void onFinish() {
                tv.setText("Time Over!");
            }
        }.start();
    }
}