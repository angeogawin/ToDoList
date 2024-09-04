package com.dev.ogawin;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.dev.ogawin.swipemenulistview.BaseSwipListAdapter;

import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;


import androidx.core.content.ContextCompat;

import com.dev.ogawin.swipemenulistview.SwipeMenuListView;
import com.dev.ogawin.databasehelper.MyDatabaseHelper;
import com.dev.ogawin.model.Rappel;


import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MyListAdapter extends BaseSwipListAdapter {

    private final Context context;

    MyDatabaseHelper db;

    private List<Rappel> rappelList;
    public MyListAdapter(Context context,  List<Rappel> rappelList) {


        this.context=context;
        this.rappelList = rappelList;

        db = MyDatabaseHelper.getInstanceDb(context);
    }

    public void remove(int position) {
        db.deleteRappel((Rappel) this.getItem(position));

        rappelList.remove(position);
        rappelList = sortByDate(rappelList);
        notifyDataSetChanged();
    }
    public void refreshForAll() {
        rappelList.clear();
        rappelList.addAll(db.getAllRappels());

        rappelList = sortByDate(rappelList);
        notifyDataSetChanged();
    }

    public void refreshForToday() {
        rappelList.clear();
        rappelList.addAll(db.getTodayRappels());

        rappelList = sortByDate(rappelList);
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }


    @Override
    public int getItemViewType(int position) {

        return 0;
    }

    @Override
    public int getCount() {
        return rappelList.size();
    }

    @Override
    public Object getItem(int position) {
        return rappelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = View.inflate(parent.getContext(),
                    R.layout.mylist, null);
            new ViewHolder(view);
        }
        ViewHolder holder = (ViewHolder) view.getTag();


        String priorite = rappelList.get(position).getRappelPriorite();
        String symbolePriorite = "";
        if("Faible".equals(priorite))
        {
            symbolePriorite = "! ";
        }
        else if("Moyenne".equals(priorite))
        {
            symbolePriorite = "!! ";
        }
        else if("Elevée".equals(priorite))
        {
            symbolePriorite = "!!! ";
        }

        holder.exclamationMark.setText(symbolePriorite);

        holder.titleEdittext.setText(rappelList.get(position).getRappelTitle());
        holder.subtitleText.setText(String.valueOf(parseRappelDate(rappelList.get(position).getRappelDate())) + " " +
                String.valueOf(parseRappelTime(rappelList.get(position).getRappelTime()))  +
                parseRappelRecurrence(rappelList.get(position).getRappelRecurrence()));

        //Selon que la date planifiée pour le rappel soit ou non passée, la mettre en rouge
        mettreDateEnRouge(holder,rappelList.get(position).getRappelDate(),rappelList.get(position).getRappelTime());



        holder.titleEdittext.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_ENTER) { //Whenever you got user click enter. Get text in edittext and check it equal test1. If it's true do your code in listenerevent of button3

                    Rappel rappel = rappelList.get(position);
                    rappel.setRappelTitle(holder.titleEdittext.getText().toString());
                    db.updateRappel(rappel);

                    holder.titleEdittext.clearFocus();

                    return  true;
                }
                return false;
            }

            });

        holder.titleRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //On met a jour la date/temps du rappel dans la bdd
                mettreAJourBddDateEtTempsPourRappelRecurrent(rappelList.get(position));
                //deselectionner le  radio button
                holder.titleRadio.setChecked(false);
                /*final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 2000ms


                    }
                }, 2000);*/

            }
        });


        return view;

    };


    public void mettreDateEnRouge(ViewHolder holder,LocalDate date,LocalTime time)
    {
        ColorStateList oldColors =  holder.subtitleText.getTextColors(); //save original colors
        // Set the alarm to specified date
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.clear(Calendar.MILLISECOND);

        if(date != null)
        {
            calendar.set(Calendar.YEAR, date.getYear());
            calendar.set(Calendar.MONTH, date.getMonthOfYear()-1);
            calendar.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());

            calendar.clear(Calendar.HOUR_OF_DAY); //reset hour to zero
            calendar.clear(Calendar.MINUTE);
            calendar.clear(Calendar.SECOND);

        }
        if(time != null)
        {
            calendar.set(Calendar.HOUR_OF_DAY, time.getHourOfDay());
            calendar.set(Calendar.MINUTE, time.getMinuteOfHour());
            calendar.set(Calendar.SECOND, time.getSecondOfMinute());
        }

        Calendar calendar_now = Calendar.getInstance();
        calendar_now.setTimeInMillis(System.currentTimeMillis());

        if(calendar_now.getTimeInMillis() >= calendar.getTimeInMillis())
        {
            //La date du rappel vient d etre atteinte, mettre en rouge le sous-titre du rappel
            holder.subtitleText.setTextColor(Color.RED);
        }
        else
        {
            //La date du rappel vient d etre atteinte, mettre en rouge le sous-titre du rappel
            int defaultSubtitleTextColor = ContextCompat.getColor(context, R.color.gray_background);
            holder.subtitleText.setTextColor(defaultSubtitleTextColor);
        }
    }


    /*
        Met à jour pour un rappel donné, la date et le temps spécifié dans la bdd, si ce rappel est récurrent
        Fonction appelée lorsque l'utilisateur clic sur le radio button indiquant que la tache est complétée
        On met donc à jour pour ce rappel la prochaine date d'exécution
     */
    public void mettreAJourBddDateEtTempsPourRappelRecurrent(Rappel rappel)
    {
        //Prochaine execution dans "recurrence" temps

        int uniteDeTemps =0;
        int nbUnites = 0;

        String recurrence = rappel.getRappelRecurrence();

        if("Jamais".equals(recurrence))
        {

            db.deleteRappel(rappel);
            rappelList.remove(rappel);

            ActiviteCreerRappel.cancelAlarmeNotification(context,rappel);
            //Plus tard, créer dans la bdd une colonne 'completed' qui permettra de filtrer la vue sur les taches non completées
            //Quand on clickera sur le radio button:
            //-pour une tache non recurrente: son attribut 'completed' passera a true
            //-pour une tache recurrente: on créera un rappel de type non-recurrent,avec les memes attributs que
            //la tache recurrente, sauf que son attribut 'completed' passera a true
        }
        else
        {
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

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());

            calendar.clear(Calendar.MILLISECOND);

            LocalDate date = rappel.getRappelDate();
            if(date != null)
            {
                calendar.set(Calendar.YEAR, date.getYear());
                calendar.set(Calendar.MONTH, date.getMonthOfYear()-1);
                calendar.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());

                calendar.clear(Calendar.HOUR_OF_DAY); //reset hour to zero
                calendar.clear(Calendar.MINUTE);
                calendar.clear(Calendar.SECOND);

            }
            LocalTime time = rappel.getRappelTime();
            if(rappel.getRappelTime() != null)
            {
                calendar.set(Calendar.HOUR_OF_DAY, time.getHourOfDay());
                calendar.set(Calendar.MINUTE, time.getMinuteOfHour());
                calendar.set(Calendar.SECOND, time.getSecondOfMinute());
            }


            //on cherche la prochaine date du rappel actuel(puisqu'il est récurrent)
            //cette date dépend du moment actuel(où l'utilisateur clic sur valider la tache)
            Calendar calendar_now = Calendar.getInstance();
            calendar_now.setTimeInMillis(System.currentTimeMillis());

            do {
                calendar.set(uniteDeTemps, calendar.get(uniteDeTemps)+nbUnites);


            }while(calendar.getTimeInMillis()<calendar_now.getTimeInMillis());

            //Si on arrive ici, c'est que nous avons trouvé la prochaine date du rappel
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            LocalDate rappelDate =LocalDate.parse(format.format(new Date(calendar.getTime().getTime())));
            rappel.setRappelDate(rappelDate);

            LocalTime oldtime = new LocalTime(calendar.getTimeInMillis());
            String hour = String.valueOf(oldtime.getHourOfDay());
            String minute = String.valueOf(oldtime.getMinuteOfHour());
            DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm");

            LocalTime rappelTime =fmt.parseLocalTime(hour+ ":" + minute);
            rappel.setRappelTime(rappelTime);

            db.updateRappel(rappel);
            //On actualise la prochaine alarme pour la notif.
            ActiviteCreerRappel.creerUpdateAlarmePourNotification(context, rappel.getRappelTitle(),rappel,rappelDate,rappelTime,recurrence);




        }
        //Update view
        rappelList = sortByDate(rappelList);
        notifyDataSetChanged();


    }
    public String parseRappelTime(LocalTime rappelTime)
    {
        String resultat ="";
        if(rappelTime != null)
        {
            resultat = rappelTime.toString("HH:mm");
        }


        return resultat;
    }

    public String parseRappelDate(LocalDate rappelDate)
    {
        String resultat ="";
        if(rappelDate != null)
        {
            resultat = rappelDate.toString("dd/MM/yyyy");

            LocalDate now = LocalDate.now();
            String nowS = now.toString("dd/MM/yyyy");
            String nowPlusOneDay = now.plusDays(1).toString("dd/MM/yyyy");
            String nowPlusTwoDay = now.plusDays(2).toString("dd/MM/yyyy");
            String nowMinusOneDay = now.minusDays(1).toString("dd/MM/yyyy");
            String nowMinusTwoDay = now.minusDays(2).toString("dd/MM/yyyy");

            if(nowS.equals(resultat))
            {
                //Rappel is today
                resultat = context.getResources().getString(R.string.aujourdhui);
            }
            else if(nowPlusOneDay.equals(resultat))
            {
                //Rappel is tomorrow
                resultat = context.getResources().getString(R.string.demain);
            }
            else if(nowMinusOneDay.equals(resultat))
            {
                //Rappel was yesterday
                resultat= context.getResources().getString(R.string.hier);
            }
            else if(nowMinusTwoDay.equals(resultat))
            {
                //Rappel was before yesterday
                resultat= context.getResources().getString(R.string.avanthier);
            }
            else if(nowPlusTwoDay.equals(resultat))
            {
                //Rappel is after tomorrow
                resultat= context.getResources().getString(R.string.apresdemain);
            }
        }



        return resultat;
    }

    public String parseRappelRecurrence(String rappelRecurrence)
    {
        String resultat ="";

        if(!rappelRecurrence.contains("Jamais"))
        {
            resultat = ", " + rappelRecurrence;
        }

        return resultat;
    }

    //Tri des rappels du plus proche au plus lointain
    public static List<Rappel> sortByDate(List<Rappel> liste)
    {

        //Tri par ordre de recence
        Comparator<Rappel> compareByDate = new Comparator<Rappel>() {
            @Override
            public int compare(Rappel o1, Rappel o2) {
                long tps1 = 0;
                long tps2 = 0;
                if(o1.getRappelDate() != null)
                {
                    tps1 = o1.getRappelDate().toDate().getTime();
                    if(o1.getRappelTime() != null)
                    {
                        tps1 += 1000 * (o1.getRappelTime().getHourOfDay() * 3600 + o1.getRappelTime().getMinuteOfHour() * 60 + o1.getRappelTime().getSecondOfMinute());
                    }

                }
                if(o2.getRappelDate() != null)
                {
                    tps2 = o2.getRappelDate().toDate().getTime() ;
                    if(o2.getRappelTime() != null)
                    {
                        tps2 += 1000 * (o2.getRappelTime().getHourOfDay() * 3600 + o2.getRappelTime().getMinuteOfHour() * 60 + o2.getRappelTime().getSecondOfMinute());
                    }
                }
                long  result =  (tps1 -tps2);
                if(result < 0)
                {
                    return -1;
                }
                else if(result > 0 )
                {
                    return 1;
                }
                else
                {
                    return 0;
                }

            }
        };
        Collections.sort(liste, compareByDate);

        return liste;
    }

}