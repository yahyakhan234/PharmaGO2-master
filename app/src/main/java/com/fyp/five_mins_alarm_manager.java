package com.fyp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;

public class five_mins_alarm_manager extends BroadcastReceiver {
    FirebaseFirestore db;
    String TAG="5 mins Timer",PID,s;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Alarm time","5 mins Time Over");
        db=FirebaseFirestore.getInstance();
        s=FirebaseAuth.getInstance().getCurrentUser().getEmail();

        final DocumentReference fromPath=db.collection("processed_unaccepted_order").document(s);
        final DocumentReference toPath=db.collection("processed_accepted_order").document(s);

        fromPath.get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                PID=documentSnapshot.getString("PID");
            }
        });
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
                                                        Map<String, Object> m=new HashMap<>();
                                                        m.put("is_accepted",true);
                                                        db.collection("users")
                                                                .document(Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail()))
                                                                .set(m,SetOptions.merge());
                                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                        new GenerateNotif().sendNotificationToSinglePharmacist(PID);
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

