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
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.fyp.SendNotificationPack.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class dashboard_pharmacy extends AppCompatActivity {

    BottomNavigationView bottomNavigationMenu;
    String refreshToken;
    MaterialButton status_button,signout_button,buy_order_button,get_location;
    FirebaseAuth mAuth;
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
        setContentView(R.layout.activity_dashboard_pharmacy);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

         status_button  = findViewById(R.id.status_set);
         buy_order_button=findViewById(R.id.check_orders);

        get_location=findViewById(R.id.get_location);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE}, 1);

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
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        Log.d("TOKEN","TOKEN GENERATED");
                       refreshToken = task.getResult();
                        updatetoken(refreshToken,firebaseUser,db);

                        Log.d("TOKEN",refreshToken);
                    }
                });

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        status_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // new GenerateNotif().sendNotificationToSingleUser("N3ocfZdKvcVuh0BaFiHumEgOBhR2");
              //  startActivity(new Intent(dashboard_pharmacy.this, pharmacy_status.class));
                    startActivity(new Intent(dashboard_pharmacy.this,live_chat_pharma.class));
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
        get_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnLocation();
            }
        });

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

    void updatetoken(String newtoken, FirebaseUser firebaseUser, FirebaseFirestore db){

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
}
