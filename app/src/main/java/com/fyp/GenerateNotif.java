package com.fyp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.fyp.SendNotificationPack.APIService;
import com.fyp.SendNotificationPack.Client;
import com.fyp.SendNotificationPack.Data;
import com.fyp.SendNotificationPack.MyResponse;
import com.fyp.SendNotificationPack.NotificationSender;
import com.fyp.classes.online_user;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class GenerateNotif {
    private APIService apiService;
    private LinkedList<online_user> onlineUsers=new LinkedList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("pharma_users_online");
    public void sendNotificationToAllUsers() {

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);




        usersRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                online_user new_user=new online_user();

                SharedPreferences sharedPreferences;



                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    // Note note = documentSnapshot.toObject(Note.class);
                    new_user.setUID(documentSnapshot.getId());
                    new_user.setUToken(documentSnapshot.getString("token"));
                    onlineUsers.add(new_user);
                    // String userID=documentSnapshot.getId();
                    //String userToken=documentSnapshot.getString("UID");

                    sendNotifications(new_user.getUToken(),"Hello", "Customer");
                    Log.d("Token Check",new_user.getUID()+" "+new_user.getUToken());
                /*    note.setDocumentId(documentSnapshot.getId());
                    String documentId = note.getDocumentId();
                    String title = note.getTitle();
                    String description = note.getDescription();
                    data += "ID: " + documentId
                            + "\nTitle: " + title + "\nDescription: " + description + "\n\n";*/
                }
            }
        });








    }
    public void get_all_online_users(){

        usersRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                online_user new_user=new online_user();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                   // Note note = documentSnapshot.toObject(Note.class);
                    new_user.setUID(documentSnapshot.getId());
                    new_user.setUToken(documentSnapshot.getString("token"));
                    onlineUsers.add(new_user);
                   // String userID=documentSnapshot.getId();
                    //String userToken=documentSnapshot.getString("UID");
                    Log.d("Token Check",new_user.getUID()+" "+new_user.getUToken());
                /*    note.setDocumentId(documentSnapshot.getId());
                    String documentId = note.getDocumentId();
                    String title = note.getTitle();
                    String description = note.getDescription();
                    data += "ID: " + documentId
                            + "\nTitle: " + title + "\nDescription: " + description + "\n\n";*/
                }
            }
        });
    }


        //get all documents in the collection online users, and store in linked list
        public void sendNotifications(String usertoken, String title, String message) {
            Data data = new Data(title, message);
            NotificationSender sender = new NotificationSender(data,
                    usertoken);
            apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
                @Override
                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                    if (response.code() == 200) {
                        if (response.body().success != 1) {
                            Log.d("Failed","Notification Sending Failed");
                            //Toast.makeText(SendNotif.this, "Failed ",
                              //      Toast.LENGTH_LONG);
                        }
                    }
                }
                @Override
                public void onFailure(Call<MyResponse> call, Throwable t) {
                }
            });
        }
    }


