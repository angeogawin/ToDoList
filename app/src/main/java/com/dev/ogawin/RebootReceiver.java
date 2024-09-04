package com.dev.ogawin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import android.util.Log;

import com.dev.ogawin.databasehelper.MyDatabaseHelper;
import com.dev.ogawin.model.Rappel;

import java.util.List;

//This receiver is called when the phone is rebooted.
//It allows to re -do the planification of alarms that has been interupted
// when phone was turned off
public class RebootReceiver extends BroadcastReceiver {
    private String TAG = "RebootReceiver";
    MyDatabaseHelper db ;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Log.d(TAG, "action: "+intent.getAction());
        db = new MyDatabaseHelper(context.getApplicationContext());

        List<Rappel>  rappels= db.getAllRappels();

        //We keep only rappels having attribute 'rappelPlannedAlarmTimeInMillis' different from 0 and null.

        for(Rappel r:rappels)
        {
            if(r.getRappelPlannedAlarmTimeInMillis() !=null && r.getRappelPlannedAlarmTimeInMillis() != 0L)
            {
                //After a reboot, we re-do the planification of registered alarmed
                ActiviteCreerRappel.creerUpdateAlarmePourNotification(context, r.getRappelTitle(),r,r.getRappelDate(),r.getRappelTime(),r.getRappelRecurrence());

            }
        }



    }


}