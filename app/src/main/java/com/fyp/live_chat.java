package com.fyp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;

public class live_chat extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_chat);
        ChatMessage chatMessage=new ChatMessage("Hello",00, ChatMessage.Type.RECEIVED);

        chatMessage.setMessage("Hello");
        chatMessage.setType(ChatMessage.Type.RECEIVED);
        ChatView chatView = (ChatView) findViewById(R.id.chat_view);
        chatView.addMessage(chatMessage);
        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener(){
            @Override
            public boolean sendMessage(ChatMessage chatMessage1){

                return true;
            }
        });
    }
}