package com.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fyp.SendNotificationPack.Token;
import com.fyp.classes.viewComplains;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jsibbold.zoomage.ZoomageView;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class dashboard_lab extends AppCompatActivity {
    BottomNavigationView bottomNavigationMenu;
    String refreshToken;
    MaterialButton status_button,signout_button,buy_order_button,get_location,viewComplainsButton,generateComplaint;
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
    File prescription;
    TextView nameTV,usernameTV,phoneTV,emailTV;
    RatingBar ratingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_lab);
        mAuth = FirebaseAuth.getInstance();
        SharedPreferences sharedPreferences=getSharedPreferences("USER_DETAIL", MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();
        final FirebaseUser firebaseUser=mAuth.getCurrentUser();

        MaterialButton view_tests_button=findViewById(R.id.view_requested_bookings);
        get_location=findViewById(R.id.get_location);
        setBannerNews();
        view_tests_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(dashboard_lab.this,buy_requests_lab.class));
            }
        });
        returnLocation();
        bottomNavigationMenu=findViewById(R.id.bottom_navigation_lab);
        signout_button=findViewById(R.id.signout_button);
        generateComplaint=findViewById(R.id.generate_complaint);
        generateComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(dashboard_lab.this, viewComplains.class));
            }
        });
        inflateProfile();
        fillInProgressMenu();
        inflateCompletedOrders();
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

        get_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnLocation();
            }
        });
        signout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences("USER_DETAIL", Context.MODE_PRIVATE);
                settings.edit().clear().commit();
                finish();
                startActivity(new Intent(dashboard_lab.this, login_Screen.class));
                mAuth.signOut();
            }
        });
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE}, 1);
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

                    findViewById(R.id.completed_orders).setVisibility(View.GONE);
                    findViewById(R.id.profile).setVisibility(View.GONE);

                    return true;
                }
                if (item.getItemId() == R.id.bottomNavigationBrowse) {
                    LinearLayout L1=findViewById(R.id.settings);
                    L1.setVisibility(View.GONE);
                    L1=findViewById(R.id.in_progress_orders);
                    L1.setVisibility(View.GONE);
                    LinearLayout L = findViewById(R.id.browse);
                    L.setVisibility(View.VISIBLE);

                    findViewById(R.id.completed_orders).setVisibility(View.GONE);
                    findViewById(R.id.profile).setVisibility(View.GONE);


                    return true;
                }
                if (item.getItemId()==R.id.bottomNavigationProgress){

                    LinearLayout L1=findViewById(R.id.settings);
                    L1.setVisibility(View.GONE);
                    L1=findViewById(R.id.in_progress_orders);
                    L1.setVisibility(View.VISIBLE);
                    LinearLayout L = findViewById(R.id.browse);
                    L.setVisibility(View.GONE);

                    findViewById(R.id.completed_orders).setVisibility(View.GONE);
                    findViewById(R.id.profile).setVisibility(View.GONE);
                    return true;



                }
                if (item.getItemId() == R.id.bottomNavigationProfile) {
                    LinearLayout L1=findViewById(R.id.settings);
                    L1.setVisibility(View.GONE);
                    L1=findViewById(R.id.in_progress_orders);
                    L1.setVisibility(View.GONE);
                    LinearLayout L = findViewById(R.id.browse);
                    L.setVisibility(View.GONE);

                    findViewById(R.id.completed_orders).setVisibility(View.GONE);
                    findViewById(R.id.profile).setVisibility(View.VISIBLE);


                    return true;
                }
                if (item.getItemId() == R.id.bottomNavigationCompleted) {
                    LinearLayout L1=findViewById(R.id.settings);
                    L1.setVisibility(View.GONE);
                    L1=findViewById(R.id.in_progress_orders);
                    L1.setVisibility(View.GONE);
                    LinearLayout L = findViewById(R.id.browse);
                    L.setVisibility(View.GONE);
                    findViewById(R.id.completed_orders).setVisibility(View.VISIBLE);
                    findViewById(R.id.profile).setVisibility(View.GONE);
                    return true;
                }


                return false;
            }
        });



    }

    private void setBannerNews() {
        StorageReference getpresc= FirebaseStorage.getInstance().getReference();
        try {
            prescription= File.createTempFile("images", "jpg");;
            getpresc.child("banner_news/banner.jpg").getFile(prescription).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    ImageView bannerNewsView=findViewById(R.id.bannerNews);
                    bannerNewsView.setScaleType(ImageView.ScaleType.FIT_XY);
                    bannerNewsView.setImageDrawable(Drawable.createFromPath(prescription.getPath()));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void fillInProgressMenu(){
        final Context context=this;
        db=FirebaseFirestore.getInstance();
        db.collection("lab_bookings").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
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
                                startActivity(new Intent(dashboard_lab.this,lab_order_in_progress.class)
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
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(map, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
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
    void inflateProfile(){
        nameTV=findViewById(R.id.profile_name);
        emailTV=findViewById(R.id.profile_email);
        ratingBar=findViewById(R.id.rating_bar);
        usernameTV=findViewById(R.id.profile_username);
        phoneTV=findViewById(R.id.profile_phone);
        db=FirebaseFirestore.getInstance();
        db.collection("users").document(mAuth.getCurrentUser().getEmail()).get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                nameTV.setText("Name: "+documentSnapshot.getString("Full Name"));
                emailTV.setText("Email: "+mAuth.getCurrentUser().getEmail());
                ratingBar.setRating(Long.parseLong(documentSnapshot.getString("rating")));
                usernameTV.setText("Username: "+documentSnapshot.getString("Username"));
                phoneTV.setText("Phone: "+documentSnapshot.getString("Phone Number"));
            }
        });
    }
    void inflateCompletedOrders(){

        db.collection("lab_orders_completed").document(mAuth.getCurrentUser().getEmail()).get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                findViewById(R.id.progress_bar_done).setVisibility(View.GONE);
                if (!documentSnapshot.getString("count").equals("0")) {
                    TextView tv=findViewById(R.id.no_orders_done);
                    tv.setVisibility(View.GONE);
                    for (int i = 1; i <= Integer.parseInt(documentSnapshot.getString("count")); i++) {
                        LayoutInflater l = LayoutInflater.from(dashboard_lab.this);
                        View v = l.inflate(R.layout.in_progress_orders_resource, null);
                        LinearLayout parent = (LinearLayout) findViewById(R.id.orders_container_done);
                        parent.addView(v);
                        String stringText = "Order ID: " + documentSnapshot.getString("id" + i);
                        TextView textView = findViewById(R.id.tv_customer);
                        textView.setText(stringText);
                        textView.setId(Integer.parseInt(Integer.toString(TVID) + i));
                        textView = findViewById(R.id.email_hidden);
                        textView.setText(documentSnapshot.getString("id" + i));
                        textView.setId(Integer.parseInt(Integer.toString(EMAILID) + i));
                        MaterialButton mButton = findViewById(R.id.details);
                        mButton.setId(Integer.parseInt(Integer.toString(BUTTONID) + i));
                        mButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TextView t = findViewById(Integer.parseInt(
                                        EMAILID + String.valueOf(Integer.toString(v.getId())
                                                .charAt(Integer.toString(v.getId()).length() - 1))));
                                startActivity(new Intent(dashboard_lab.this, order_completed_lab_forLab.class)
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