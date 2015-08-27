package com.bumba27.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bumba27.whereru.ListeningVoiceService;

/**
 * Created by anirban on 03-08-2015.
 */
public class BootReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(ReusableClass.getFromPreference("onOffFlag", context).equalsIgnoreCase("on"))
        {
            context.startService(new Intent(context, ListeningVoiceService.class));
        }
        else if(ReusableClass.getFromPreference("onOffFlag", context).equalsIgnoreCase("off"))
        {
            //Do nothing
        }

    }
}