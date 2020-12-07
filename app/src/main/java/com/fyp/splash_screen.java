package com.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class splash_screen extends AppCompatActivity {

    private final static int SPLASH_ANIM=2000;
    Animation animtop,animbottom;
    ImageView splashLogo;
    TextView splashText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_splash_screen);

        animbottom= AnimationUtils.loadAnimation(this,R.anim.bottom_anim);
        animtop=AnimationUtils.loadAnimation(this,R.anim.top_anim);

        splashLogo=  findViewById(R.id.splashlogo);
        splashText= findViewById(R.id.splashtext);

        splashText.setAnimation(animbottom);
        splashLogo.setAnimation(animtop);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        final FirebaseUser currentUser = mAuth.getCurrentUser();


      /*  if(currentUser!=null) {
            db.collection("users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String user_type = documentSnapshot.getString(signup.USER_TYPE_KEY);
                              /*  String FullName=sharedPreferences.getString("USER_TYPE","");

                                switch (FullName) {
                                    case "Patient": {

                                        startActivity(new Intent(splash_screen.this, dashboard.class));
                                        finish();
                                        break;

                                    }
                                    case "Laboratory": {


                                    }
                                    case "Pharmacy": {

                                        startActivity(new Intent(splash_screen.this, dashboard_pharmacy.class));
                                        finish();
                                        break;

                                    }

                                }*/
                                            /*notif=documentSnapshot.getBoolean(RECEIVE_NOTIFICATION);
                                            orders=documentSnapshot.getBoolean(RECEIVE_ORDERS);
                                            chat=documentSnapshot.getBoolean(RECEIVE_LIVE_CHAT);*/


                       /*     }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

        }*/






            new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                //Call next screen
                Intent intent=new Intent(splash_screen.this,login_Screen.class);
                // Attach all the elements those you want to animate in design
                Pair[] pairs=new Pair[2];pairs[0]=new Pair<View, String>(splashLogo,"logo_image");
                pairs[1]=new Pair<View, String>(splashText,"logo_text");
                //wrap the call in API level 21 or higher
                if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.LOLLIPOP)
                {
                    if(currentUser==null) {
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(splash_screen.this, pairs);
                        startActivity(intent, options.toBundle());

                        finish();
                    }
                    else
                    {
                        final SharedPreferences sharedPreferences=getSharedPreferences("USER_DETAIL", MODE_PRIVATE);

                        String FullName=sharedPreferences.getString("USER_TYPE","");

                        switch (FullName) {
                            case "Patient": {

                                startActivity(new Intent(splash_screen.this, dashboard.class));
                                finish();
                                break;

                            }
                            case "Laboratory": {


                            }
                            case "Pharmacy": {

                                startActivity(new Intent(splash_screen.this, dashboard_pharmacy.class));
                                finish();
                                break;

                            }

                        }


                    }
                }
            }
        },SPLASH_ANIM);
    }
}
