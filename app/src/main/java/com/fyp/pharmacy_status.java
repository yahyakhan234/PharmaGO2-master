package com.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ALL")
public class pharmacy_status extends AppCompatActivity {

    public static final String RECEIVE_NOTIFICATION="receive_notification";
    public static final String RECEIVE_LIVE_CHAT="receive_live_chat";
    public static final String RECEIVE_ORDERS="receive_orders";
    boolean notif;
    boolean chat;
    boolean orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_status);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        updatesettings();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final SwitchCompat notificationSetting=findViewById(R.id.newnotif);
        final SwitchCompat livechatSetting=findViewById(R.id.accept_livechat);
        final SwitchCompat acceptOrdersSettings=findViewById(R.id.accept_orders);

        db.collection("pharma_users_online")
            .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){

                            notif=documentSnapshot.getBoolean(RECEIVE_NOTIFICATION);
                            orders=documentSnapshot.getBoolean(RECEIVE_ORDERS);
                            chat=documentSnapshot.getBoolean(RECEIVE_LIVE_CHAT);

                            notificationSetting.setChecked(notif);
                            livechatSetting.setChecked(chat);
                            acceptOrdersSettings.setChecked(orders);


                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


        MaterialButton savesetting = findViewById(R.id.update_settings);

        savesetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean notifSet=notificationSetting.isChecked();
                boolean livechatset=livechatSetting.isChecked();
                boolean orderset=acceptOrdersSettings.isChecked();
                Map<String, Object> settings= new HashMap<>();
                settings.put(RECEIVE_NOTIFICATION,notifSet);
                settings.put(RECEIVE_LIVE_CHAT,livechatset);
                settings.put(RECEIVE_ORDERS,orderset);
                db.collection("pharma_users_online")
                        .document(FirebaseAuth.getInstance().getCurrentUser()
                                .getUid()).set(settings, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(pharmacy_status.this, "Settings Saved",
                                Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


                startActivity(new Intent(pharmacy_status.this, dashboard_pharmacy.class));
                finish();
            }

        });
    }
public void updatesettings(){


}
}
