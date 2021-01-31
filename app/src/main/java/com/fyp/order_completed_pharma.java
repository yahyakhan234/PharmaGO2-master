package com.fyp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class order_completed_pharma extends AppCompatActivity {

    public static final String MED_ID="100";
    public static final String PRICE_ID="101";
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String orderID,s;
    TextView med,price;
    RatingBar ratingBar;
    int rate;
    MaterialButton setRating,setRatingDisabled,viewPrescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_completed_pharma);
        ratingBar=findViewById(R.id.rating_bar);
        orderID=getIntent().getStringExtra("orderID");
        setRating=findViewById(R.id.submit_rating);
        setRatingDisabled=findViewById(R.id.submit_rating_disabled);
        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        viewPrescription=findViewById(R.id.view_prescription_button);
        inflate_menu();
        viewPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(order_completed_pharma.this, view_prescription.class).putExtra("orderID",orderID));
            }
        });
        setRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("rating",Double.toString(ratingBar.getRating()));
                rate= (int) ratingBar.getRating();
                new AlertDialog.Builder(order_completed_pharma.this)
                        .setIcon(R.drawable.logo_splash)
                        .setMessage("Are you sure you want to give rating of "+rate)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Map<String,Object> m=new HashMap<>();
                                m.put("user_rating",""+rate);
                                m.put("user_rated",true);
                                db.collection("orders_completed").document(orderID).set(m, SetOptions.merge())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                db.collection("orders_completed").document(orderID).get(Source.SERVER)
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                if (documentSnapshot.getBoolean(upload_prescription.HAS_PRESCRIPTION_KEY)){

                                                                    findViewById(R.id.view_prescription_button).setVisibility(View.VISIBLE);

                                                                }
                                                                db.collection("users")
                                                                        .document(Objects.requireNonNull(documentSnapshot.getString("uemail")))
                                                                        .get(Source.SERVER)
                                                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                int totalRatingSum= Integer.parseInt(Objects.requireNonNull(documentSnapshot.getString("total_rating")));
                                                                                int ratingCount= Integer.parseInt(Objects.requireNonNull(documentSnapshot.getString("rating_count")));
                                                                                totalRatingSum=totalRatingSum+rate;
                                                                                ratingCount++;
                                                                                int actual_rating=(totalRatingSum*5)/(ratingCount*5);
                                                                                Map<String,Object> map=new HashMap<>();
                                                                                map.put("total_rating",""+totalRatingSum);
                                                                                map.put("rating_count",""+ratingCount);
                                                                                map.put("rating",""+actual_rating);
                                                                                db.collection("users").document(documentSnapshot.getId()).set(map, SetOptions.merge())
                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void aVoid) {
                                                                                                setRating.setVisibility(View.GONE);
                                                                                                setRatingDisabled.setVisibility(View.VISIBLE);
                                                                                            }
                                                                                        });
                                                                            }
                                                                        });
                                                            }
                                                        });
                                            }
                                        });

                            }
                        })
                        .setNegativeButton("No",null)
                        .show();


            }
        });
    }
    void inflate_menu(){
        db.collection("orders_completed").document(orderID).get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.getBoolean(upload_prescription.HAS_PRESCRIPTION_KEY)){
                    viewPrescription.setVisibility(View.VISIBLE);
                }
                else{
                    viewPrescription.setVisibility(View.GONE);
                }

                if (documentSnapshot.getBoolean("user_rated")!=null) {
                    //noinspection ConstantConditions
                    if (documentSnapshot.getBoolean("user_rated"))
                    {
                        ratingBar.setRating(Integer.parseInt(Objects.requireNonNull(documentSnapshot.getString("user_rating"))));
                        ratingBar.setIsIndicator(true);
                    }
                    else {
                        setRatingDisabled.setVisibility(View.GONE);
                        setRating.setVisibility(View.VISIBLE);
                    }
                }
                int count = Integer.parseInt(Objects.requireNonNull(documentSnapshot.getString(customer_custom_request.ORDER_COUNT_KEY)));
                for (int i = 1; i <= count; i++) {

                    LayoutInflater l = LayoutInflater.from(order_completed_pharma.this);
                    View v = l.inflate(R.layout.med_processed_price_resource, null);
                    LinearLayout parent = findViewById(R.id.parent_inflater);
                    parent.addView(v);
                    med = findViewById(R.id.med);
                    med.setId(Integer.parseInt(MED_ID + i));
                    s = documentSnapshot.getString(customer_custom_request.MED_KEY + i)
                            + " " + documentSnapshot.getString(customer_custom_request.TYPE_KEY + i)
                            + " " + documentSnapshot.getString(customer_custom_request.QTY_KEY + i);
                    med.setText(s);
                    price = findViewById(R.id.price);
                    price.setId(Integer.parseInt(PRICE_ID + i));
                    s = "Price: " + documentSnapshot.getString(pharmacy_price_order.PRICE_KEY + i);
                    price.setText(s);

                }
                med = findViewById(R.id.total);
                med.setText(documentSnapshot.getString(pharmacy_price_order.TOTAL_KEY));

            }
        });

    }
}