package com.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.Objects;


public class order_request_page extends AppCompatActivity
        implements OnMapReadyCallback {

    public static final String CONTAINER_ID="100";
    public static final String MED_ID="101";
    public static final String QTY_ID="102";
    String s;

    private GoogleMap mMap;
    public MaterialButton accept_button;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    int count;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_request_page);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

         context=this;
        accept_button=findViewById(R.id.accept_order);
        s=getIntent().getStringExtra("Email");
        db.collection("orders").document(s).get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                     count=Integer.parseInt(Objects.requireNonNull(documentSnapshot.getString(customer_custom_request.ORDER_COUNT_KEY)));
                    for (int i=1;i<=count;i++) {
                        LayoutInflater l = LayoutInflater.from(context);
                        View v = l.inflate(R.layout.med_detail_inflatable, null);
                        LinearLayout parent = findViewById(R.id.inflate_container);
                        parent.addView(v);
                        v.setId(Integer.parseInt(CONTAINER_ID + i));
                        v=findViewById(R.id.med);
                        TextView vi= (TextView) v;
                        vi.setText(documentSnapshot.getString("med"+i));
                        v.setId(Integer.parseInt(MED_ID+i));
                        v=findViewById(R.id.quantity);
                        v.setId(Integer.parseInt(QTY_ID+i));
                        vi= (TextView) v;
                        String s=documentSnapshot.getString("qty"+i) + " " + documentSnapshot.getString("item type"+i);
                        vi.setText(s);
                    }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.locCus);
        mapFragment.getMapAsync(this);
        accept_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(order_request_page.this, pharmacy_price_order.class).putExtra("Email",s));
                finish();
            }

        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Customer Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
    }
}
