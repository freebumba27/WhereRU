package com.bumba27.whereru;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumba27.utils.ReusableClass;


public class SplashScreen extends Activity {

    LinearLayout logo_layout;
    boolean backPressed = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spash_screen);

        if(ReusableClass.getFromPreference("appStartingDate", this).equalsIgnoreCase(""))
        {
            ReusableClass.saveInPreference("appStartingDate", System.currentTimeMillis() + "", this);
        }


        Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        logo_layout = (LinearLayout) findViewById(R.id.linearLayoutLogoLayout);

        logo_layout.setAnimation(slideUp);

        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime - Long.parseLong(ReusableClass.getFromPreference("appStartingDate", this));
        Log.v("TAG", "firstTime: " + Long.parseLong(ReusableClass.getFromPreference("appStartingDate", this)));
        Log.v("TAG", "currentTime: " + currentTime);
        Log.v("TAG", "Time Diff: " + timeDiff);

        //Starting date ~ 1424165427370 = 17-02-2015 15:00:00 India Time
        //Trial Period  ~ 1000*60*60*24*4 = 345600000 mili sec (4 days)

        if (timeDiff > 1000*60*60*24*60)
        {
            //Finish App
            //finish();
            Toast.makeText(this, getString(R.string.trial_period_expaired), Toast.LENGTH_LONG).show();
        }
        else {
            //Do the normal things

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!backPressed) {
                        Intent myIntent = new Intent(SplashScreen.this, MainActivity.class);
                        finish();
                        startActivity(myIntent);
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    }
                }
            }, 3500);
        }
    }

    @Override
    public void onBackPressed()
    {
        backPressed = true;
        finish();
    }
}
