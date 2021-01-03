package com.fyp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.core.app.NotificationCompat;

public class delivery_alarm_manager extends BroadcastReceiver {
    FirebaseFirestore db;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Alarm time","Time Over");
        db=FirebaseFirestore.getInstance();
        Map<String,Object> m=new HashMap<>();
        m.put("in_time",false);
        db.collection("users")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()
                        .getEmail())).set(m, SetOptions.merge());
       db.collection("processed_accepted_order")
               .document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).get(Source.SERVER)
               .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                   @Override
                   public void onSuccess(DocumentSnapshot documentSnapshot) {
                    new GenerateNotif().timeOverNotify(documentSnapshot.getString("PID"));
                   }
               });



        

    }
}
