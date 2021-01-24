package com.fyp.classes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fyp.R;
import com.fyp.dashboard;
import com.fyp.generateComplaint;
import com.fyp.order_completed;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import static com.fyp.dashboard_pharmacy.BUTTONID;
import static com.fyp.dashboard_pharmacy.EMAILID;
import static com.fyp.dashboard_pharmacy.TVID;

public class viewComplains extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_complains);
        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        inflateComplains();
        MaterialButton newComplaintButton=findViewById(R.id.new_complain_buttton);
        newComplaintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(viewComplains.this,generateComplaint.class));
            }
        });
    }
    void inflateComplains(){
        db.collection("users_complain_count").document(mAuth.getCurrentUser().getUid()).get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                findViewById(R.id.progress_bar).setVisibility(View.GONE);
                if (!documentSnapshot.getString("count").equals("0")) {
                    TextView tv=findViewById(R.id.no_orders);
                    tv.setVisibility(View.GONE);
                    for (int i = 1; i <= Integer.parseInt(documentSnapshot.getString("count")); i++) {
                        LayoutInflater l = LayoutInflater.from(viewComplains.this);
                        View v = l.inflate(R.layout.in_progress_orders_resource, null);
                        LinearLayout parent = (LinearLayout) findViewById(R.id.orders_container);
                        parent.addView(v);
                        String stringText = "Complain Subject\n" + documentSnapshot.getString(generateComplaint.SUBJECT_KEY + i);
                        TextView textView = findViewById(R.id.tv_customer);
                        textView.setText(stringText);
                        textView.setId(Integer.parseInt(Integer.toString(TVID) + i));
                        textView = findViewById(R.id.email_hidden);
                        textView.setText(documentSnapshot.getString(generateComplaint.COMPLAIN_ID_KEY + i));
                        textView.setId(Integer.parseInt(Integer.toString(EMAILID) + i));
                        MaterialButton mButton = findViewById(R.id.details);
                        mButton.setId(Integer.parseInt(Integer.toString(BUTTONID) + i));
                        mButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TextView t = findViewById(Integer.parseInt(
                                        EMAILID + String.valueOf(Integer.toString(v.getId())
                                                .charAt(Integer.toString(v.getId()).length() - 1))));
                                startActivity(new Intent(viewComplains.this, complaintDetail.class)
                                        .putExtra("complaintID", t.getText()));
                                Log.d("tag", "email:" + t.getText());
                            }
                        });
                    }
                }
                else{
                    TextView textView=findViewById(R.id.no_orders);
                    textView.setVisibility(View.VISIBLE);
                }

            }
        });

    }

}
