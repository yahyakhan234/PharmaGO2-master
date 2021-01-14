package com.fyp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class live_chat_pharma extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String m1,m2,lastmessage;
    private int Count=1,pCount,a;
    private MaterialButton SendButton;
    private int updateCount;
    private boolean sendNotification;
    private static final String MESSAGE_KEY="message";
    String liveChatID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateCount=Count=1;
        setContentView(R.layout.activity_live_chat_pharma);
        liveChatID=getIntent().getStringExtra("id");
        SendButton=findViewById(R.id.send_message);
        final ChatMessage chatMessage=new ChatMessage("",00, ChatMessage.Type.RECEIVED);
        chatMessage.setMessage("Start Of Conversation");
        chatMessage.setType(ChatMessage.Type.RECEIVED);
        final ChatView chatView = (ChatView) findViewById(R.id.chat_view);
        chatView.addMessage(chatMessage);
        //  setChat();
        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener(){
            @Override
            public boolean sendMessage(ChatMessage chatMessage1){

                Map<String,Object> map = new HashMap<>();
                Map<String,Object> message=new HashMap<>();
                message.put("message",chatMessage1.getMessage());
                message.put("type","pharma");
                map.put("message"+3,message);
                db= FirebaseFirestore.getInstance();
                db.collection("live_chat").document(liveChatID).set(map, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("CHECK","SUCKCES STORE");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("CHECK", "?? "+e);
                    }
                });
                return true;
            }
        });
        SendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!chatView.getTypedMessage().isEmpty()){
                    db.collection("live_chat").document(liveChatID).get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            a=Integer.parseInt(documentSnapshot.getString("count"));
                            a++;
                            chatMessage.setMessage(chatView.getTypedMessage());
                            chatMessage.setType(ChatMessage.Type.SENT);
                            //chatView.addMessage(chatMessage);
                            chatView.getInputEditText().setText("");
                            db=FirebaseFirestore.getInstance();
                            Map<String,Object> map = new HashMap<>();
                            Map<String,Object> message=new HashMap<>();
                            message.put("message",chatMessage.getMessage());
                            message.put("type","pharma");
                            map.put("message"+a,message);
                            map.put("count",Integer.toString(a));
                            db.collection("live_chat").document(liveChatID).set(map,SetOptions.merge());
                            GenerateNotif generateNotif=new GenerateNotif();
                            generateNotif.sendNewMessageNotification(documentSnapshot.getString("UID"),liveChatID);
                        }
                    });
                    //Make count dynamic, setonchangelistner in OnResume()

                }
            }
        });

    }
    protected void onResume(){
        super.onResume();

        db=FirebaseFirestore.getInstance();

        db.collection("live_chat")
                .document(liveChatID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        ChatView chatView=findViewById(R.id.chat_view);
                        if(updateCount!=Integer.parseInt(documentSnapshot.getString("count"))){
                            assert documentSnapshot != null;
                            Map<String,Object> map=null;
                            Map<String,Object> message = null;
                            map = Objects.requireNonNull(documentSnapshot.getData());
                            for (int i=updateCount;i<=Integer.parseInt(documentSnapshot.getString("count"));i++){

                                message= (Map<String, Object>) map.get(MESSAGE_KEY+i);
                                m1=message.get("message").toString();
                                m2=message.get("type").toString();
                                if (m1.equalsIgnoreCase(lastmessage)){
                                    continue;

                                }
                                ChatMessage chatMessage;

                                switch (m2){
                                    case "user":{
                                        chatMessage=new ChatMessage(m1,00, ChatMessage.Type.RECEIVED);
                                        break;
                                    }
                                    case "pharma":{
                                        chatMessage=new ChatMessage(m1,00, ChatMessage.Type.SENT);
                                        break;
                                    }

                                    default:
                                        throw new IllegalStateException("Unexpected value: " + m2);
                                }
                                chatView.addMessage(chatMessage);
                                lastmessage=chatMessage.getMessage();
                            }

                            updateCount=Integer.parseInt(documentSnapshot.getString("count"));
                        }

                    }

                });
        sendNotification=false;
        SharedPreferences sharedPreferences =getSharedPreferences("LIVE_CHAT_DETAIL",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("IS_ACTIVE",sendNotification);
        editor.commit();
        Log.d("lifecycle","Resumed");
    }
    protected void onPause() {
        super.onPause();
        sendNotification=true;
        SharedPreferences sharedPreferences =getSharedPreferences("LIVE_CHAT_DETAIL",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("IS_ACTIVE",sendNotification);
        Log.d("lifecycle","Paused");
        editor.commit();
    }
}