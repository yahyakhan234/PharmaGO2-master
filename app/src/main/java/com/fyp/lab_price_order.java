package com.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import java.util.HashMap;
import java.util.Map;

public class lab_price_order extends AppCompatActivity {
    public static final String TAG="tag";
    public static final String TOTAL_KEY="total";
    public static final String PID_KEY="PID";
    public static final String PEMAIL_KEY="pemail";
    public static final String DELIVERY_IN_DAYS_KEY="delivery_in_days";
    public static final String TIME_REQUESTED_KEY="timeRequested";
    public static final String DATE_REQUESTED_KEY = "dateRequested";
    public static final String TEST_TYPE_NAME_KEY="testTypeName";
    public static final String TEST_TYPE_KEY="testType";
    public static final String UID_KEY="UID";
    public static final String UEMAIL_KEY="uemail";



    MaterialButton process_order;
    public MaterialButton accept_button;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    String uemail,uname,timeRqst,dateRqst,TypeRqst,totalPrice,deliveryDays,orderID,UID;
    TextView NameTextView,TimeTextView,DateTextView,TestTypeTextView;
    TextInputLayout price,resultDeliverDays;
    ProgressDialog wait;
    Map<String,Object> updateBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_price_order);
        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        wait= ProgressDialog.show(lab_price_order.this,"Loading","Please wait");
        price=findViewById(R.id.price);
        resultDeliverDays=findViewById(R.id.result_time);
        TimeTextView=findViewById(R.id.time_requested);
        DateTextView=findViewById(R.id.date_requested);
        TestTypeTextView=findViewById(R.id.test_type);
        updateBooking=new HashMap<>();
        uemail=getIntent().getStringExtra("uemail");
        db.collection("orders_lab")
                .document(uemail)
                .get(Source.SERVER)
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        timeRqst=TimeTextView.getText()+documentSnapshot.getString(TIME_REQUESTED_KEY);
                        TimeTextView.setText(timeRqst);
                        dateRqst=DateTextView.getText()+documentSnapshot.getString(DATE_REQUESTED_KEY);
                        DateTextView.setText(dateRqst);
                        TypeRqst=TestTypeTextView.getText()+documentSnapshot.getString(TEST_TYPE_NAME_KEY);
                        TestTypeTextView.setText(TypeRqst);
                        UID=documentSnapshot.getString(UID_KEY);
                        wait.dismiss();
                        }
                });

        process_order=findViewById(R.id.process_order);
        process_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalPrice=price.getEditText().getText().toString().trim();
                deliveryDays=resultDeliverDays.getEditText().getText().toString().trim();
                updateBooking.put(PEMAIL_KEY,mAuth.getCurrentUser().getEmail());
                updateBooking.put(TOTAL_KEY,totalPrice);
                updateBooking.put(DELIVERY_IN_DAYS_KEY,deliveryDays);
                updateBooking.put(PID_KEY,mAuth.getCurrentUser().getUid());

                if (totalPrice.equals("")||deliveryDays.equals(""))
                {
                    Toast.makeText(lab_price_order.this,"Enter Price and Days for result to be delivered",Toast.LENGTH_LONG).show();
                }
                else{
                    wait= ProgressDialog.show(lab_price_order.this,"Processing","Processing Your Order, Please Wait");

                    //remove from orders_lab and add to unaccepted booking
                    final DocumentReference fromPath=db.collection("orders_lab").document(uemail);
                    final DocumentReference toPath=db.collection("lab_unaccepted_bookings").document(uemail);
                    fromPath.get(Source.SERVER).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                orderID=document.getString(customer_custom_request.ORDERID_KEY);
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
                                                                    db.collection("lab_unaccepted_bookings").document(uemail)
                                                                            .set(updateBooking, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Map<String,Object> map=new HashMap<>();
                                                                            map.put("PID",mAuth.getCurrentUser().getUid());
                                                                            map.put("UID",UID);
                                                                            map.put("count","0");
                                                                            Map<String,Object> message=new HashMap<>();
                                                                            message.put("message","Start Of Conversation");
                                                                            message.put("type","user");
                                                                            map.put("message0",message);

                                                                            db.collection("live_chat")
                                                                                    .document("LC"+orderID)
                                                                                    .set(map);
                                                                            new GenerateNotif().sendNotificationToUserFromLab(UID);
                                                                            wait.dismiss();
                                                                            new AlertDialog.Builder(lab_price_order.this)
                                                                                    .setTitle("Done!")
                                                                                    .setMessage("Your order has been Processed, You will be notified if customer accepts your bid")
                                                                                    .setIcon(R.drawable.logo_splash)
                                                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                                           finish();
                                                                                        }
                                                                                    })
                                                                                    .show();

                                                                        }
                                                                    });

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


                }
            }

        });

    }
}