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
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fyp.SendNotificationPack.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class dashboard_pharmacy extends AppCompatActivity {

    BottomNavigationView bottomNavigationMenu;
    String refreshToken;
    MaterialButton status_button,signout_button,buy_order_button,get_location,viewComplainsButton;
    FirebaseAuth mAuth;
    Location gps_loc;
    Location network_loc;
    Location final_loc;
    double longitude;
    double latitude;
     FirebaseFirestore db;
     public static final int TVID=100;
     public static final int EMAILID=101;
     public static final int BUTTONID=102;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        SharedPreferences sharedPreferences=getSharedPreferences("USER_DETAIL", MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();
        final FirebaseUser firebaseUser=mAuth.getCurrentUser();

        setContentView(R.layout.activity_dashboard_pharmacy);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

         status_button  = findViewById(R.id.status_set);
         buy_order_button=findViewById(R.id.check_orders);
         viewComplainsButton=findViewById(R.id.view_complain);
         viewComplainsButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 
             }
         });

        get_location=findViewById(R.id.get_location);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE}, 1);
        fillInProgressMenu();
        bottomNavigationMenu=findViewById(R.id.bottom_navigation_pharmacy);
        signout_button=findViewById(R.id.signout_button);
        bottomNavigationMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.bottomNavigationSetting) {
                    LinearLayout L1=findViewById(R.id.browse);
                    L1.setVisibility(View.GONE);
                    L1=findViewById(R.id.in_progress_orders);
                    L1.setVisibility(View.GONE);
                    LinearLayout L = findViewById(R.id.settings);
                    L.setVisibility(View.VISIBLE);


                    return true;
                }
                if (item.getItemId() == R.id.bottomNavigationBrowse) {
                    LinearLayout L1=findViewById(R.id.settings);
                    L1.setVisibility(View.GONE);
                    L1=findViewById(R.id.in_progress_orders);
                    L1.setVisibility(View.GONE);
                    LinearLayout L = findViewById(R.id.browse);
                    L.setVisibility(View.VISIBLE);


                    return true;
                }
                if (item.getItemId()==R.id.bottomNavigationProgress){

                    LinearLayout L1=findViewById(R.id.settings);
                    L1.setVisibility(View.GONE);
                    L1=findViewById(R.id.in_progress_orders);
                    L1.setVisibility(View.VISIBLE);
                    LinearLayout L = findViewById(R.id.browse);
                    L.setVisibility(View.GONE);
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
    void fillInProgressMenu(){
        final Context context=this;
        db=FirebaseFirestore.getInstance();
        db.collection("pharma_orders").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(!documentSnapshot.getString("count").equalsIgnoreCase("0")) {
                    for (int i = 1; i <= Integer.parseInt(documentSnapshot.getString("count")); i++) {
                        LayoutInflater l=LayoutInflater.from(context);
                        View v= l.inflate(R.layout.in_progress_orders_resource,null);
                        LinearLayout parent=(LinearLayout) findViewById(R.id.orders_container);
                        parent.addView(v);
                        String stringText="Order from "+documentSnapshot.getString("name"+i);
                        TextView textView=findViewById(R.id.tv_customer);
                        textView.setText(stringText);
                        textView.setId(Integer.parseInt(Integer.toString(TVID)+i));
                        textView=findViewById(R.id.email_hidden);
                        textView.setText(documentSnapshot.getString("order"+i));
                        textView.setId(Integer.parseInt(Integer.toString(EMAILID)+i));
                        MaterialButton mButton=findViewById(R.id.details);
                        mButton.setId(Integer.parseInt(Integer.toString(BUTTONID)+i));
                        mButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TextView t=findViewById(Integer.parseInt(
                                        EMAILID+String.valueOf(Integer.toString(v.getId())
                                                .charAt(Integer.toString(v.getId()).length()-1))));
                                startActivity(new Intent(dashboard_pharmacy.this,pharmacy_order_in_progress.class)
                                        .putExtra("email",t.getText()));
                                Log.d("tag","email:"+t.getText());
                            }
                        });


                    }
                }
                else
                {
                    TextView tv=findViewById(R.id.no_orders);
                    tv.setVisibility(View.VISIBLE);
                }
                ProgressBar progressBar=findViewById(R.id.progress_bar);
                progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

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
        db=FirebaseFirestore.getInstance();
        Map<String,Object> map=new HashMap<>();
        map.put("is_located",true);
        map.put("lat",Double.toString(latitude));
        map.put("long",Double.toString(longitude));
        db.collection("pharma_users_online")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .set(map,SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
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
