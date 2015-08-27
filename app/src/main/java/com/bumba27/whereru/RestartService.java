package com.bumba27.whereru;


import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

public class RestartService extends Service {
    Context con;
    public RestartService() {
        con = RestartService.this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        stopService(new Intent(this, SimpleVoiceService.class));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(con, SimpleVoiceService.class);
                //stopService(i);
                con.startService(i);
                stopSelf();
            }
        }, 800);

        //iterateInstalledApps();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void iterateInstalledApps()
    {
        PackageManager p = this.getPackageManager();
        final List<PackageInfo> appinstall =
                p.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        for(PackageInfo pInfo:appinstall)
        {
            String[] reqPermission=pInfo.requestedPermissions;
            if(reqPermission!=null)
            {
                for(int i=0;i<reqPermission.length;i++)
                {
                    if (((String)reqPermission[i]).equals("android.permission.RECORD_AUDIO"))
                    {
                        Log.d("TAG", "Package Name: " + pInfo.packageName.toString());
                        if(!pInfo.packageName.toString().equalsIgnoreCase("com.bumba27.whereru")
                                && !pInfo.packageName.toString().equalsIgnoreCase("com.google.android.apps.plus")) {
                            killPackage(pInfo.packageName.toString());
                            break;
                        }
                    }
                }
            }
        }
    }
    private void killPackage(String packageToKill)
    {
        ActivityManager actvityManager =
                (ActivityManager) this.getSystemService( ACTIVITY_SERVICE );
        final List<ActivityManager.RunningAppProcessInfo> procInfos =
                actvityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : procInfos)
        {
            if(runningAppProcessInfo.processName.equals(packageToKill))
            {
                android.os.Process.sendSignal(runningAppProcessInfo.pid,
                        android.os.Process.SIGNAL_KILL);
                actvityManager.killBackgroundProcesses(packageToKill);
            }
        }
    }
}
