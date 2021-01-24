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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.SendNotificationPack.Token;
import com.fyp.classes.viewComplains;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

import static com.fyp.dashboard_pharmacy.BUTTONID;
import static com.fyp.dashboard_pharmacy.EMAILID;
import static com.fyp.dashboard_pharmacy.TVID;


public class dashboard extends AppCompatActivity
{
    String refreshToken;
    MaterialButton upload_button,custom_order,signout_button,get_location,generateComplaint;
    Button test;
    BottomNavigationView bottomNavigationMenu;
    RelativeLayout pending_order,loading_layout;
    LinearLayout new_order_cluster;
    String status;
    private FirebaseAuth mAuth;
    Location gps_loc;
    Location network_loc;
    Location final_loc;
    double longitude;
    double latitude;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        SharedPreferences sharedPreferences=getSharedPreferences("USER_DETAIL", MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();
        final FirebaseUser firebaseUser=mAuth.getCurrentUser();
        setContentView(R.layout.activity_dashboard);
        test=findViewById(R.id.test3);
        upload_button=findViewById(R.id.upload_prescription);
        custom_order=findViewById(R.id.custom_request);
        bottomNavigationMenu=findViewById(R.id.bottom_navigation);
        signout_button=findViewById(R.id.signout_button);
        get_location=findViewById(R.id.get_location);
        pending_order=findViewById(R.id.pending_order_view);
        new_order_cluster=findViewById(R.id.new_order_cluster);
        TextView welcome=findViewById(R.id.welcome_text);
        loading_layout=findViewById(R.id.loadingPanel);
        generateComplaint=findViewById(R.id.generate_complaint);
        generateComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(dashboard.this, viewComplains.class));
            }
        });
        inflateCompletedOrders();
        String FullName=sharedPreferences.getString("NAME","");
        welcome.setText("Welcome "+FullName);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE}, 1);

        db.collection("users").document(mAuth.getCurrentUser().getEmail()).get(Source.SERVER)
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getBoolean("is_ordering"))
                {
                    loading_layout.setVisibility(View.GONE);
                    new_order_cluster.setVisibility(View.GONE);
                    pending_order.setVisibility(View.VISIBLE);
                }
                else {
                    loading_layout.setVisibility(View.GONE);
                    new_order_cluster.setVisibility(View.VISIBLE);
                    pending_order.setVisibility(View.GONE);
                }
                status=documentSnapshot.getString("status");
            }
        });

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
        pending_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(dashboard.this,"Clicked",Toast.LENGTH_SHORT).show();
                switch (status){
                    case "searching":{
                        startActivity(new Intent(dashboard.this,searching_deliverer.class));
                        break;
                    }
                    case "priced":{
                    startActivity(new Intent(dashboard.this,customer_order_processed.class));
                    break;
                    }
                    case "accepted":{
                        startActivity(new Intent(dashboard.this,customer_order_processed.class));
                    }

                }
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
                    findViewById(R.id.completed_orders).setVisibility(View.GONE);
                    return true;
                }
                if (item.getItemId() == R.id.bottomNavigationBrowse) {
                    LinearLayout L1=findViewById(R.id.settings);
                    L1.setVisibility(View.GONE);
                    LinearLayout L = findViewById(R.id.browse);
                    L.setVisibility(View.VISIBLE);
                    findViewById(R.id.completed_orders).setVisibility(View.GONE);




                    return true;
                }
                if (item.getItemId() == R.id.bottomNavigationCompleted) {
                    LinearLayout L1=findViewById(R.id.settings);
                    L1.setVisibility(View.GONE);
                    LinearLayout L = findViewById(R.id.browse);
                    L.setVisibility(View.GONE);
                    findViewById(R.id.completed_orders).setVisibility(View.VISIBLE);



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


            db=FirebaseFirestore.getInstance();
            Map<String,Object> map=new HashMap<>();
            map.put("is_located",true);
            map.put("lat",Double.toString(latitude));
            map.put("long",Double.toString(longitude));
            db.collection("pharma_users_online")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(map,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("tag","Stored SuCKSESSFULL");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("tag","Stored FAILURER: "+e);

                }
            });

            Log.d("tag",Double.toString(latitude+longitude));

    }
    void inflateCompletedOrders(){

        db.collection("user_orders_completed").document(mAuth.getCurrentUser().getUid()).get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                findViewById(R.id.progress_bar).setVisibility(View.GONE);
            if (!documentSnapshot.getString("count").equals("0")) {
                TextView tv=findViewById(R.id.no_orders);
                tv.setVisibility(View.GONE);
                for (int i = 1; i <= Integer.parseInt(documentSnapshot.getString("count")); i++) {
                    LayoutInflater l = LayoutInflater.from(dashboard.this);
                    View v = l.inflate(R.layout.in_progress_orders_resource, null);
                    LinearLayout parent = (LinearLayout) findViewById(R.id.orders_container);
                    parent.addView(v);
                    String stringText = "Order ID: " + documentSnapshot.getString("order" + i);
                    TextView textView = findViewById(R.id.tv_customer);
                    textView.setText(stringText);
                    textView.setId(Integer.parseInt(Integer.toString(TVID) + i));
                    textView = findViewById(R.id.email_hidden);
                    textView.setText(documentSnapshot.getString("order" + i));
                    textView.setId(Integer.parseInt(Integer.toString(EMAILID) + i));
                    MaterialButton mButton = findViewById(R.id.details);
                    mButton.setId(Integer.parseInt(Integer.toString(BUTTONID) + i));
                    mButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TextView t = findViewById(Integer.parseInt(
                                    EMAILID + String.valueOf(Integer.toString(v.getId())
                                            .charAt(Integer.toString(v.getId()).length() - 1))));
                            startActivity(new Intent(dashboard.this, order_completed.class)
                                    .putExtra("orderID", t.getText()));
                            Log.d("tag", "email:" + t.getText());
                        }
                    });
                }
            }
            else{
                TextView textView=findViewById(R.id.no_orders);
                textView.setVisibility(View.VISIBLE);
            }

                }
        });

    }



}

