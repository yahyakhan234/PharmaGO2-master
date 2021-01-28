package com.fyp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.lang.reflect.Type;
import java.sql.Date;
import java.sql.Time;

public class order_request_page_lab extends AppCompatActivity  implements OnMapReadyCallback {
    private GoogleMap mMap;
    public MaterialButton accept_button;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    String uemail,uname,timeRqst,dateRqst,TypeRqst;
    TextView NameTextView,TimeTextView,DateTextView,TestTypeTextView;
    ProgressDialog wait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_request_page_lab);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        wait= ProgressDialog.show(order_request_page_lab.this,"Loading","Please wait");
        NameTextView=findViewById(R.id.name_text_view);
        TimeTextView=findViewById(R.id.time_requested);
        DateTextView=findViewById(R.id.date_requested);
        TestTypeTextView=findViewById(R.id.test_type);
        uemail=getIntent().getStringExtra("uemail");
        db.collection("orders_lab").document(uemail).get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                uname=NameTextView.getText()+documentSnapshot.getString("Name");
                NameTextView.setText(uname);
                timeRqst=TimeTextView.getText()+documentSnapshot.getString("timeRequested");
                TimeTextView.setText(timeRqst);
                dateRqst=DateTextView.getText()+documentSnapshot.getString("dateRequested");
                DateTextView.setText(dateRqst);
                TypeRqst=TestTypeTextView.getText()+documentSnapshot.getString("testType");
                TestTypeTextView.setText(TypeRqst);
                wait.dismiss();
            }
        });
        accept_button=findViewById(R.id.accept_order);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.locCus);
        mapFragment.getMapAsync(this);
        accept_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(order_request_page_lab.this, lab_price_order.class).putExtra("uemail",uemail));
                finish();
            }

        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Customer Location"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(-35,151)).title("Your Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));


    }
}
