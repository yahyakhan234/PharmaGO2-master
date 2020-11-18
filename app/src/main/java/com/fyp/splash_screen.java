package com.fyp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class splash_screen extends AppCompatActivity {

    private static int SPLASH_ANIM=3000;
    Animation animtop,animbottom;
    ImageView splashLogo;
    TextView splashText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);

        animbottom= AnimationUtils.loadAnimation(this,R.anim.bottom_anim);
        animtop=AnimationUtils.loadAnimation(this,R.anim.top_anim);

        splashLogo=  findViewById(R.id.splashlogo);
        splashText= findViewById(R.id.splashtext);

        splashText.setAnimation(animbottom);
        splashLogo.setAnimation(animtop);


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
                    ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(splash_screen.this,pairs);
                    startActivity(intent,options.toBundle());
                    finish();
                }
            }
        },SPLASH_ANIM);
    }
}
