package com.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class pharmacy_price_order extends AppCompatActivity {
    public static final String CONTAINER_ID="100";
    public static final String MED_ID="101";
    public static final String PRICE_ID="102";
    public static final String PRICE_KEY="price";
    public static final String TOTAL_KEY="total";
    public static final String TAG="move doc";
    private FirebaseFirestore db;
    int count,bill;
    String UID;

    Context context;

    MaterialButton process_order,calculate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_price_order);
        process_order=findViewById(R.id.process_order);
        String s=getIntent().getStringExtra("Email");
        Log.d("extra","no??:"+ s);
        //context=this;

        db=FirebaseFirestore.getInstance();
        db.collection("orders").document(s).get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                count=Integer.parseInt(Objects.requireNonNull(documentSnapshot.getString(customer_custom_request.ORDER_COUNT_KEY)));
                for (int i=1;i<=count;i++) {
                    LayoutInflater l = LayoutInflater.from(pharmacy_price_order.this);
                    View v = l.inflate(R.layout.pricing_resource, null);
                    LinearLayout parent = findViewById(R.id.inflate_container);
                    parent.addView(v);
                    v.setId(Integer.parseInt(CONTAINER_ID + i));
                    v=findViewById(R.id.med);
                    TextView vi= (TextView) v;
                    String s=documentSnapshot.getString("med"+i)+" "
                            +documentSnapshot.getString("qty"+i) + " "
                            + documentSnapshot.getString("item type"+i);
                    vi.setText(s);
                    v.setId(Integer.parseInt(MED_ID+i));
                    TextInputLayout textInputLayout=findViewById(R.id.price);
                    textInputLayout.setId(Integer.parseInt(PRICE_ID+i));
                }
                UID=documentSnapshot.getString("UID");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        calculate=findViewById(R.id.calculate_bill);
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView total=findViewById(R.id.total);
                bill=0;
                TextInputLayout temp;

                for (int i=1;i<=count;i++){
                temp=findViewById(Integer.parseInt(PRICE_ID+i));
                bill=bill+Integer.parseInt(temp.getEditText().getText().toString());
                }
                total.setText("Amount    "+bill);

            }
        });
        process_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextInputLayout temp;
                String s;

                Map<String, Object> medicine_order = new HashMap<>();
                for (int i=1;i<=count;i++){
                    temp=findViewById(Integer.parseInt(PRICE_ID+i));
                    s=temp.getEditText().getText().toString();
                    medicine_order.put(PRICE_KEY+i,s);
                }

                s=getIntent().getStringExtra("Email");
                medicine_order.put(TOTAL_KEY,Integer.toString(bill));
                medicine_order.put("PID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                db.collection("orders")
                        .document(s)
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
                            if (document != null) {
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

               // startActivity(new Intent(pharmacy_price_order.this, pharmacy_order_in_progress.class));
                //finish();
            }

        });

    }
}