package com.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tiper.MaterialSpinner;

public class login_Screen extends AppCompatActivity {

    MaterialSpinner spinner2;
    Button login_button,register1;

    String email;
    String password;
    private static final String[] ITEMS = {"Patient", "Pharmacy", "Laboratory"};
    private FirebaseAuth mAuth;

    private ArrayAdapter<String> adapter;


    @Override
           protected void onCreate(Bundle savedInstanceState) {
        Log.d("onc check", "first on create");
        Toast.makeText(this, "test",Toast.LENGTH_LONG).show();
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                    .detectDiskReads()
//                    .detectDiskWrites()
//                    .detectNetwork()   // or .detectAll() for all detectable problems
//                    .penaltyLog()
//                    .build());


            super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)
        {

            startActivity(new Intent(login_Screen.this, dashboard.class));



        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Log.d("switch check", "check for firebase and reference");



        String selected_option;
        setContentView(R.layout.activity_login_screen);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, ITEMS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final MaterialSpinner materialSpinner = findViewById(R.id.material_spinner);
        final TextInputLayout em = findViewById(R.id.email1);
        final TextInputLayout pw = findViewById(R.id.password1);

        materialSpinner.setAdapter(adapter);
        login_button=findViewById(R.id.login_button);
        register1=findViewById(R.id.register1);
        register1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                startActivity(new Intent(login_Screen.this, signup.class));
                finish();

            } });


        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



        String selected_option= (String) materialSpinner.getSelectedItem();
        String email= em.getEditText().getText().toString();
        String password= pw.getEditText().getText().toString();
        Log.d("switch check", selected_option);
        switch (selected_option) {
            case "Patient": {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(login_Screen.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(login_Screen.this,"Success",Toast.LENGTH_LONG).show();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    startActivity(new Intent(login_Screen.this, dashboard.class));

                                }
                                else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("chk", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(login_Screen.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }



                            }
                        });


                        break;



            }
            case "Laboratory":
            {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(login_Screen.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(login_Screen.this,"Success",Toast.LENGTH_LONG).show();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    startActivity(new Intent(login_Screen.this, dashboard_lab.class));

                                }
                                else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("chk", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(login_Screen.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }



                            }
                        });


                        break;

        }
            case "Pharmacy":
            {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(login_Screen.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(login_Screen.this,"Success",Toast.LENGTH_LONG).show();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    startActivity(new Intent(login_Screen.this, dashboard_pharmacy.class));
                                    finish();
                                }
                                else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("chk", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(login_Screen.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }



                            }
                        });

                        break;

            }


        }

            }

        });



    }

    private void updateUI(Object o) {
    }

}