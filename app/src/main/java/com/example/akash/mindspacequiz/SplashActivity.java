package com.example.akash.mindspacequiz;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {
    ImageView anm;Intent in;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// remove title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        anm = (ImageView) findViewById(R.id.imageView);
        //getSupportActionBar().hide();
        anm.setBackgroundResource(R.drawable.animation_list);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                in = new Intent(getApplicationContext(), StartActivity.class);
                startActivity(in);
                finish();
            }
        }, 3000);


    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        AnimationDrawable frameAnimation =
                (AnimationDrawable) anm.getBackground();
        if (hasFocus) {
            frameAnimation.start();
        } else {
            frameAnimation.stop();
        }
    }
}
