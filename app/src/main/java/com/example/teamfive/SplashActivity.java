package com.example.teamfive;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class SplashActivity  extends AppCompatActivity {

    Animation anim_FadeIn;
    Animation anim_ball;
    Animation anim_flower;
    ConstraintLayout constraintLayout;
    ImageView lcklockImageView;
    ImageView oImageView;
    ImageView flowerImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        constraintLayout = findViewById(R.id.constraintLayout);
        lcklockImageView = findViewById(R.id.lock_lck);
        oImageView = findViewById(R.id.lock_o);
        flowerImageView = findViewById(R.id.lock_o2);

        anim_FadeIn = AnimationUtils.loadAnimation(this, R.anim.anim_splash_fadein);
        anim_ball = AnimationUtils.loadAnimation(this, R.anim.anim_splash_ball);
        anim_flower = AnimationUtils.loadAnimation(this, R.anim.anim_splash_flower);

        anim_FadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        lcklockImageView.startAnimation(anim_FadeIn);
        oImageView.startAnimation(anim_ball);
        flowerImageView.startAnimation(anim_flower);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}