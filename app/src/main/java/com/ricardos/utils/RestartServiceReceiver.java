package com.ricardos.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.ricardos.whereru.SimpleVoiceService;

public class RestartServiceReceiver extends BroadcastReceiver {
    public RestartServiceReceiver() {
    }

    private static final String TAG = "RestartServiceReceiver";

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.e(TAG, "onReceive YouWillNeverKillMe");

        if(ReusableClass.getFromPreference("RunAfterOneMin", context).equalsIgnoreCase(""))
        {
            context.startService(new Intent(context.getApplicationContext(), SimpleVoiceService.class));
        }
        else if(ReusableClass.getFromPreference("RunAfterOneMin", context).equalsIgnoreCase("true"))
        {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    context.startService(new Intent(context.getApplicationContext(), SimpleVoiceService.class));
                }
            }, 1000*60);
        }
    }
}
