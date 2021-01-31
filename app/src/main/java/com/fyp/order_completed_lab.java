package com.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class order_completed_lab extends AppCompatActivity {
    public static final String MED_ID = "100";
    public static final String PRICE_ID = "101";
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String orderID, s;
    TextView med, price;
    RatingBar ratingBar;
    int rate;
    TextView timeRequestedTV,dateRequestedTV,totalTV,testName,resultTV;

    MaterialButton setRating, setRatingDisabled, downloadResultButton;

    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_completed_lab);
        timeRequestedTV=findViewById(R.id.time_requested);
        testName=findViewById(R.id.test_type);
        dateRequestedTV=findViewById(R.id.date_requested);
        totalTV=findViewById(R.id.total);
        ratingBar = findViewById(R.id.rating_bar);
        orderID = getIntent().getStringExtra("orderID");
        setRating = findViewById(R.id.submit_rating);
        setRatingDisabled = findViewById(R.id.submit_rating_disabled);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        downloadResultButton = findViewById(R.id.download_result);
        inflate_menu();
        downloadResultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download();
            }
        });
        setRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("rating", Double.toString(ratingBar.getRating()));
                rate = (int) ratingBar.getRating();
                new AlertDialog.Builder(order_completed_lab.this)
                        .setIcon(R.drawable.logo_splash)
                        .setMessage("Are you sure you want to give rating of " + rate)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Map<String, Object> m = new HashMap<>();
                                m.put("lab_rating", "" + rate);
                                m.put("lab_rated", true);
                                db.collection("lab_tests_completed").document(orderID).set(m, SetOptions.merge())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                db.collection("lab_tests_completed").document(orderID).get(Source.SERVER)
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                db.collection("users")
                                                                        .document(Objects.requireNonNull(documentSnapshot.getString("uemail")))
                                                                        .get(Source.SERVER)
                                                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                int totalRatingSum = Integer.parseInt(Objects.requireNonNull(documentSnapshot.getString("total_rating")));
                                                                                int ratingCount = Integer.parseInt(Objects.requireNonNull(documentSnapshot.getString("rating_count")));
                                                                                totalRatingSum = totalRatingSum + rate;
                                                                                ratingCount++;
                                                                                int actual_rating = (totalRatingSum * 5) / (ratingCount * 5);
                                                                                Map<String, Object> map = new HashMap<>();
                                                                                map.put("total_rating", "" + totalRatingSum);
                                                                                map.put("rating_count", "" + ratingCount);
                                                                                map.put("rating", "" + actual_rating);
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
                        .setNegativeButton("No", null)
                        .show();


            }
        });
    }

    void inflate_menu() {
        db.collection("lab_tests_completed").document(orderID).get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                testName.setText(documentSnapshot.getString(lab_price_order.TEST_TYPE_NAME_KEY));
                timeRequestedTV.setText(documentSnapshot.getString(lab_price_order.TIME_REQUESTED_KEY));
                dateRequestedTV.setText(documentSnapshot.getString(lab_price_order.DATE_REQUESTED_KEY));
                totalTV.setText(documentSnapshot.getString(lab_price_order.TOTAL_KEY));
                orderID=documentSnapshot.getString("orderID");


                if (documentSnapshot.getBoolean("lab_rated") != null) {
                    //noinspection ConstantConditions
                    if (documentSnapshot.getBoolean("lab_rated")) {
                        ratingBar.setRating(Integer.parseInt(Objects.requireNonNull(documentSnapshot.getString("lab_rating"))));
                        ratingBar.setIsIndicator(true);
                    } else {
                        setRatingDisabled.setVisibility(View.GONE);
                        setRating.setVisibility(View.VISIBLE);
                    }
                }


            }
        });

    }
    public void download()
    {

        storageReference= FirebaseStorage.getInstance().getReference();
        StorageReference ref = storageReference.child("lab_results").child(orderID);

        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url=uri.toString();
                Toast.makeText(order_completed_lab.this,"Download Started",Toast.LENGTH_LONG).show();
                downloadFile(order_completed_lab.this,orderID,".pdf",DIRECTORY_DOWNLOADS,url);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {


            }
        });


    }
    public void downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url) {


        DownloadManager downloadmanager = (DownloadManager) context.
                getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);

        downloadmanager.enqueue(request);
    }
}