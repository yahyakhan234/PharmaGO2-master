package com.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tiper.MaterialSpinner;

import java.util.HashMap;
import java.util.Map;

public class signup extends AppCompatActivity {

    Button register_button;
    private static final String[] ITEMS = {"Patient", "Pharmacy", "Laboratory"};
    private FirebaseAuth mAuth;
    public static final String USERNAME_KEY = "Username";
    public static final String EMAIL_KEY = "Email";
    public static final String USER_TYPE_KEY = "User Type";
    public static final String FULL_NAME_KEY = "Full Name";
    public static final String PHONE_KEY = "Phone Number";
    public String username;


    private ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_signup);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, ITEMS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final MaterialSpinner materialSpinner = findViewById(R.id.user_type);
        materialSpinner.setAdapter(adapter);
        final TextInputLayout unm=findViewById(R.id.username);
        final TextInputLayout em = findViewById(R.id.email);
        final TextInputLayout pw = findViewById(R.id.password);
        final TextInputLayout name = findViewById(R.id.full_name);
        final TextInputLayout pnum = findViewById(R.id.phone);


        register_button=findViewById(R.id.register);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = em.getEditText().getText().toString();
                String password = pw.getEditText().getText().toString();
                 username = unm.getEditText().getText().toString();
                String full_name= name.getEditText().getText().toString();
                String phone = pnum.getEditText().getText().toString();
                String selected_option= (String) materialSpinner.getSelectedItem();
                final Map<String, Object> user = new HashMap<>();
                user.put(USERNAME_KEY, username);
                user.put(FULL_NAME_KEY, full_name);
                user.put(EMAIL_KEY, email);
                user.put(PHONE_KEY, phone);
                user.put(USER_TYPE_KEY,selected_option);
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    db.collection("users").document(username)
                                            .set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("tag", "Added Successfully");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("tag", "Error adding document", e);
                                                }
                                            });
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("chk", "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(signup.this, "User Signed Up. Login Please",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("chk", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(signup.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }

                                // ...
                            }
                        });
            }
        });
    }

}