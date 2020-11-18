package com.fyp;

import com.fyp.SendNotificationPack.APIService;
import com.fyp.SendNotificationPack.Client;
import com.fyp.classes.online_user;

import java.util.LinkedList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenerateNotif {
    private APIService apiService;
    private LinkedList<online_user> onlineUsers;


    public void sendnotification() {
        get_all_online_users();
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
    }
    public void get_all_online_users(){

        //get all documents in the collection online users, and store in linked list

    }

}
