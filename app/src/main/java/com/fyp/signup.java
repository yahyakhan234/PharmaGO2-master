package com.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.tiper.MaterialSpinner;

import java.util.HashMap;
import java.util.Map;

import static com.fyp.customer_lab_booking.HAS_TEST_BOOKED_KEY;
import static com.fyp.customer_lab_booking.IS_TEST_ACCEPTED_KEY;
import static com.fyp.generateComplaint.COUNT_KEY;

public class signup extends AppCompatActivity {

    private static final String COMPLETE_REQUESTED_KEY = "complete_requested";
    private static final String IN_TIME_KEY ="in_time" ;
    private static final String IS_ACCEPTED_KEY = "is_accepted";
    private static final String IS_ORDERING_KEY ="is_ordering" ;
    private static final String LAB_COMPLETE_REQUESTED = "lab_complete_requested";
    private static final String RATING_KEY = "rating";
    private static final String STATUS_KEY = "status";
    private static final String ORDERS_KEY = "orders";
    public static final String RATING_COUNT_KEY="rating_count";
    public static final String TOTAL_RATING_KEY="total_rating";
    private static final String IS_DELETED_KEY ="is_deleted" ;
    Button register_button;
    private static final String[] ITEMS = {"Patient", "Pharmacy", "Laboratory"};
    private FirebaseAuth mAuth;
    public static final String USERNAME_KEY = "Username";
    public static final String EMAIL_KEY = "Email";
    public static final String USER_TYPE_KEY = "User Type";
    public static final String FULL_NAME_KEY = "Full Name";
    public static final String PHONE_KEY = "Phone Number";
    public String username;
    ProgressDialog wait;


    private ArrayAdapter<String> adapter;
    String selected_option;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_signup);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, ITEMS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Button goToSignin=findViewById(R.id.go_to_signin);
        goToSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(signup.this,login_Screen.class));
            }
        });
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
                wait = ProgressDialog.show(signup.this, "Processing", "Signing You up, Please Wait");
                email = em.getEditText().getText().toString();
                String password = pw.getEditText().getText().toString();
                username = unm.getEditText().getText().toString();
                String full_name = name.getEditText().getText().toString();
                String phone = pnum.getEditText().getText().toString();
                selected_option = (String) materialSpinner.getSelectedItem();
                if (hasNull(username, full_name, email, password, phone)) {
                    wait.dismiss();
                    Toast.makeText(signup.this, "Please Fill All desired Fields", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (isValidEmailAddress(email)) {
                        if (password.matches(".*\\d.*")&&password.length()>7) {
                            final Map<String, Object> user = new HashMap<>();
                            user.put(USERNAME_KEY, username);
                            user.put(FULL_NAME_KEY, full_name);
                            user.put(EMAIL_KEY, email);
                            user.put(PHONE_KEY, phone);
                            user.put(USER_TYPE_KEY, selected_option);
                            user.put(RATING_KEY, "5");
                            user.put(RATING_COUNT_KEY, "0");
                            user.put(TOTAL_RATING_KEY, "0");
                            user.put(IS_DELETED_KEY, false);
                            if (selected_option.equals("Patient")) {
                                user.put(COMPLETE_REQUESTED_KEY, false);
                                user.put(HAS_TEST_BOOKED_KEY, false);
                                user.put(IN_TIME_KEY, false);
                                user.put(IS_ACCEPTED_KEY, false);
                                user.put(IS_ORDERING_KEY, false);
                                user.put(IS_TEST_ACCEPTED_KEY, false);
                                user.put(LAB_COMPLETE_REQUESTED, false);
                                user.put(STATUS_KEY, "searching");
                            } else if (selected_option.equals("Pharmacy")) {
                                user.put(ORDERS_KEY, "0");
                            }
                            mAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                db.collection("users").document(email)
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
                                                final FirebaseUser user = mAuth.getCurrentUser();
                                                final Map<String, Object> map = new HashMap<>();
                                                map.put(COUNT_KEY, "0");
                                                db.collection("users_complain_count").document(user.getUid()).set(map, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                    }
                                                });
                                                switch (selected_option) {
                                                    case "Laboratory": {
                                                        db.collection("lab_bookings").document(user.getEmail()).set(map, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {

                                                                db.collection("lab_orders_completed").document(user.getEmail()).set(map, SetOptions.merge())
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                wait.dismiss();
                                                                                Toast.makeText(signup.this, "User Signed Up. Login Please",
                                                                                        Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                            }
                                                        });
                                                        break;
                                                    }
                                                    case "Pharmacy": {
                                                        db.collection("pharma_orders").document(user.getUid()).set(map, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                db.collection("pharma_orders_completed").document(user.getEmail()).set(map, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        wait.dismiss();
                                                                        Toast.makeText(signup.this, "User Signed Up. Login Please",
                                                                                Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                    break;
                                                    case "Patient": {
                                                        db.collection("user_lab_orders_completed").document(user.getUid()).set(map, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                db.collection("user_orders_completed").document(user.getUid()).set(map, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        wait.dismiss();
                                                                        Toast.makeText(signup.this, "User Signed Up. Login Please",
                                                                                Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }
                                                        });
                                                        break;
                                                    }
                                                }
                                            } else {
                                                // If sign in fails, display a message to the user.
                                                wait.dismiss();
                                                Log.w("chk", "createUserWithEmail:failure", task.getException());
                                                Toast.makeText(signup.this, "Oops, Something went wrong, Are you sure those are correct inputs?.",
                                                        Toast.LENGTH_LONG).show();

                                            }

                                            // ...
                                        }
                                    });
                        }
                        else {
                            wait.dismiss();
                            Toast.makeText(signup.this,"Your Password Must contain a Numeric and have more than 8 characters",Toast.LENGTH_LONG).show();

                        }
                    }

                    else {
                        wait.dismiss();
                        Toast.makeText(signup.this,"Please Enter A valid Email",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
    public boolean hasNull(String username,String name,String email,String pw,String phone){
            if (username.trim().equals("")||name.trim().equals("")||email.trim().equals("")||pw.trim().equals("")||phone.trim().equals(""))
                return true;
            else
                return false;
    }

}