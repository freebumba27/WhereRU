package com.bumba27.whereru;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;


public class SplashScreen extends Activity {

    LinearLayout logo_layout;
    boolean backPressed = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.spash_screen);

        Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        logo_layout = (LinearLayout) findViewById(R.id.linearLayoutLogoLayout);

        logo_layout.setAnimation(slideUp);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!backPressed)
                {
                    Intent myIntent = new Intent(SplashScreen.this, MainActivity.class);
                    finish();
                    startActivity(myIntent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                }
            }
        }, 3500);
    }

    @Override
    public void onBackPressed()
    {
        backPressed = true;
        finish();
    }
}
