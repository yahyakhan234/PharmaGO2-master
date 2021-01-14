package com.fyp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static java.security.AccessController.getContext;

public class customer_custom_request extends AppCompatActivity {

    MaterialButton addItem,proceed;

    int counter=1;
    TextInputLayout items;
    //Keys
    public static final String MED_KEY="med";
    public static final String QTY_KEY="qty";
    public static final String TYPE_KEY="item type";
    public static final String ORDERID_KEY="orderID";
    public static final String TIME_KEY="time";
    public static final String ORDER_TYPE_KEY="type";
    public static final String UID_KEY="UID";
    public static final String FULL_NAME_KEY="Name";
    public static final String ORDER_COUNT_KEY="Count";

    //ID constants
    public static final String CONTAINER_ID="100";
    public static final String MED_ID="101";
    public static final String QTY_ID="102";
    public static final String TYPE_ID="103";
    public static final String MENU_ID="104";

    private FirebaseAuth mAuth;
    //View[] vi=new View[3];
    String[] item_type = new String[] {"Tablets", "Bottles","Other (Please Specify)"};
    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_custom_request);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        final String email= currentUser.getEmail();
        addItem=findViewById(R.id.addItem);
        proceed = findViewById(R.id.search_deliverer);
        addNewItemToView(counter);
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_popup_item,item_type);
        AutoCompleteTextView editTextFilledExposedDropdown =
                findViewById(Integer.parseInt(MENU_ID+counter));
        editTextFilledExposedDropdown.setAdapter(adapter);
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
                   }

                    medicine_order.put(UID_KEY,mAuth.getCurrentUser().getUid());
                    medicine_order.put("uemail",mAuth.getCurrentUser().getEmail());
                    medicine_order.put(FULL_NAME_KEY,sharedPreferences.getString("NAME",""));
                    medicine_order.put(ORDER_COUNT_KEY,Integer.toString(counter));
                @SuppressLint("SimpleDateFormat")
                DateFormat df = new SimpleDateFormat("H:mm a");
                String date = df.format(Calendar.getInstance().getTime());
                    medicine_order.put(TIME_KEY,date);
                    medicine_order.put(ORDER_TYPE_KEY,"Custom Order");
                    db.collection("entityCount").document("TotalOrders")
                            .get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String s=documentSnapshot.getString("latest_order_number");
                            s=Integer.toString(Integer.parseInt(s)+1);
                            medicine_order.put(ORDERID_KEY,s);
                            db.collection("orders").document(email)
                                    .set(medicine_order).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    GenerateNotif g=new GenerateNotif();
                                    g.sendNotificationToAllUsers();
                                    Map<String,Object> setorder=new HashMap<>();
                                    setorder.put("is_ordering",true);
                                  //  setorder.put("progress",searching_deliverer.class);
                                    setorder.put("is_accepted",false);
                                    db.collection("users").document(email).set(setorder,SetOptions.merge());
                                    Log.d("tag", "Added Successfully");

                                    startActivity(new Intent(customer_custom_request.this, searching_deliverer.class));
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("tag", "Error adding document", e);
                                        }
                                    });

                        }
                    });





            }});



        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter++;
                addNewItemToView(counter);
                /*
                    items = (TextInputLayout) vi[counter];
                    items.setVisibility(View.VISIBLE);
                    if(counter!=4)
                    counter++;*/
            }});

    }
    void addNewItemToView(int counter)
    {

        LayoutInflater l=LayoutInflater.from(this);
        View v= l.inflate(R.layout.single_med_view,null);
        LinearLayout parent= findViewById(R.id.med_view_inflater);
        parent.addView(v);
        v.setId(Integer.parseInt(CONTAINER_ID+counter));
        items=findViewById(R.id.med);
        items.setId(Integer.parseInt(MED_ID+counter));
        items=findViewById(R.id.qty);
        items.setId(Integer.parseInt(QTY_ID+counter));
        items=findViewById(R.id.type_selector);
        items.setId(Integer.parseInt(TYPE_ID+counter));
        v=findViewById(R.id.filled_exposed_dropdown);
        v.setId(Integer.parseInt(MENU_ID+counter));
        v=(AutoCompleteTextView)v;
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_popup_item,item_type);
        ((AutoCompleteTextView) v).setAdapter(adapter);



    }
}