package com.fyp;

import android.util.Log;

import com.fyp.SendNotificationPack.APIService;
import com.fyp.SendNotificationPack.Client;
import com.fyp.classes.online_user;
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

public class GenerateNotif {
    private APIService apiService;
    private LinkedList<online_user> onlineUsers;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("pharma_users_online");


    public void sendnotification() {
        get_all_online_users();
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

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
                    new_user.setUToken(documentSnapshot.getString("UID"));
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

    }


