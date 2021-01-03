package com.fyp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;
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

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static com.fyp.notificationBuilder.CHANNEL_ID;

public class timeRunnerService extends Service {
    int minutes,seconds,temp=0;
    private String orderID;
    FirebaseFirestore db;
    @Override
    public void onCreate() {
        super.onCreate();
        Map<String, Object> m=new HashMap<>();
        m.put("in_time",true);
        db=FirebaseFirestore.getInstance();
        db.collection("users")
                .document(Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail()))
                .set(m, SetOptions.merge());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, customer_order_processed.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Under Progress Order")
                .setContentText("An order is ongoing, Tap to view details")
                .setSmallIcon(R.drawable.location_icon)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        new CountDownTimer(3600000, 1000) {

            public void onTick(long millisUntilFinished) {
                SharedPreferences sp = getSharedPreferences("USER_DETAIL", MODE_PRIVATE);

                boolean will_write = sp.getBoolean("is_viewing_timer", true);
                if (will_write) {
                    seconds = (int) (millisUntilFinished / 1000);

                        SharedPreferences sharedPreferences
                                = getSharedPreferences("order_time", MODE_PRIVATE);
                        SharedPreferences.Editor editPrefs;
                        editPrefs = sharedPreferences.edit();
                        editPrefs.putString("time", Integer.toString(seconds));
                        editPrefs.apply();
                        minutes = temp;

                }

            }

            public void onFinish() {

                db=FirebaseFirestore.getInstance();

                Map<String, Object> m=new HashMap();
                m.put("in_time",false);
                db.collection("users")
                        .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                        .set(m, SetOptions.merge());

            }
        }.start();


        return START_NOT_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
