package com.fyp.SendNotificationPack;
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

import com.fyp.Buy_Requests;
import com.fyp.customer_order_processed;
import com.fyp.live_chat;
import com.fyp.live_chat_pharma;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.fyp.R;
public class MyFireBaseMessagingService extends FirebaseMessagingService {
    String title,message;
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
                newMessageNotify();
            }
        }
        else if (title.equalsIgnoreCase("Accept Order")){
            Log.d("notif","Notification received");
            acceptOrder(title,message);

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
    private void newMessageNotify(){
        SharedPreferences sharedPreferences=getSharedPreferences("USER_DETAIL", MODE_PRIVATE);
        String  s=sharedPreferences.getString("USER_TYPE","");
        Class cls;
        if (s.equalsIgnoreCase("pharma")){
            cls=live_chat.class;
        }
        else {

            cls= live_chat_pharma.class;

        }
        Intent intent = new Intent(this, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

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

}
