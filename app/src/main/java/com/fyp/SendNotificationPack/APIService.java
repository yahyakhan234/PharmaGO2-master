package com.fyp.SendNotificationPack;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {@Headers(
        {
                "Content-Type:application/json",
                "Authorization:key=AAAATJ5Sf4I:APA91bGLu0D8LX98eWWHQDq-OPjJmUHbuXtZfiBd-swN7GvNQ3yxugTk4T1hRJ9HCw1l4OnXbSflvbGkH6H2F96SzdTv3G-6st0OfQDF32-1HW9ryFyccRoLqVHtCIt7b-U2nObI9MQS" // Your server
                }
)
@POST("fcm/send")
Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}