package com.dev.ogawin;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.dev.ogawin.databasehelper.MyDatabaseHelper;
import com.dev.ogawin.model.Rappel;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.NOTIFICATION_SERVICE;

/*
    Le but de cette classe est de déclencher la notification de rappel
    sur le téléphone.C'est le service AlarmManager qui la contacte
    quand l'heure/la date prévue arrive.
 */
public class BroadcastReceiverForNotifications extends BroadcastReceiver {
    MyDatabaseHelper db ;
    @Override
    public void onReceive(Context context, Intent intent) {
        //android.os.Debug.waitForDebugger();
        // Preparefdgsfb intent which is triggered if the
        // notification is selected
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_IMMUTABLE);
        Intent intentNotif = new Intent(context, ActiviteTout.class);

// Build notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification noti;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "channel_id";
            CharSequence channelName = "Channel Name";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(notificationChannel);

            noti = new Notification.Builder(context.getApplicationContext(), channelId)
                    .setContentTitle(intentNotif.getStringExtra("title"))
                    .setSmallIcon(R.drawable.icons8_liste_100)
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setContentIntent(pIntent)
                    .build();
        } else {
            noti = new Notification.Builder(context.getApplicationContext())
                    .setContentTitle(intentNotif.getStringExtra("title"))
                    .setSmallIcon(R.drawable.icons8_liste_100)
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setContentIntent(pIntent)
                    .build();
        }

        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        noti.defaults |= Notification.DEFAULT_SOUND;
        noti.defaults |= Notification.DEFAULT_VIBRATE;

        int idRappel = intentNotif.getIntExtra("id", 0);
        notificationManager.notify(idRappel, noti);
        //On planifie la prochaine alarme
        String recurrence = intent.getStringExtra("recurrence");
        if(recurrence != null && !"".equals(recurrence) && !"Jamais".equals(recurrence))
        {
            planifierProchaineAlarme(context,intent,recurrence);

        }
    }

    public void planifierProchaineAlarme(Context context,Intent intent,String recurrence)
    {
        //Prochaine execution dans "recurrence" temps

        int uniteDeTemps =0;
        int nbUnites = 0;
        if(recurrence.contains("Horaire"))
        {
            uniteDeTemps = Calendar.HOUR_OF_DAY;
        }
        else if(recurrence.contains("jours"))
        {
            uniteDeTemps = Calendar.DAY_OF_MONTH;
        }
        else if(recurrence.contains("semaines"))
        {
            uniteDeTemps = Calendar.WEEK_OF_YEAR;
        }
        else if(recurrence.contains("mois"))
        {
            uniteDeTemps = Calendar.MONTH;
        }
        else if(recurrence.contains("ans"))
        {
            uniteDeTemps = Calendar.YEAR;
        }

        if("Horaire".equals(recurrence) || "Tous les jours".equals(recurrence) || "Toutes les semaines".equals(recurrence) || "Tous les mois".equals(recurrence) || "Tous les ans".equals(recurrence))
        {
            nbUnites = 1;
        }
        else if("Toutes les 2 semaines".equals(recurrence))
        {
            nbUnites = 2;
        }
        else if("Tous les 3 mois".equals(recurrence))
        {
            nbUnites = 3;
        }
        else if("Tous les 6 mois".equals(recurrence))
        {
            nbUnites = 6;
        }

        Calendar calendar = (Calendar)intent.getSerializableExtra("calendar");
        if(calendar != null)
        {
            //on cherche la prochaine date du rappel actuel(puisqu'il est récurrent)
            //cette date dépend du moment actuel(où la notification est reçue)
            //particulièrement utile si la notification s'affiche bien longtemps après l'heure de l'alarme
            //prévue.C'est le cas si le telephone est éteint à l'heure précise où la notification devait
            //s'afficher.Au démarrage, la notif s'affichera et ainsi, on planifiera correctement
            //la prochaine alarme.
            Calendar calendar_now = Calendar.getInstance();
            calendar_now.setTimeInMillis(System.currentTimeMillis());

            do {
                calendar.set(uniteDeTemps, calendar.get(uniteDeTemps)+nbUnites);

            }while(calendar.getTimeInMillis()<calendar_now.getTimeInMillis());

            //Si on arrive ici, c'est que nous avons trouvé la prochaine date du rappel

            AlarmManager alarmMgr;
            PendingIntent alarmIntent;

            alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            //on actualise la valeur du calendrier et on met à jour l'extra de l'intent
            //Pas besoin de mettre à jour l'extra 'recurrence' vu qu'il n'a pas changé
            intent.putExtra("calendar",calendar);

            int idRappel=0;
            idRappel = intent.getIntExtra("id",0);

            alarmIntent = PendingIntent.getBroadcast(context, idRappel, intent, PendingIntent.FLAG_IMMUTABLE);


            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),alarmIntent);



        }
    }
}
