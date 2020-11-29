package com.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class dashboard_pharmacy extends AppCompatActivity {

    BottomNavigationView bottomNavigationMenu;
    MaterialButton status_button,signout_button,buy_order_button;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_dashboard_pharmacy);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

         status_button  = findViewById(R.id.status_set);
         buy_order_button=findViewById(R.id.check_orders);

        bottomNavigationMenu=findViewById(R.id.bottom_navigation_pharmacy);
        signout_button=findViewById(R.id.signout_button);
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

        status_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(dashboard_pharmacy.this, pharmacy_status.class));

            }

        });
        signout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(dashboard_pharmacy.this, login_Screen.class));
                mAuth.signOut();
            }
        });
        buy_order_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(dashboard_pharmacy.this,Buy_Requests.class));
            }
        });

    }
}
