package com.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class reset_password extends AppCompatActivity {
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        MaterialButton sendButton=findViewById(R.id.send_email);
        MaterialButton goBackButton=findViewById(R.id.go_back);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final TextInputLayout em=findViewById(R.id.email);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog wait=ProgressDialog.show(reset_password.this,"Processing", "Please Wait!",true);
                email=em.getEditText().getText().toString();
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(reset_password.this,"Oops, looks like you did not Signup",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    wait.dismiss();
                                    new AlertDialog.Builder(reset_password.this)
                                            .setTitle("Email Sent!")
                                            .setMessage("Password reset email has been sent, please reset your password and continue with login")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    finish();
                                                }
                                            })
                                            .setIcon(R.drawable.logo_splash)
                                            .show();

                                    Toast.makeText(reset_password.this,"Email Sent!",Toast.LENGTH_LONG).show();
                                    Log.d("tag", "Email sent.");
                                }
                            }

                        });

            }
        });
    }
}