package com.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fyp.SendNotificationPack.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;


public class dashboard extends AppCompatActivity
{
    String refreshToken;
    MaterialButton upload_button,custom_order,signout_button,get_location;
    Button test;
    BottomNavigationView bottomNavigationMenu;
    private FirebaseAuth mAuth;
    Location gps_loc;
    Location network_loc;
    Location final_loc;
    double longitude;
    double latitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        SharedPreferences sharedPreferences=getSharedPreferences("USER_DETAIL", MODE_PRIVATE);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser firebaseUser=mAuth.getCurrentUser();
        setContentView(R.layout.activity_dashboard);
        test=findViewById(R.id.test3);
        upload_button=findViewById(R.id.upload_prescription);
        custom_order=findViewById(R.id.custom_request);
        bottomNavigationMenu=findViewById(R.id.bottom_navigation);
        signout_button=findViewById(R.id.signout_button);
        get_location=findViewById(R.id.get_location);

        TextView welcome=findViewById(R.id.welcome_text);
        String FullName=sharedPreferences.getString("NAME","");
        welcome.setText("Welcome "+FullName);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE}, 1);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        Log.d("TOKEN","TOKEN GENERATED");
                        refreshToken=task.getResult();
                        updatetoken(refreshToken,firebaseUser,db);

                        Log.d("TOKEN",refreshToken);
                    }
                });

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
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
                //startActivity(new Intent(dashboard.this, upload_prescription.class));
                startActivity(new Intent(dashboard.this,live_chat.class));
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
        get_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnLocation();
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
    void updatetoken(String newtoken,FirebaseUser firebaseUser,FirebaseFirestore db){

        Token token1= new Token(newtoken);
        Map<String,Object> m=new HashMap<>();
        m.put("token",newtoken);
        db.collection("pharma_users_online")
                .document(firebaseUser.getUid())
                .set(m, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
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
        // Sign in success, update UI with the signed-in user's information
        Log.d("chk", "refresh token part");




    }
    void returnLocation(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {

        }

            try {

                gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                network_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (gps_loc != null) {
                final_loc = gps_loc;
                latitude = final_loc.getLatitude();
                longitude = final_loc.getLongitude();
            } else if (network_loc != null) {
                final_loc = network_loc;
                latitude = final_loc.getLatitude();
                longitude = final_loc.getLongitude();
            } else {
                latitude = 0.0;
                longitude = 0.0;
            }


            Log.d("tag",Double.toString(latitude+longitude));

    }



}

