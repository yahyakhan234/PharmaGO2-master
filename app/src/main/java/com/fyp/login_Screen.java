package com.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tiper.MaterialSpinner;

public class login_Screen extends AppCompatActivity {

    MaterialSpinner spinner2;
    Button login_button,register1,forgotPasswordButton;

    String email;
    String password;
    boolean is_deleted;
    private static final String[] ITEMS = {"Patient", "Pharmacy", "Laboratory"};
    private FirebaseAuth mAuth;
    String name;
    private ArrayAdapter<String> adapter;

    @Override
           protected void onCreate(Bundle savedInstanceState) {
        Log.d("onc check", "first on create");

//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                    .detectDiskReads()
//                    .detectDiskWrites()
//                    .detectNetwork()   // or .detectAll() for all detectable problems
//                    .penaltyLog()
//                    .build());


            super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Log.d("switch check", "check for firebase and reference");


        String selected_option;
        setContentView(R.layout.activity_login_screen);
        forgotPasswordButton=findViewById(R.id.forgot_password);
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login_Screen.this,reset_password.class));
            }
        });
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, ITEMS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final MaterialSpinner materialSpinner ;
        final TextInputLayout em = findViewById(R.id.email1);
        final TextInputLayout pw = findViewById(R.id.password1);


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



                String email = em.getEditText().getText().toString();
                String password = pw.getEditText().getText().toString();
                boolean reject = false;
                reject = email.trim().isEmpty() || password.trim().isEmpty();
                String selected_option ;
               /* if (selected_option==null){
                    selected_option="";
                }*/
                if (!reject){
                    final ProgressDialog wait=ProgressDialog.show(login_Screen.this,"Processing","Logging You in, Please Wait",true);

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(login_Screen.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information


                                        FirebaseUser user = mAuth.getCurrentUser();

                                        if (user != null) {
                                            FirebaseFirestore.getInstance()
                                                    .collection("users")
                                                    .document(user.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    SharedPreferences sharedPreferences
                                                            = getSharedPreferences("USER_DETAIL", MODE_PRIVATE);

                                                    SharedPreferences.Editor editPrefs;
                                                    editPrefs = sharedPreferences.edit();
                                                    if (documentSnapshot.getBoolean("is_deleted")){
                                                        is_deleted=true;
                                                        mAuth.signOut();
                                                    }
                                                    else {
                                                        is_deleted=false;

                                                    }
                                                    name = documentSnapshot.getString("Full Name");
                                                    String type = documentSnapshot.getString("User Type");
                                                    Log.d("NameTag", ":" + name);

                                                    Log.d("NameTag", "Name:" + name);
                                                    editPrefs.putString("NAME", name);
                                                    editPrefs.putString("USER_TYPE", type);
                                                    editPrefs.commit();
                                                    wait.dismiss();
                                                    if (!is_deleted) {

                                                        Toast.makeText(login_Screen.this, "Success", Toast.LENGTH_LONG).show();
                                                        finish();
                                                        startActivity(new Intent(login_Screen.this, splash_screen.class));
                                                    }
                                                    else {

                                                        Toast.makeText(login_Screen.this, "Oops, looks like your account was deleted or suspended, Contact Support for further details", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });


                                        }
                                        //  startActivity(new Intent(login_Screen.this, dashboard.class));

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("chk", "signInWithEmail:failure", task.getException());
                                        Toast.makeText(login_Screen.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        wait.dismiss();
                                    }


                                }
                            });



               /* switch (selected_option) {
                    case "Patient": {
                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(login_Screen.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information


                                            Toast.makeText(login_Screen.this, "Success", Toast.LENGTH_LONG).show();
                                            FirebaseUser user = mAuth.getCurrentUser();

                                            if (user != null) {
                                                FirebaseFirestore.getInstance()
                                                        .collection("users")
                                                        .document(user.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        SharedPreferences sharedPreferences
                                                                = getSharedPreferences("USER_DETAIL", MODE_PRIVATE);

                                                        SharedPreferences.Editor editPrefs;
                                                        editPrefs = sharedPreferences.edit();

                                                        name = documentSnapshot.getString("Full Name");
                                                        String type = documentSnapshot.getString("User Type");
                                                        Log.d("NameTag", ":" + name);

                                                        Log.d("NameTag", "Name:" + name);
                                                        editPrefs.putString("NAME", name);
                                                        editPrefs.putString("USER_TYPE", type);
                                                        editPrefs.commit();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                    }
                                                });


                                            }
                                            finish();
                                            startActivity(new Intent(login_Screen.this,splash_screen.class));
                                          //  startActivity(new Intent(login_Screen.this, dashboard.class));

                                        } else {
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
                    case "Laboratory": {
                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(login_Screen.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Toast.makeText(login_Screen.this, "Success", Toast.LENGTH_LONG).show();
                                            FirebaseUser user = mAuth.getCurrentUser();

                                            if (user != null) {
                                                FirebaseFirestore.getInstance()
                                                        .collection("users")
                                                        .document(user.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        SharedPreferences sharedPreferences
                                                                = getSharedPreferences("USER_DETAIL", MODE_PRIVATE);

                                                        SharedPreferences.Editor editPrefs;
                                                        editPrefs = sharedPreferences.edit();

                                                        name = documentSnapshot.getString("Full Name");
                                                        String type = documentSnapshot.getString("User Type");
                                                        Log.d("NameTag", ":" + name);

                                                        Log.d("NameTag", "Name:" + name);
                                                        editPrefs.putString("NAME", name);
                                                        editPrefs.putString("USER_TYPE", type);
                                                        editPrefs.commit();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                    }
                                                });
                                            }

                                            startActivity(new Intent(login_Screen.this, dashboard_lab.class));

                                        } else {
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
                    case "Pharmacy": {
                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(login_Screen.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Toast.makeText(login_Screen.this, "Success", Toast.LENGTH_LONG).show();
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            startActivity(new Intent(login_Screen.this, dashboard_pharmacy.class));
                                            finish();
                                        } else {
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
                    default:{
                        Toast.makeText(login_Screen.this,"Please Select A valid option",Toast.LENGTH_SHORT).show();
                    }


                }*/
            }
                else{
                    Toast.makeText(login_Screen.this,"Please Do not leave fields blank",Toast.LENGTH_SHORT).show();

                }

            }

        });



    }

    private void updateUI(Object o) {
    }

}