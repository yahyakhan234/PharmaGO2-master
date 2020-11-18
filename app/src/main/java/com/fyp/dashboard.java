package com.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;


public class dashboard extends AppCompatActivity
{
    MaterialButton upload_button,custom_order,signout_button;
    Button test;
    BottomNavigationView bottomNavigationMenu;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        GenerateNotif n=new GenerateNotif();
       // n.get_all_online_users();
        setContentView(R.layout.activity_dashboard);
        test=findViewById(R.id.test3);
        upload_button=findViewById(R.id.upload_prescription);
        custom_order=findViewById(R.id.custom_request);
        bottomNavigationMenu=findViewById(R.id.bottom_navigation);
        signout_button=findViewById(R.id.signout_button);
        signout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(dashboard.this, login_Screen.class));
                mAuth.signOut();
            }
        });
        upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(dashboard.this, upload_prescription.class));

            }
        });
        custom_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(dashboard.this, customer_custom_request.class));

            }
        });
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(dashboard.this, customer_book_test.class));

            }
        });
        bottomNavigationMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.bottomNavigationSetting) {
                    LinearLayout L1=findViewById(R.id.browse);
                    L1.setVisibility(View.GONE);
                    LinearLayout L = findViewById(R.id.settings);
                    L.setVisibility(View.VISIBLE);


                    return true;
                }
                if (item.getItemId() == R.id.bottomNavigationBrowse) {
                    LinearLayout L1=findViewById(R.id.settings);
                    L1.setVisibility(View.GONE);
                    LinearLayout L = findViewById(R.id.browse);
                    L.setVisibility(View.VISIBLE);


                    return true;
                }
                return false;
            }
        });




    }

}

