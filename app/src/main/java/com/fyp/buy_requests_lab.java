package com.fyp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fyp.classes.order;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class buy_requests_lab extends AppCompatActivity {

    order new_order;
    private FirebaseFirestore db ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_requests_lab);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        db=FirebaseFirestore.getInstance();
        db.collection("orders_lab").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                new_order=new order();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    if (documentSnapshot.getBoolean("print") == null) {
                        new_order.setEmail(documentSnapshot.getId());
                        new_order.setName(documentSnapshot.getString("Name"));
                        new_order.setUid(documentSnapshot.getString("UID"));
                        new_order.setTime(documentSnapshot.getString("timestamp"));
                        new_order.setOrder_id(documentSnapshot.getString("orderID"));
                        new_order.setOrder_type(documentSnapshot.getString(lab_price_order.TEST_TYPE_NAME_KEY));
                        new_order.setEmail(documentSnapshot.getString(lab_price_order.UEMAIL_KEY));
                        LayoutInflater l = LayoutInflater.from(buy_requests_lab.this);
                        View v = l.inflate(R.layout.order_resource, null);
                        LinearLayout parent = (LinearLayout) findViewById(R.id.parent_layout_order);
                        parent.addView(v);
                        TextView tv = findViewById(R.id.tv_customer);
                        tv.setId(Integer.parseInt(new_order.getOrder_id() + "1"));
                        tv.setText("Customer " + new_order.getName() + "," + new_order.getOrder_type());
                        tv = findViewById(R.id.time_order);
                        tv.setId(Integer.parseInt(new_order.getOrder_id() + "2"));
                        tv.setText(new_order.getTime());
                        tv = findViewById(R.id.email_hidden);
                        tv.setId(Integer.parseInt(new_order.getOrder_id() + "4"));
                        tv.setText(new_order.getEmail());
                        MaterialButton materialButton = findViewById(R.id.details);
                        materialButton.setId(Integer.parseInt(new_order.getOrder_id() + "3"));
                        materialButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                           /* if(new_order.getOrder_type().equalsIgnoreCase("Custom Order")){
                                startActivity(new Intent(Buy_Requests.this, order_request_page.class)
                                    .putExtra("Email",new_order.getEmail()));
                            }*/
                                Log.d("IDCHECK", Integer.toString(v.getId()));
                                MaterialButton m = (MaterialButton) v;
                                TextView t = findViewById(Integer.parseInt(
                                        (Integer.toString(v.getId()).substring(0, Integer.toString(v.getId()).length() - 1) + "4")));
                                startActivity(new Intent(buy_requests_lab.this, order_request_page_lab.class)
                                        .putExtra("uemail", t.getText()));


                            }
                        });
                        Log.d("Token Check", new_order.getEmail() + " " + new_order.getOrder_type());

                    }
                }
            }
        });

    }
}