package com.fyp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jsibbold.zoomage.ZoomageView;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class pharmacy_prescription_upload extends AppCompatActivity implements OnMapReadyCallback {

    public static final String MED_KEY="med";
    public static final String QTY_KEY="qty";
    public static final String TYPE_KEY="item type";
    public static final String ORDERID_KEY="orderID";
    public static final String TIME_KEY="time";
    public static final String ORDER_TYPE_KEY="type";
    public static final String UID_KEY="UID";
    public static final String PID_KEY="PID";
    public static final String FULL_NAME_KEY="Name";
    public static final String ORDER_COUNT_KEY="Count";
    public static final String PRICE_KEY="price";
    public static final String TOTAL_KEY="total";

    public static final String TAG="Upload presc price";
    public static final String CONTAINER_ID="100";
    public static final String MED_ID="101";
    public static final String QTY_ID="102";
    public static final String TYPE_ID="103";
    public static final String MENU_ID="104";
    public static final String PRICE_ID="105";




    private GoogleMap mMap;
    int counter=1,bill;
    TextInputLayout items;
    String s,orderID,UID;
    File prescription;
    private FirebaseAuth mAuth;
    String[] item_type = new String[] {"Tablets", "Bottles","Other (Please Specify)"};

    MaterialButton addItem,proceed,calculate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_prescription_upload);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        final String email= getIntent().getStringExtra("Email");
        addItem=findViewById(R.id.addItem);
        proceed = findViewById(R.id.search_deliverer);
        addNewItemToView(counter);
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_popup_item,item_type);
        AutoCompleteTextView editTextFilledExposedDropdown =
                findViewById(Integer.parseInt(MENU_ID+counter));

        final TextView total=findViewById(R.id.total);
        editTextFilledExposedDropdown.setAdapter(adapter);
        addItem=findViewById(R.id.addItem);
        db.collection("orders").document(email).get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UID=documentSnapshot.getString("UID");
                orderID=documentSnapshot.getString(ORDERID_KEY);
                StorageReference getpresc= FirebaseStorage.getInstance().getReference();
                try {
                    prescription=File.createTempFile("images", "jpg");;
                    getpresc.child("prescriptions/"+orderID+".jpg").getFile(prescription).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            ProgressBar p=findViewById(R.id.progress_bar);
                            p.setVisibility(View.GONE);
                            ZoomageView prescView=findViewById(R.id.presc_view);
                            prescView.setImageDrawable(Drawable.createFromPath(prescription.getPath()));
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        calculate=findViewById(R.id.calculate_bill);
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bill=0;
                TextInputLayout temp;
                for (int i=1;i<=counter;i++){
                    temp=findViewById(Integer.parseInt(PRICE_ID+i));
                    bill=bill+Integer.parseInt(temp.getEditText().getText().toString());
                }
                total.setText("Amount    "+bill);

            }
        });

        final Map<String, Object> medicine_order = new HashMap<>();

        proceed.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences=getSharedPreferences("USER_DETAIL",MODE_PRIVATE);
                for (int i=1;i<=counter;i++) {
                    items = findViewById(Integer.parseInt(MED_ID + i));
                    medicine_order.put((MED_KEY + i), items.getEditText().getText().toString());

                    items = findViewById(Integer.parseInt(QTY_ID + i));

                    medicine_order.put((QTY_KEY + i), items.getEditText().getText().toString());

                    items = findViewById(Integer.parseInt(TYPE_ID + i));

                    medicine_order.put(TYPE_KEY+i, items.getEditText().getText().toString());

                    items=findViewById(Integer.parseInt(PRICE_ID+i));

                    medicine_order.put(PRICE_KEY+i,items.getEditText().getText().toString());

                }
                medicine_order.put(TOTAL_KEY,total.getText());
                medicine_order.put(PID_KEY,mAuth.getCurrentUser().getUid());
                medicine_order.put("pemail",mAuth.getCurrentUser().getEmail());
                medicine_order.put(ORDER_COUNT_KEY,Integer.toString(counter));
                db.collection("orders")
                        .document(email)
                        .set(medicine_order, SetOptions.merge())
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
                s=getIntent().getStringExtra("Email");
                final DocumentReference fromPath=db.collection("orders").document(s);
                final DocumentReference toPath=db.collection("processed_unaccepted_order").document(s);
                fromPath.get(Source.SERVER).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            orderID=document.getString(customer_custom_request.ORDERID_KEY);
                            if (document.getData() != null) {
                                toPath.set(document.getData())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                                fromPath.delete()
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d(TAG, "DocumentSnapshot successfully deleted!");

                                                                Map<String,Object> map=new HashMap<>();
                                                                map.put("PID",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                                map.put("UID",UID);
                                                                map.put("count","0");
                                                                Map<String,Object> message=new HashMap<>();
                                                                message.put("message","Start Of Conversation");
                                                                message.put("type","user");
                                                                map.put("message0",message);

                                                                db.collection("live_chat")
                                                                        .document("LC"+orderID)
                                                                        .set(map);
                                                                new GenerateNotif().sendNotificationToSingleUser(UID);
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w(TAG, "Error deleting document", e);
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error writing document", e);
                                            }
                                        });
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });

            }});



        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter++;
                addNewItemToView(counter);
            }});

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.locCus);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Customer Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
    }
    void addNewItemToView(int counter)
    {

        LayoutInflater l=LayoutInflater.from(this);
        View v= l.inflate(R.layout.single_med_view_presc,null);
        LinearLayout parent= findViewById(R.id.med_view_inflater);
        parent.addView(v);
        v.setId(Integer.parseInt(CONTAINER_ID+counter));
        items=findViewById(R.id.med);
        items.setId(Integer.parseInt(MED_ID+counter));
        items=findViewById(R.id.qty);
        items.setId(Integer.parseInt(QTY_ID+counter));
        items=findViewById(R.id.type_selector);
        items.setId(Integer.parseInt(TYPE_ID+counter));
        items=findViewById(R.id.price);
        items.setId(Integer.parseInt(PRICE_ID+counter));
        v=findViewById(R.id.filled_exposed_dropdown);
        v.setId(Integer.parseInt(MENU_ID+counter));
        v=(AutoCompleteTextView)v;
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_popup_item,item_type);
        ((AutoCompleteTextView) v).setAdapter(adapter);



    }
}