package com.fyp;

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

import com.fyp.classes.online_user;
import com.fyp.classes.order;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Buy_Requests extends AppCompatActivity {

    private FirebaseFirestore db ;
//    private CollectionReference ordersRef = db.collection("orders");
    public static final String PRESC_UPLOAD_TEXT="Prescription Upload";
    public static final String CUSTOM_ORDER_TEXT="Custom Order";
    order new_order;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy__requests);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /*MaterialButton b2=findViewById(R.id.details);
        MaterialButton b1 = findViewById(R.id.details1);*/
        final Context context=this;

        db=FirebaseFirestore.getInstance();
        //FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
          //      .setPersistenceEnabled(false)
            //    .build();
        //db.setFirestoreSettings(settings);

        db.collection("orders").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                new_order=new order();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    new_order.setEmail(documentSnapshot.getId());
                    new_order.setName(documentSnapshot.getString("Name"));
                    new_order.setUid(documentSnapshot.getString("UID"));
                    new_order.setTime(documentSnapshot.getString("time"));
                    new_order.setOrder_type(documentSnapshot.getString("type"));
                    new_order.setOrder_id(documentSnapshot.getString("orderID"));
                    LayoutInflater l=LayoutInflater.from(context);
                    View v= l.inflate(R.layout.order_resource,null);
                    LinearLayout parent=(LinearLayout) findViewById(R.id.parent_layout_order);
                    parent.addView(v);
                    TextView tv=findViewById(R.id.tv_customer);
                    tv.setId(Integer.parseInt(new_order.getOrder_id()+"1"));
                    tv.setText("Customer "+new_order.getName()+","+new_order.getOrder_type());
                    tv=findViewById(R.id.time_order);
                    tv.setId(Integer.parseInt(new_order.getOrder_id()+"2"));
                    tv.setText(new_order.getTime());
                    tv=findViewById(R.id.email_hidden);
                    tv.setId(Integer.parseInt(new_order.getOrder_id()+"4"));
                    tv.setText(new_order.getEmail());
                    MaterialButton materialButton=findViewById(R.id.details);
                    materialButton.setId(Integer.parseInt(new_order.getOrder_id()+"3"));
                    materialButton.setText("View "+ new_order.getOrder_type());

                    materialButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           /* if(new_order.getOrder_type().equalsIgnoreCase("Custom Order")){
                                startActivity(new Intent(Buy_Requests.this, order_request_page.class)
                                    .putExtra("Email",new_order.getEmail()));
                            }*/
                            Log.d("IDCHECK",Integer.toString(v.getId()));
                            MaterialButton m= (MaterialButton) v;
                            TextView t=findViewById(Integer.parseInt(
                                    (Integer.toString(v.getId()).substring(0,Integer.toString(v.getId()).length()-1)+"4")));

                            if(m.getText().toString().contains("Custom")){
                                startActivity(new Intent(Buy_Requests.this, order_request_page.class)
                                        .putExtra("Email",t.getText()));
                            }
                            if(m.getText().toString().contains("Prescription")){
                                startActivity(new Intent(Buy_Requests.this, pharmacy_prescription_upload.class)
                                .putExtra("Email",t.getText()));

                            }
                        }
                    });
                      Log.d("Token Check",new_order.getEmail()+" "+new_order.getOrder_type());

                }
            }
        });

       /* LayoutInflater l=LayoutInflater.from(this);
        View v= l.inflate(R.layout.order_resource,null);
        LinearLayout parent=(LinearLayout) findViewById(R.id.parent_layout_order);
        parent.addView(v);*/
      /*  b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Buy_Requests.this, order_request_page.class)
                        .putExtra("KEY","value"));
            }

        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Buy_Requests.this, pharmacy_prescription_upload.class));
            }

        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    protected void onPause() {
        super.onPause();
        finish();
    }
}
