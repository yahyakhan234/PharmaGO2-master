package com.fyp.SendNotificationPack;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.fyp.Buy_Requests;
import com.fyp.customer_lab_booking;
import com.fyp.customer_order_processed;
import com.fyp.dashboard_pharmacy;
import com.fyp.five_mins_alarm_manager;
import com.fyp.live_chat;
import com.fyp.live_chat_pharma;
import com.fyp.timeRunnerService;
import com.fyp.delivery_alarm_manager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.fyp.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class MyFireBaseMessagingService extends FirebaseMessagingService {
    String title,message;
    //public static final long HOUR_TIME=3600000;       //actual
    //public static final long FIVE_MINUTE_TIME=300000; //actual

    public static final long HOUR_TIME=10000;   //test
    public static final long FIVE_MINUTE_TIME=10000; //test
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        createNotificationChannel();
        SharedPreferences sharedPreferences=getSharedPreferences("LIVE_CHAT_DETAIL", MODE_PRIVATE);
        boolean b=sharedPreferences.getBoolean("IS_ACTIVE",true);
        boolean c=sharedPreferences.getBoolean("HAS_OPENED",true);
        title=remoteMessage.getData().get("Title");
        message=remoteMessage.getData().get("Message");
        if (title.equalsIgnoreCase("live chat")){
            if (b){
                newMessageNotify(message);
            }
        }
        else if (title.equalsIgnoreCase("Accept Order")){
            Log.d("notif","Notification received");
            acceptOrder(title,message);

        }
        else if(title.equalsIgnoreCase("Prepare Order")){
            prepareOrder(title,message);
        }
        else if (title.equalsIgnoreCase("Complete Request")){

            requestComplete(title,message);

        }
        else if (title.equalsIgnoreCase("Order Completed")){
            orderCompletedNotify(title,message);

        }
        else if (title.equalsIgnoreCase("Accept Booking")){


        }
        else if(title.equalsIgnoreCase("Lab Test Complete")){
            labTestCompleteNotify(title,message);
        }
        else {
            addNotification(title, message);
        }

  /*      NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.logo_splash)
                        .setContentTitle(title)
                        .setContentText(message);

        NotificationManager manager =
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
*/
    }

    private void labTestCompleteNotify(String title, String message) {
        Intent intent = new Intent(this, customer_lab_booking.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.logo_splash)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        Notification notification=builder.build();
        notification.flags|=Notification.FLAG_INSISTENT;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, notification);
    }

    private void orderCompletedNotify(String title, String message) {
        Intent intent = new Intent(this, dashboard_pharmacy.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.logo_splash)
                .setContentTitle("New Buy Request")
                .setContentText("New Buy Request From "+message+".Tap To View")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(1, builder.build());


    }

    private void addNotification(String title,String message) {
        // Builds your notification
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, Buy_Requests.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.logo_splash)
                .setContentTitle("New Buy Request")
                .setContentText("New Buy Request From "+message+".Tap To View")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(1, builder.build());


    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "C1";
            String description = "Channel 1";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void acceptOrder(String title,String message) {
        // Builds your notification
        // Create an explicit intent for an Activity in your app
       /* Intent serviceIntent = new Intent(this, timeRunnerService.class);
        ContextCompat.startForegroundService(this, serviceIntent);*/
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        AlarmManager alarmManager1= (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent1 = new Intent(this, delivery_alarm_manager.class);
        Intent intent2=new Intent(this, five_mins_alarm_manager.class);
        PendingIntent pendingIntent2=PendingIntent.getBroadcast(this,1,intent2,0);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this, 2, intent1, 0);
        long time=  (System.currentTimeMillis()+HOUR_TIME);
        long fiveMins=(System.currentTimeMillis()+FIVE_MINUTE_TIME);
        Map<String,Object> map=new HashMap<>();
        map.put("final_time",""+time);
        FirebaseFirestore.getInstance().collection("processed_unaccepted_order")
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .set(map, SetOptions.merge());
        SharedPreferences sharedPreferences
                = getSharedPreferences("order_time", MODE_PRIVATE);
        SharedPreferences.Editor editPrefs;
        editPrefs = sharedPreferences.edit();
        editPrefs.putLong("time", time);
        editPrefs.putLong("fiveMins",fiveMins);
        editPrefs.apply();
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent1);
        alarmManager1.setExact(AlarmManager.RTC_WAKEUP,fiveMins,pendingIntent2);
        Log.d("Alarm time","Alarm Set For: "+((time)/1000));



        Intent intent = new Intent(this, customer_order_processed.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.logo_splash)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        Notification notification=builder.build();
        notification.flags|=Notification.FLAG_INSISTENT;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, notification);
    }
    private void acceptBooking(String title,String message){
        Intent intent = new Intent(this, customer_lab_booking.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.logo_splash)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(1, builder.build());




    }
    private void newMessageNotify(String liveChatID){
        SharedPreferences sharedPreferences=getSharedPreferences("USER_DETAIL", MODE_PRIVATE);
        String  s=sharedPreferences.getString("USER_TYPE","");
        Class cls;
        if (s.equalsIgnoreCase("Patient")){
            cls=live_chat.class;
        }
        else {

            cls= live_chat_pharma.class;

        }
        Intent intent = new Intent(this, cls).putExtra("id",liveChatID);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.logo_splash)
                .setContentTitle("New Message!")
                .setContentText("New Message in Live Chat, Tap to view")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(1, builder.build());


    }

    public void requestComplete(String title, String message){

        Intent intent = new Intent(this, customer_order_processed.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.logo_splash)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        Notification notification=builder.build();
        notification.flags|=Notification.FLAG_INSISTENT;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, notification);

    }

    private void prepareOrder(String title, String message) {
        Intent intent = new Intent(this, dashboard_pharmacy.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.logo_splash)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        Notification notification=builder.build();
        notification.flags|=Notification.FLAG_INSISTENT;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, notification);
    }

}
