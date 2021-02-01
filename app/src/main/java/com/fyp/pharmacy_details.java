package com.fyp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import org.w3c.dom.Text;

public class pharmacy_details extends AppCompatActivity  implements OnMapReadyCallback {
    private GoogleMap mMap;
    String email,uid;
    TextView emailTV,phoneTV,nameTV,ratingTV;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_details);
        db=FirebaseFirestore.getInstance();
        email=getIntent().getStringExtra("email");
        uid=getIntent().getStringExtra("uid");
        setdetails();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.locCus);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        LatLng sydney = new LatLng(-34, 151);
        db.collection("pharma_users_online").document(uid).get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.getBoolean("is_located")!=null){
                    //noinspection ConstantConditions
                    if (documentSnapshot.getBoolean("is_located")) {

                        String lat=documentSnapshot.getString("lat");
                        String lng=documentSnapshot.getString("long");
                        LatLng sydney = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                        mMap.addMarker(new MarkerOptions().position(sydney).title("Location")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
                    }

            }
        }});

    }
    void setdetails(){
        emailTV=findViewById(R.id.email);
        phoneTV=findViewById(R.id.phone);
        ratingTV=findViewById(R.id.rating);
        nameTV=findViewById(R.id.pharmacy_name);
        db.collection("users").document(email).get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                emailTV.setText(documentSnapshot.getString(signup.EMAIL_KEY));
                phoneTV.setText(documentSnapshot.getString(signup.PHONE_KEY));
                ratingTV.setText(documentSnapshot.getString("rating")+"/5");
                nameTV.setText(documentSnapshot.getString(signup.FULL_NAME_KEY));

            }
        });

    }
}